#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ $# -eq 0 ]; then
  echo "Usage: ./run.sh <mode> [extra_services...]"
  echo "Modes:"
  echo "  0: Install dev dependencies (java 21, gradle, pnpm)"
  echo "  1: Full prod (Kubernetes + all services)"
  echo "  2: Full except GitLab, run docker kubernetes"
  echo "  3: Full except GitLab, kubernetes; just using docker compose all services"
  echo "  4: Full except GitLab, kubernetes; just using docker compose some services via name (kibana, kafka, redis, oracle always run)"
  echo "  5: Just run kibana, kafka, redis, oracle (Docker Compose)"
  exit 1
fi

MODE=$1
shift

case "$MODE" in
  0)
    echo "=== Mode 0: Install dev dependencies (java 21, node 22, gradle, pnpm) ==="
    OS_TYPE="$(uname -s)"

    # --- Install Java 21 ---
    JAVA_MISSING=false
    if [ "$OS_TYPE" == "Darwin" ]; then
      if ! /usr/libexec/java_home -v 21 &> /dev/null; then JAVA_MISSING=true; fi
    else
      if ! java -version 2>&1 | grep -qE 'version "21|openjdk 21'; then JAVA_MISSING=true; fi
    fi

    if [ "$JAVA_MISSING" = true ]; then
      echo "Java 21 could not be found, installing..."
      if [ "$OS_TYPE" == "Darwin" ]; then
        if command -v brew &> /dev/null; then
          brew install openjdk@21
          echo "Note: You might need to add Java 21 to your PATH or symlink it as suggested by Homebrew output."
        else
          echo "Please install Homebrew (https://brew.sh/) first."
        fi
      elif [ "$OS_TYPE" == "Linux" ]; then
        if command -v apt-get &> /dev/null; then
          sudo apt-get update && sudo apt-get install -y openjdk-21-jdk
        else
          echo "No apt package manager found. Please install Java 21 manually."
        fi
      fi
    else
      echo "Java 21 is already installed."
    fi

    # --- Install Node 22 ---
    NODE_MISSING=false
    if ! command -v node &> /dev/null; then
      NODE_MISSING=true
    elif ! node -v | grep -q '^v22\.'; then
      echo "Current Node version is $(node -v), switching to Node 22..."
      NODE_MISSING=true
    fi

    if [ "$NODE_MISSING" = true ]; then
      if [ "$OS_TYPE" == "Darwin" ]; then
        if command -v brew &> /dev/null; then
          brew install node@22
          brew link --overwrite node@22
          export PATH="/usr/local/opt/node@22/bin:/opt/homebrew/opt/node@22/bin:$PATH"
        else
          echo "Please install Homebrew first."
        fi
      elif [ "$OS_TYPE" == "Linux" ]; then
        if command -v snap &> /dev/null; then
          sudo snap install node --channel=22/stable --classic
        elif command -v apt-get &> /dev/null; then
          if command -v curl &> /dev/null; then
            curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
            sudo apt-get install -y nodejs
          else
            echo "curl is required to install Node via apt. Please install curl."
          fi
        else
          echo "No apt or snap found. Please install Node 22 manually."
        fi
      fi
    else
      echo "Node 22 is already installed and active ($(node -v))."
    fi

    # --- Install Gradle ---
    if ! command -v gradle &> /dev/null; then
      echo "gradle could not be found, installing..."
      if [ "$OS_TYPE" == "Darwin" ]; then
        if command -v brew &> /dev/null; then
          brew install gradle
        else
          echo "Please install Homebrew first."
        fi
      elif [ "$OS_TYPE" == "Linux" ]; then
        if command -v apt-get &> /dev/null; then
          sudo apt-get update && sudo apt-get install -y gradle
        elif command -v snap &> /dev/null; then
          sudo snap install gradle --classic
        else
          echo "No apt or snap found. Please install gradle manually."
        fi
      fi
    else
      echo "gradle is already installed."
    fi

    # --- Install PNPM ---
    if ! command -v pnpm &> /dev/null; then
      echo "pnpm could not be found, installing..."
      if command -v npm &> /dev/null; then
        npm install -g pnpm
      elif [ "$OS_TYPE" == "Darwin" ] && command -v brew &> /dev/null; then
        brew install pnpm
      else
        curl -fsSL https://get.pnpm.io/install.sh | sh -
      fi
    else
      echo "pnpm is already installed."
    fi
    ;;
  1)
    echo "=== Mode 1: Full Prod ==="
    ./infra/scripts/00-install-tools.sh
    ./infra/scripts/01-create-cluster.sh
    export HELMFILE_ARGS=""
    ./infra/scripts/02-deploy-all.sh
    ;;
  2)
    echo "=== Mode 2: Full except GitLab ==="
    ./infra/scripts/00-install-tools.sh
    ./infra/scripts/01-create-cluster.sh
    export HELMFILE_ARGS="--selector name!=gitlab"
    ./infra/scripts/02-deploy-all.sh
    ;;
  3)
    echo "=== Mode 3: Docker Compose All Services ==="
    docker compose up -d
    ;;
  4)
    echo "=== Mode 4: Docker Compose Some Services ==="
    # Core services that must run
    CORE_SERVICES="oracle redis kafka elasticsearch kibana"
    EXTRA_SERVICES="$@"
    
    if [ -z "$EXTRA_SERVICES" ]; then
      echo "No extra services provided. To run just core services, you can also use mode 5."
    fi
    
    echo "Starting: $CORE_SERVICES $EXTRA_SERVICES"
    docker compose up -d $CORE_SERVICES $EXTRA_SERVICES
    ;;
  5)
    echo "=== Mode 5: Docker Compose Core Backing Services Only ==="
    CORE_SERVICES="oracle redis kafka elasticsearch kibana"
    echo "Starting: $CORE_SERVICES"
    docker compose up -d $CORE_SERVICES
    ;;
  *)
    echo "Invalid mode: $MODE"
    exit 1
    ;;
esac

echo "Done."
