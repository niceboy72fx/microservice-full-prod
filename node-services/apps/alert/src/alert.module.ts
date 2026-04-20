import { Module } from "@nestjs/common";
import { AlertController } from "./alert.controller";
import { AlertService } from "./alert.service";
import { GraphQLModule } from "@nestjs/graphql";
import { ApolloDriver, ApolloDriverConfig } from "@nestjs/apollo";

@Module({
  imports: [
    GraphQLModule.forRoot<ApolloDriverConfig>({
      driver: ApolloDriver,
      autoSchemaFile: true,
    }),
  ],
  controllers: [AlertController],
  providers: [AlertService],
})
export class AlertModule {}
