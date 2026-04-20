#!/bin/bash
set -euo pipefail

check() {
  local name=$1
  local url=$2

  if curl -sf "$url" >/dev/null 2>&1; then
    echo "[OK] $name"
  else
    echo "[FAIL] $name ($url)"
  fi
}

echo "=== Checking pods ==="
kubectl get pods -A | grep -v Running | grep -v Completed | grep -v NAME \
  && echo "WARNING: some pods not Running" || echo "[OK] All pods Running"

echo ""
echo "=== Checking endpoints ==="
check "MinIO console" "http://minio-console.trading.local"
check "Grafana" "http://grafana.trading.local/api/health"
check "Prometheus" "http://prometheus.trading.local/-/healthy"
check "Kibana" "http://kibana.trading.local/api/status"
check "GitLab" "http://gitlab.trading.local/-/health"
check "Rancher" "http://rancher.trading.local/ping"

echo ""
echo "=== Resource usage ==="
kubectl top nodes
kubectl top pods -A --sort-by=memory
