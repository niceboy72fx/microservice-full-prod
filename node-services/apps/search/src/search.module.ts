import { Module } from "@nestjs/common";
import { ConfigModule, ConfigService } from "@nestjs/config";
import { SearchController } from "./search.controller";
import { SearchService } from "./search.service";
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
  controllers: [SearchController],
  providers: [SearchService],
})
export class SearchModule {}
