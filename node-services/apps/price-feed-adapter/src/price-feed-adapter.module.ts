import { Module } from "@nestjs/common";
import { PriceFeedAdapterController } from "./price-feed-adapter.controller";
import { PriceFeedAdapterService } from "./price-feed-adapter.service";
import { GraphQLModule } from "@nestjs/graphql";
import { ApolloDriver, ApolloDriverConfig } from "@nestjs/apollo";

@Module({
  imports: [
    GraphQLModule.forRoot<ApolloDriverConfig>({
      driver: ApolloDriver,
      autoSchemaFile: true,
    }),
  ],
  controllers: [PriceFeedAdapterController],
  providers: [PriceFeedAdapterService],
})
export class PriceFeedAdapterModule {}
