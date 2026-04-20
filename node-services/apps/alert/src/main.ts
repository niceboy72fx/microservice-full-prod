import { NestFactory } from "@nestjs/core";
import { AlertModule } from "./alert.module";
import { Transport, MicroserviceOptions } from "@nestjs/microservices";
import { elasticsearchLogger } from "@app/shared-package/elasticsearch-logger";

async function bootstrap() {
  const app = await NestFactory.create(AlertModule, {
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
        groupId: "alert-consumer",
      },
    },
  });

  // Connect gRPC Microservice
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: "alert",
      protoPath: "path/to/alert.proto",
    },
  });

  await app.startAllMicroservices();
  await app.listen(process.env.PORT ?? 3000);
}
bootstrap();
