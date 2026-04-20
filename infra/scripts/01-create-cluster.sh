#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

check_linux_inotify_limits() {
  if [[ "$(uname -s)" != "Linux" ]]; then
    return
  fi

  local min_instances=1024
  local min_watches=524288
  local current_instances current_watches

  current_instances="$(sysctl -n fs.inotify.max_user_instances)"
  current_watches="$(sysctl -n fs.inotify.max_user_watches)"

  if (( current_instances < min_instances || current_watches < min_watches )); then
    cat <<EOF
Inotify limits are too low for this Kind cluster.
Current:
  fs.inotify.max_user_instances=$current_instances
  fs.inotify.max_user_watches=$current_watches
Required minimum:
  fs.inotify.max_user_instances=$min_instances
  fs.inotify.max_user_watches=$min_watches

Temporary fix:
  sudo sysctl -w fs.inotify.max_user_instances=$min_instances
  sudo sysctl -w fs.inotify.max_user_watches=$min_watches

Permanent fix:
  printf 'fs.inotify.max_user_instances=%s\nfs.inotify.max_user_watches=%s\n' $min_instances $min_watches | sudo tee /etc/sysctl.d/99-kind.conf
  sudo sysctl --system
EOF
    exit 1
  fi
}

check_linux_inotify_limits

kind create cluster --name trading --config "$REPO_ROOT/infra/kind-config.yaml"

kubectl apply -f https://raw.githubusercontent.com/rancher/local-path-provisioner/v0.0.34/deploy/local-path-storage.yaml
kubectl apply -f "$REPO_ROOT/infra/manifests/namespaces.yaml"
kubectl apply -f "$REPO_ROOT/infra/manifests/storage-class.yaml"

kubectl wait --for=condition=Ready node --all --timeout=120s

echo ""
echo "=== Cluster Info ==="
kubectl cluster-info
kubectl get nodes -o wide
