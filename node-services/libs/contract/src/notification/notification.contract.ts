export interface NotificationMessageContract {
  id: string;
  recipient: string;
  channel: 'EMAIL' | 'SMS' | 'PUSH';
  payload: Record<string, unknown>;
}
