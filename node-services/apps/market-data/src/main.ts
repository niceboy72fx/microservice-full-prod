import { NestFactory } from "@nestjs/core";
import { MarketDataModule } from "./market-data.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/shared-package/elasticsearch-logger";

async function bootstrap() {
  const app = await NestFactory.create(MarketDataModule, {
    logger: elasticsearchLogger,
  });

  // Connect Kafka Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.KAFKA,
    options: {
      client: {
        brokers: [process.env.KAFKA_BROKER || "localhost:9092"],
      },
      consumer: {
        groupId: "market-data-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "market-data",
      protoPath: "path/to/market-data.proto",
    },
  });

  await app.startAllMicroservices();
  await app.listen(process.env.PORT ?? 3000);
}
bootstrap();
