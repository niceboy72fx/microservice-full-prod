import { Module } from "@nestjs/common";
import { ConfigModule, ConfigService } from "@nestjs/config";
import { PriceFeedAdapterController } from "./price-feed-adapter.controller";
import { PriceFeedAdapterService } from "./price-feed-adapter.service";
import { GraphQLModule } from "@nestjs/graphql";
import { ApolloDriver, ApolloDriverConfig } from "@nestjs/apollo";
import { envValidationSchema } from "@app/shared-package";

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      validationSchema: envValidationSchema,
    }),
    GraphQLModule.forRootAsync<ApolloDriverConfig>({
      driver: ApolloDriver,
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => ({
        autoSchemaFile: true,
        playground: configService.get<boolean>('GRAPHQL_PLAYGROUND'),
      }),
    }),
  ],
  controllers: [PriceFeedAdapterController],
  providers: [PriceFeedAdapterService],
})
export class PriceFeedAdapterModule {}
