#!/bin/bash
set -euo pipefail

if ! command -v tailscale >/dev/null 2>&1; then
  echo "tailscale is not installed"
  exit 1
fi

if ! command -v getent >/dev/null 2>&1; then
  echo "getent is required"
  exit 1
fi

if [[ ${EUID:-$(id -u)} -ne 0 ]]; then
  echo "Run as root: sudo $0"
  exit 1
fi

TAILSCALE_IP="$(tailscale ip -4 | head -n 1)"
if [[ -z "$TAILSCALE_IP" ]]; then
  echo "No Tailscale IPv4 found. Make sure Tailscale is connected."
  exit 1
fi

LOCAL_HOSTS_LINE_1="127.0.0.1 minio.trading.local minio-console.trading.local"
LOCAL_HOSTS_LINE_2="127.0.0.1 grafana.trading.local prometheus.trading.local"
LOCAL_HOSTS_LINE_3="127.0.0.1 kibana.trading.local gitlab.trading.local"
LOCAL_HOSTS_LINE_4="127.0.0.1 rancher.trading.local"

append_if_missing() {
  local line="$1"
  if ! grep -Fqx "$line" /etc/hosts; then
    printf '%s\n' "$line" >> /etc/hosts
  fi
}

append_if_missing "$LOCAL_HOSTS_LINE_1"
append_if_missing "$LOCAL_HOSTS_LINE_2"
append_if_missing "$LOCAL_HOSTS_LINE_3"
append_if_missing "$LOCAL_HOSTS_LINE_4"

# Direct hostname resolution over Tailscale works better here than serve/funnel
# because ingress routes strictly on rancher.trading.local and gitlab.trading.local.
tailscale funnel reset >/dev/null 2>&1 || true
tailscale serve reset >/dev/null 2>&1 || true

cat <<EOF
Host setup complete.

Tailscale IP: $TAILSCALE_IP

This host now resolves local ingress names via /etc/hosts.
Tailscale serve/funnel config was reset to avoid hostname mismatch with ingress.

Client machines should map these names to $TAILSCALE_IP:
  rancher.trading.local
  gitlab.trading.local
  registry.trading.local
  kas.trading.local

Server checks:
  getent hosts rancher.trading.local gitlab.trading.local
  curl -k https://rancher.trading.local/ping
EOF
