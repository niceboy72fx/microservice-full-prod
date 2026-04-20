import { Controller, Get } from '@nestjs/common';
import { MarketDataService } from './market-data.service';

@Controller()
export class MarketDataController {
  constructor(private readonly marketDataService: MarketDataService) {}

  @Get()
  getHello(): string {
    return this.marketDataService.getHello();
  }
}
