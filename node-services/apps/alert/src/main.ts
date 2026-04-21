import { NestFactory } from "@nestjs/core";
import { AlertModule } from "./alert.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/shared-package/elasticsearch-logger";
import { ConfigService } from "@nestjs/config";
import { join } from "path";

async function bootstrap() {
  const app = await NestFactory.create(AlertModule, {
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
        groupId: "alert-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "alert",
      protoPath: join(process.cwd(), "libs/shared-package/src/proto/alert.proto"),
    },
  });

  await app.startAllMicroservices();
  await app.listen(configService.get<number>("PORT") ?? 3000);
}
bootstrap();
