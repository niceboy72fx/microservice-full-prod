import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { ProjectTemplateController } from './project-template.controller';
import { ProjectTemplateService } from './project-template.service';
import { envValidationSchema } from '@app/validation';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      validationSchema: envValidationSchema,
    }),
  ],
  controllers: [ProjectTemplateController],
  providers: [ProjectTemplateService],
})
export class ProjectTemplateModule {}
