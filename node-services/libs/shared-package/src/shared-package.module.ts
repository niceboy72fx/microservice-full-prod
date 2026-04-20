import { Module } from '@nestjs/common';
import { SharedPackageService } from './shared-package.service';

@Module({
  providers: [SharedPackageService],
  exports: [SharedPackageService],
})
export class SharedPackageModule {}
