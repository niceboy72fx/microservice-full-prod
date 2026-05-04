import { NestFactory } from "@nestjs/core";
import { PriceFeedAdapterModule } from "./price-feed-adapter.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/observability";
import { ConfigService } from "@nestjs/config";
import { join } from "path";

async function bootstrap() {
  const app = await NestFactory.create(PriceFeedAdapterModule, {
    logger: elasticsearchLogger,
  });

  const configService = app.get(ConfigService);

  // Connect Kafka Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.KAFKA,
    options: {
      client: {
        brokers: [configService.get<string>("KAFKA_BROKER") || "localhost:9092"],
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
      package: "price_feed_adapter",
      protoPath: join(process.cwd(), "libs/grpc/src/proto/price-feed-adapter.proto"),
    },
  });

  await app.startAllMicroservices();
  await app.listen(configService.get<number>("PORT") ?? 3000);
}
bootstrap();
