import { Module } from '@nestjs/common';
import { ProjectTemplateController } from './project-template.controller';
import { ProjectTemplateService } from './project-template.service';

@Module({
  imports: [],
  controllers: [ProjectTemplateController],
  providers: [ProjectTemplateService],
})
export class ProjectTemplateModule {}
