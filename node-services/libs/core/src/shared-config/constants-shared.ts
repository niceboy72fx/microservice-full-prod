import { readFileSync, existsSync } from "fs";
import { resolve } from "path";

export type ErrorCodeEntry = {
  code: number;
  message: string;
};

export type SharedConstants = {
  httpClientCode: Record<string, ErrorCodeEntry>;
  serviceCallCode: Record<string, ErrorCodeEntry>;
  commonConstant: Record<string, number | string | boolean>;
  roleConstant: Record<string, number | string | boolean>;
};

function resolveConstantsPath(): string {
  const override = process.env.SHARED_CONSTANTS_PATH;
  if (override && existsSync(override)) return resolve(override);

  const candidates = [
    resolve(process.cwd(), "shared-config/constants-shared.json"),
    resolve(process.cwd(), "../shared-config/constants-shared.json"),
    resolve(process.cwd(), "../../shared-config/constants-shared.json"),
  ];

  for (const p of candidates) {
    if (existsSync(p)) return p;
  }

  throw new Error("shared-config/constants-shared.json not found. Set SHARED_CONSTANTS_PATH");
}

export function loadSharedConstants(): SharedConstants {
  const file = resolveConstantsPath();
  const raw = readFileSync(file, "utf8");
  return JSON.parse(raw) as SharedConstants;
}

export function getServiceCallCode(key: string): ErrorCodeEntry {
  const constants = loadSharedConstants();
  const found = constants.serviceCallCode[key];
  if (!found) {
    throw new Error(`Missing serviceCallCode key: ${key}`);
  }
  return found;
}

export function getHttpClientCode(key: string): ErrorCodeEntry {
  const constants = loadSharedConstants();
  const found = constants.httpClientCode[key];
  if (!found) {
    throw new Error(`Missing httpClientCode key: ${key}`);
  }
  return found;
}
