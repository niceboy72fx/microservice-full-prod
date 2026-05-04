import { WinstonModule } from "nest-winston";
import * as winston from "winston";
import { ElasticsearchTransport } from "winston-elasticsearch";
import { getElasticsearchConfig, getLoggingConfig } from "@app/core";

const elastic = getElasticsearchConfig();
const logging = getLoggingConfig();

export const elasticsearchLogger = WinstonModule.createLogger({
  transports: [
    new winston.transports.Console({
      level: logging.level,
      format: winston.format.combine(winston.format.timestamp(), winston.format.json()),
    }),
    new ElasticsearchTransport({
      level: logging.level,
      clientOpts: {
        node: elastic.node,
        auth:
          elastic.username && elastic.password
            ? { username: elastic.username, password: elastic.password }
            : undefined,
        requestTimeout: elastic.requestTimeoutMs,
        maxRetries: elastic.maxRetries,
        sniffOnStart: elastic.sniffOnStart,
      },
      indexPrefix: logging.indexPrefix,
    }),
  ],
});
