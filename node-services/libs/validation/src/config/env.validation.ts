import * as Joi from 'joi';

export const envValidationSchema = Joi.object({
  PORT: Joi.number().default(3000),
  KAFKA_BROKER: Joi.string().required(),
  ELASTICSEARCH_NODE: Joi.string().uri().required(),
  GRAPHQL_PLAYGROUND: Joi.boolean().default(false),
});
