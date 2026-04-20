import { Injectable } from '@nestjs/common';

@Injectable()
export class MarketDataService {
  getHello(): string {
    return 'Hello World!';
  }
}
