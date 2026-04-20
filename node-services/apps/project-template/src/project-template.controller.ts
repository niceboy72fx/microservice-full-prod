import { Controller, Get } from '@nestjs/common';
import { ProjectTemplateService } from './project-template.service';

@Controller()
export class ProjectTemplateController {
  constructor(private readonly projectTemplateService: ProjectTemplateService) {}

  @Get()
  getHello(): string {
    return this.projectTemplateService.getHello();
  }
}
