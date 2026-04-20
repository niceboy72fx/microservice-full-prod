#!/bin/bash
set -euo pipefail

OS="$(uname -s)"

need_cmd() {
  command -v "$1" >/dev/null 2>&1
}

version_ge() {
  local current=$1
  local minimum=$2
  [[ "$(printf '%s\n%s\n' "$minimum" "$current" | sort -V | head -n1)" == "$minimum" ]]
}

install_brew_pkg() {
  local pkg=$1
  if ! brew list "$pkg" >/dev/null 2>&1; then
    brew install "$pkg"
  fi
}

install_apt_pkg() {
  local pkg=$1
  if ! dpkg -s "$pkg" >/dev/null 2>&1; then
    sudo apt-get install -y "$pkg"
  fi
}

ensure_helm() {
  local min_version="3.15.0"
  local current_version=""

  if need_cmd helm; then
    current_version="$(helm version --template '{{ .Version }}' 2>/dev/null | sed 's/^v//')"
  fi

  if [[ -n "$current_version" ]] && version_ge "$current_version" "$min_version"; then
    return
  fi

  curl -fsSL https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
}

ensure_helm_diff() {
  if helm diff version >/dev/null 2>&1; then
    return
  fi

  if helm plugin list 2>/dev/null | awk '{print $1}' | grep -qx diff; then
    helm plugin uninstall diff || true
  fi

  helm plugin install https://github.com/databus23/helm-diff
}

ensure_linux_docker() {
  if need_cmd docker; then
    echo "docker already installed; skipping docker package installation."
    return
  fi

  if dpkg -s docker-ce >/dev/null 2>&1 || dpkg -s docker.io >/dev/null 2>&1; then
    echo "docker package already present; skipping docker package installation."
    return
  fi

  install_apt_pkg docker.io
}

if [[ "$OS" == "Darwin" ]]; then
  if ! need_cmd brew; then
    echo "Homebrew is required on macOS: https://brew.sh"
    exit 1
  fi

  install_brew_pkg docker
  install_brew_pkg kind
  install_brew_pkg kubectl
  install_brew_pkg helm
  install_brew_pkg helmfile
  install_brew_pkg jq
  install_brew_pkg curl
  install_brew_pkg gettext
  brew link --force gettext >/dev/null 2>&1 || true
else
  sudo apt-get update
  ensure_linux_docker
  install_apt_pkg jq
  install_apt_pkg curl
  install_apt_pkg gettext-base

  if ! need_cmd kind; then
    curl -Lo /tmp/kind https://kind.sigs.k8s.io/dl/v0.23.0/kind-linux-amd64
    chmod +x /tmp/kind
    sudo mv /tmp/kind /usr/local/bin/kind
  fi

  if ! need_cmd kubectl; then
    curl -Lo /tmp/kubectl https://dl.k8s.io/release/v1.30.1/bin/linux/amd64/kubectl
    chmod +x /tmp/kubectl
    sudo mv /tmp/kubectl /usr/local/bin/kubectl
  fi

  ensure_helm

  if ! need_cmd helmfile; then
    curl -Lo /tmp/helmfile.tar.gz https://github.com/helmfile/helmfile/releases/download/v0.157.0/helmfile_0.157.0_linux_amd64.tar.gz
    tar -C /tmp -xzf /tmp/helmfile.tar.gz helmfile
    sudo mv /tmp/helmfile /usr/local/bin/helmfile
  fi
fi

ensure_helm_diff

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx >/dev/null 2>&1 || true
helm repo add jetstack https://charts.jetstack.io >/dev/null 2>&1 || true
helm repo add elastic https://helm.elastic.co >/dev/null 2>&1 || true
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts >/dev/null 2>&1 || true
helm repo add minio https://charts.min.io >/dev/null 2>&1 || true
helm repo add gitlab https://charts.gitlab.io >/dev/null 2>&1 || true
helm repo add rancher-stable https://releases.rancher.com/server-charts/stable >/dev/null 2>&1 || true
helm repo update

echo "Tool installation complete."
