import { Test, TestingModule } from '@nestjs/testing';
import { PriceFeedAdapterController } from './price-feed-adapter.controller';
import { PriceFeedAdapterService } from './price-feed-adapter.service';

describe('PriceFeedAdapterController', () => {
  let priceFeedAdapterController: PriceFeedAdapterController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [PriceFeedAdapterController],
      providers: [PriceFeedAdapterService],
    }).compile();

    priceFeedAdapterController = app.get<PriceFeedAdapterController>(PriceFeedAdapterController);
  });

  describe('root', () => {
    it('should return "Hello World!"', () => {
      expect(priceFeedAdapterController.getHello()).toBe('Hello World!');
    });
  });
});
