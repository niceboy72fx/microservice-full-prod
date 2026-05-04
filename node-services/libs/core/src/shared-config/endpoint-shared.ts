import { existsSync, readFileSync } from "fs";
import { resolve } from "path";

export type EndpointConfig = {
  baseUrl: string;
  graphql: string;
};

export type SharedEndpoints = Record<string, EndpointConfig>;

function resolveEndpointPath(): string {
  const override = process.env.SHARED_ENDPOINT_PATH;
  if (override && existsSync(override)) return resolve(override);

  const candidates = [
    resolve(process.cwd(), "shared-config/endpoint-shared.json"),
    resolve(process.cwd(), "shared-config/endpoint=shared.json"),
    resolve(process.cwd(), "../shared-config/endpoint-shared.json"),
    resolve(process.cwd(), "../shared-config/endpoint=shared.json"),
    resolve(process.cwd(), "../../shared-config/endpoint-shared.json"),
    resolve(process.cwd(), "../../shared-config/endpoint=shared.json"),
  ];

  for (const c of candidates) if (existsSync(c)) return c;
  throw new Error("endpoint-shared.json not found. Set SHARED_ENDPOINT_PATH");
}

export function loadSharedEndpoints(): SharedEndpoints {
  return JSON.parse(readFileSync(resolveEndpointPath(), "utf8")) as SharedEndpoints;
}

export function getEndpoint(serviceKey: string): EndpointConfig {
  const endpoints = loadSharedEndpoints();
  const value = endpoints[serviceKey];
  if (!value) throw new Error(`Unknown endpoint key: ${serviceKey}`);
  return value;
}
