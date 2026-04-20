# Trading Platform Infra

## 1. Prerequisites

- Docker 24+ with at least 12 GB RAM available
- 50 GB free disk space
- Linux or macOS on amd64
- `kind`, `kubectl`, `helm`, and `helmfile` installed by the provided bootstrap script

## 2. Quick start

```bash
./infra/scripts/00-install-tools.sh
./infra/scripts/01-create-cluster.sh
set -a; source infra/.env.example; set +a; ./infra/scripts/02-deploy-all.sh
```

## 3. Access URLs

| Service | URL |
| --- | --- |
| GitLab | http://gitlab.trading.local |
| Grafana | http://grafana.trading.local |
| Prometheus | http://prometheus.trading.local |
| Kibana | http://kibana.trading.local |
| MinIO API | http://minio.trading.local |
| MinIO Console | http://minio-console.trading.local |
| Rancher | http://rancher.trading.local |

## 4. How to add a new service to the stack

1. Create a new values file or local chart under `infra/helm/`.
2. Add the release to `infra/helmfile.yaml` and declare `needs` dependencies so ordering stays deterministic.
3. Set explicit CPU and memory requests and limits for every workload the chart creates.
4. Use `standard-rwo` for persistent workloads so PVCs land on the local-path provisioner.
5. Expose the service through ingress only after confirming it has readiness and liveness probes.

## 5. Troubleshooting

- Elasticsearch `CrashLoopBackOff`: confirm the privileged init container can apply `vm.max_map_count=262144` on the worker node.
- GitLab takes too long: expected on kind; wait up to 10 minutes before treating startup as failed.
- MinIO connection refused: verify the service is in namespace `infra`, not `infra-system`.
- `OOMKilled`: identify the pod with `kubectl get pods -A` or `kubectl top pods -A --sort-by=memory`, then raise that workload's memory limit in the matching values file and redeploy.

## 6. Teardown

```bash
kind delete cluster --name trading
```
