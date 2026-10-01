export type CommunicationAppName = 'WhatsApp' | 'Messenger' | 'Telegram' | 'Signal' | 'Phone';

export interface IncomingCallState {
  callerName: string;
  appName: CommunicationAppName;
  callType: 'voice' | 'video';
  timestamp: number;
}

export interface ActiveCallState {
  callerName: string;
  appName: CommunicationAppName;
  callType: 'voice' | 'video';
  durationSeconds: number;
  isMuted: boolean;
  isSpeakerOn: boolean;
  liveTranscript: string[];
}
