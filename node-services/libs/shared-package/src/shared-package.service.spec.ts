import { Test, TestingModule } from '@nestjs/testing';
import { SharedPackageService } from './shared-package.service';

describe('SharedPackageService', () => {
  let service: SharedPackageService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [SharedPackageService],
    }).compile();

    service = module.get<SharedPackageService>(SharedPackageService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
