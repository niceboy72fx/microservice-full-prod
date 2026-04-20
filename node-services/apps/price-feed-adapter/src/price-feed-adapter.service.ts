import { Injectable } from '@nestjs/common';

@Injectable()
export class PriceFeedAdapterService {
  getHello(): string {
    return 'Hello World!';
  }
}
