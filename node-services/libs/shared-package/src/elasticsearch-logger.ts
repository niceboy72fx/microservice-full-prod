import { WinstonModule } from 'nest-winston';
import * as winston from 'winston';
import { ElasticsearchTransport } from 'winston-elasticsearch';
import * as dotenv from 'dotenv';

dotenv.config();
export const elasticsearchLogger = WinstonModule.createLogger({
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.json(),
      ),
    }),
    new ElasticsearchTransport({
      level: 'info',
      clientOpts: { node: process.env.ELASTICSEARCH_NODE || 'http://localhost:9200' },
      indexPrefix: 'microservices-logs',
    }),
  ],
});
