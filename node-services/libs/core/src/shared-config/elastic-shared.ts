import { existsSync, readFileSync } from "fs";
import { resolve } from "path";

export type ElasticSharedConfig = {
  elasticsearch: {
    node: string;
    username: string;
    password: string;
    requestTimeoutMs: number;
    maxRetries: number;
    sniffOnStart: boolean;
  };
  kibana: {
    url: string;
  };
  logging: {
    indexPrefix: string;
    level: string;
  };
};

function resolveElasticPath(): string {
  const override = process.env.SHARED_ELASTIC_PATH;
  if (override && existsSync(override)) return resolve(override);

  const candidates = [
    resolve(process.cwd(), "shared-config/elastic-shared.json"),
    resolve(process.cwd(), "../shared-config/elastic-shared.json"),
    resolve(process.cwd(), "../../shared-config/elastic-shared.json"),
  ];

  for (const c of candidates) {
    if (existsSync(c)) return c;
  }

  throw new Error("elastic-shared.json not found. Set SHARED_ELASTIC_PATH");
}

export function loadElasticSharedConfig(): ElasticSharedConfig {
  return JSON.parse(readFileSync(resolveElasticPath(), "utf8")) as ElasticSharedConfig;
}

export function getElasticsearchConfig() {
  return loadElasticSharedConfig().elasticsearch;
}

export function getKibanaConfig() {
  return loadElasticSharedConfig().kibana;
}

export function getLoggingConfig() {
  return loadElasticSharedConfig().logging;
}
