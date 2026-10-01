export interface CommunicationAppAdapter {
  appName: string;
  packageName: string;
  sendMessage(recipient: string, message: string): Promise<boolean>;
  answerCall(): Promise<boolean>;
  rejectCall(): Promise<boolean>;
}
