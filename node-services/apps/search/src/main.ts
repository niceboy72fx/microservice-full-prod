import { NestFactory } from "@nestjs/core";
import { SearchModule } from "./search.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/shared-package/elasticsearch-logger";
import { ConfigService } from "@nestjs/config";
import { join } from "path";

async function bootstrap() {
  const app = await NestFactory.create(SearchModule, {
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
        groupId: "search-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "search",
      protoPath: join(process.cwd(), "libs/shared-package/src/proto/search.proto"),
    },
  });

  await app.startAllMicroservices();
  await app.listen(configService.get<number>("PORT") ?? 3000);
}
bootstrap();
