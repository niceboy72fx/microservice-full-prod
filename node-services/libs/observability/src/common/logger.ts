import { getLoggingConfig } from "@app/core";

export type LogLevel = "trace" | "debug" | "info" | "warn" | "error";

export type LogFields = Record<string, unknown>;

export interface CommonLogger {
  trace(message: string, fields?: LogFields): void;
  debug(message: string, fields?: LogFields): void;
  info(message: string, fields?: LogFields): void;
  warn(message: string, fields?: LogFields): void;
  error(message: string, fields?: LogFields): void;
}

const order: Record<LogLevel, number> = {
  trace: 10,
  debug: 20,
  info: 30,
  warn: 40,
  error: 50,
};

function resolveLevel(): LogLevel {
  const level = (getLoggingConfig().level || "info").toLowerCase();
  if (level in order) return level as LogLevel;
  return "info";
}

export function createCommonLogger(serviceName: string): CommonLogger {
  const threshold = resolveLevel();

  const write = (level: LogLevel, message: string, fields: LogFields = {}) => {
    if (order[level] < order[threshold]) return;
    const payload = {
      timestamp: new Date().toISOString(),
      service: serviceName,
      level,
      message,
      ...fields,
    };
    // keep stdout json for log shippers
    process.stdout.write(`${JSON.stringify(payload)}\n`);
  };

  return {
    trace: (message, fields) => write("trace", message, fields),
    debug: (message, fields) => write("debug", message, fields),
    info: (message, fields) => write("info", message, fields),
    warn: (message, fields) => write("warn", message, fields),
    error: (message, fields) => write("error", message, fields),
  };
}
