import { existsSync, readFileSync } from "fs";
import { resolve } from "path";

export type SharedTopics = Record<string, Record<string, string>>;

function resolveTopicPath(): string {
  const override = process.env.SHARED_TOPIC_PATH;
  if (override && existsSync(override)) return resolve(override);

  const candidates = [
    resolve(process.cwd(), "shared-config/topic-shared.json"),
    resolve(process.cwd(), "../shared-config/topic-shared.json"),
    resolve(process.cwd(), "../../shared-config/topic-shared.json"),
  ];

  for (const c of candidates) if (existsSync(c)) return c;
  throw new Error("topic-shared.json not found. Set SHARED_TOPIC_PATH");
}

export function loadSharedTopics(): SharedTopics {
  return JSON.parse(readFileSync(resolveTopicPath(), "utf8")) as SharedTopics;
}

export function getTopic(domain: string, key: string): string {
  const topics = loadSharedTopics();
  const domainTopics = topics[domain];
  if (!domainTopics) throw new Error(`Unknown topic domain: ${domain}`);
  const value = domainTopics[key];
  if (!value) throw new Error(`Unknown topic key: ${domain}.${key}`);
  return value;
}
