#!/bin/bash
set -euo pipefail

if [[ $# -lt 1 || $# -gt 2 ]]; then
  cat <<'EOF'
Usage:
  sudo ./infra/scripts/05-setup-client-hosts.sh <server-ip> [name]

Example:
  sudo ./infra/scripts/05-setup-client-hosts.sh 100.82.73.50
  sudo ./infra/scripts/05-setup-client-hosts.sh 192.168.0.6 lan
EOF
  exit 1
fi

if [[ ${EUID:-$(id -u)} -ne 0 ]]; then
  echo "Run as root: sudo $0 <server-ip> [name]"
  exit 1
fi

SERVER_IP="$1"
PROFILE="${2:-tailscale}"

case "$PROFILE" in
  tailscale|lan)
    ;;
  *)
    echo "Unsupported profile: $PROFILE"
    echo "Use: tailscale or lan"
    exit 1
    ;;
esac

RANCHER_LINE="$SERVER_IP rancher.trading.local"
GITLAB_LINE="$SERVER_IP gitlab.trading.local registry.trading.local kas.trading.local"
OBSERVE_LINE="$SERVER_IP grafana.trading.local prometheus.trading.local kibana.trading.local"
MINIO_LINE="$SERVER_IP minio.trading.local minio-console.trading.local"

append_if_missing() {
  local line="$1"
  if ! grep -Fqx "$line" /etc/hosts; then
    printf '%s\n' "$line" >> /etc/hosts
  fi
}

append_if_missing "$RANCHER_LINE"
append_if_missing "$GITLAB_LINE"
append_if_missing "$OBSERVE_LINE"
append_if_missing "$MINIO_LINE"

cat <<EOF
Client host mapping added.

Profile: $PROFILE
Server IP: $SERVER_IP

Configured hostnames:
  rancher.trading.local
  gitlab.trading.local
  registry.trading.local
  kas.trading.local
  grafana.trading.local
  prometheus.trading.local
  kibana.trading.local
  minio.trading.local
  minio-console.trading.local

Checks:
  getent hosts rancher.trading.local gitlab.trading.local
  curl -k https://rancher.trading.local/ping
  curl -I http://grafana.trading.local

Notes:
  Rancher should work now.
  GitLab hostname routing will work, but the app is still unhealthy until its backend image pull issue is fixed.
EOF
