import { Test, TestingModule } from '@nestjs/testing';
import { ProjectTemplateController } from './project-template.controller';
import { ProjectTemplateService } from './project-template.service';

describe('ProjectTemplateController', () => {
  let projectTemplateController: ProjectTemplateController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [ProjectTemplateController],
      providers: [ProjectTemplateService],
    }).compile();

    projectTemplateController = app.get<ProjectTemplateController>(ProjectTemplateController);
  });

  describe('root', () => {
    it('should return "Hello World!"', () => {
      expect(projectTemplateController.getHello()).toBe('Hello World!');
    });
  });
});
