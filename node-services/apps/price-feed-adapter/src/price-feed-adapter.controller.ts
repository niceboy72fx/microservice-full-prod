import { Controller, Get } from '@nestjs/common';
import { PriceFeedAdapterService } from './price-feed-adapter.service';

@Controller()
export class PriceFeedAdapterController {
  constructor(private readonly priceFeedAdapterService: PriceFeedAdapterService) {}

  @Get()
  getHello(): string {
    return this.priceFeedAdapterService.getHello();
  }
}
