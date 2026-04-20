import { NestFactory } from "@nestjs/core";
import { PriceFeedAdapterModule } from "./price-feed-adapter.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/shared-package/elasticsearch-logger";

async function bootstrap() {
  const app = await NestFactory.create(PriceFeedAdapterModule, {
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
        groupId: "price-feed-adapter-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "price-feed-adapter",
      protoPath: "path/to/price-feed-adapter.proto",
    },
  });

  await app.startAllMicroservices();
  await app.listen(process.env.PORT ?? 3000);
}
bootstrap();
