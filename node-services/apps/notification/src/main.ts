import { NestFactory } from "@nestjs/core";
import { NotificationModule } from "./notification.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/observability";
import { ConfigService } from "@nestjs/config";
import { join } from "path";

async function bootstrap() {
  const app = await NestFactory.create(NotificationModule, {
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
        groupId: "notification-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "notification",
      protoPath: join(process.cwd(), "libs/grpc/src/proto/notification.proto"),
    },
  });

  await app.startAllMicroservices();
  await app.listen(configService.get<number>("PORT") ?? 3000);
}
bootstrap();
