#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

check_helm_diff() {
  if ! helm diff version >/dev/null 2>&1; then
    cat <<'EOF'
Helm diff is not runnable. Your Helm binary or helm-diff plugin installation is broken.

Recommended fix:
  ./00-install-tools.sh

If that still leaves an old user-local Helm first in PATH, upgrade Helm explicitly:
  curl -fsSL https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

Then rerun:
  ./02-deploy-all.sh
EOF
    exit 1
  fi
}

if [[ -f "$REPO_ROOT/infra/.env" ]]; then
  set -a
  # Load deployment secrets from the repo-local env file when available.
  source "$REPO_ROOT/infra/.env"
  set +a
fi

check_helm_diff

: "${MINIO_ROOT_USER:?Need MINIO_ROOT_USER}"
: "${MINIO_ROOT_PASSWORD:?Need MINIO_ROOT_PASSWORD}"
: "${GRAFANA_PASSWORD:?Need GRAFANA_PASSWORD}"
: "${GITLAB_ROOT_PASSWORD:?Need GITLAB_ROOT_PASSWORD}"
: "${RANCHER_BOOTSTRAP_PASSWORD:?Need RANCHER_BOOTSTRAP_PASSWORD}"

echo "Add these to /etc/hosts:"
echo "  127.0.0.1 minio.trading.local minio-console.trading.local"
echo "  127.0.0.1 grafana.trading.local prometheus.trading.local"
echo "  127.0.0.1 kibana.trading.local gitlab.trading.local"
echo "  127.0.0.1 rancher.trading.local"

helmfile --file "$REPO_ROOT/infra/helmfile.yaml" apply ${HELMFILE_ARGS:-}

kubectl apply -f "$REPO_ROOT/infra/manifests/external-nodeports.yaml"

if [[ "${HELMFILE_ARGS:-}" != *"--selector name!=gitlab"* ]]; then
  echo "Waiting for GitLab... (up to 10min)"
  kubectl wait --for=condition=Ready pod \
    -l app=webservice -n gitlab --timeout=600s
else
  echo "Skipping GitLab deployment wait condition..."
fi

echo ""
echo "=== DONE ==="
echo "GitLab:     http://gitlab.trading.local  (root / $GITLAB_ROOT_PASSWORD)"
echo "GitLab IP:  http://SERVER_IP:30081"
echo "Grafana:    http://grafana.trading.local"
echo "Grafana IP: http://SERVER_IP:30082"
echo "Kibana:     http://kibana.trading.local"
echo "MinIO:      http://minio-console.trading.local"
echo "Prometheus: http://prometheus.trading.local"
echo "Rancher:    http://rancher.trading.local"
echo "Rancher IP: http://SERVER_IP:30083"
