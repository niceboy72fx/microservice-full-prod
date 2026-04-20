import { Injectable } from '@nestjs/common';

@Injectable()
export class ProjectTemplateService {
  getHello(): string {
    return 'Hello World!';
  }
}
