import { NestFactory } from "@nestjs/core";
import { NotificationModule } from "./notification.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/shared-package/elasticsearch-logger";

async function bootstrap() {
  const app = await NestFactory.create(NotificationModule, {
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
        groupId: "notification-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "notification",
      protoPath: "path/to/notification.proto",
    },
  });

  await app.startAllMicroservices();
  await app.listen(process.env.PORT ?? 3000);
}
bootstrap();
