import { NestFactory } from '@nestjs/core';
import { ProjectTemplateModule } from './project-template.module';

async function bootstrap() {
  const app = await NestFactory.create(ProjectTemplateModule);
  await app.listen(process.env.port ?? 3000);
}
bootstrap();
