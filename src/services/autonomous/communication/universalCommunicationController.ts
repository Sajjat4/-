import { IncomingCallState, ActiveCallState, CommunicationAppName } from './types';
import { WhatsAppAdapter } from './adapters/WhatsAppAdapter';
import { AccessibilityController } from '../accessibilityController';

export class UniversalCommunicationController {
  private static instance: UniversalCommunicationController;
  private incomingCallSubscribers: Array<(call: IncomingCallState | null) => void> = [];
  private activeCallSubscribers: Array<(call: ActiveCallState | null) => void> = [];
  private currentIncomingCall: IncomingCallState | null = null;
  private currentActiveCall: ActiveCallState | null = null;
  private callTimer: any = null;
  private accessibility = AccessibilityController.getInstance();

  private constructor() {}

  public static getInstance(): UniversalCommunicationController {
    if (!UniversalCommunicationController.instance) {
      UniversalCommunicationController.instance = new UniversalCommunicationController();
    }
    return UniversalCommunicationController.instance;
  }

  public subscribeIncomingCall(cb: (call: IncomingCallState | null) => void): () => void {
    this.incomingCallSubscribers.push(cb);
    cb(this.currentIncomingCall);
    return () => {
      this.incomingCallSubscribers = this.incomingCallSubscribers.filter((s) => s !== cb);
    };
  }

  public subscribeActiveCall(cb: (call: ActiveCallState | null) => void): () => void {
    this.activeCallSubscribers.push(cb);
    cb(this.currentActiveCall);
    return () => {
      this.activeCallSubscribers = this.activeCallSubscribers.filter((s) => s !== cb);
    };
  }

  public triggerSimulatedIncomingCall(callerName: string = 'তানভীর আহমেদ', appName: CommunicationAppName = 'WhatsApp') {
    this.currentIncomingCall = {
      callerName,
      appName,
      callType: 'voice',
      timestamp: Date.now(),
    };
    this.notifyIncomingCall();
  }

  public async answerCall(): Promise<void> {
    if (!this.currentIncomingCall) return;
    const incoming = this.currentIncomingCall;
    this.currentIncomingCall = null;
    this.notifyIncomingCall();

    this.currentActiveCall = {
      callerName: incoming.callerName,
      appName: incoming.appName,
      callType: incoming.callType,
      durationSeconds: 0,
      isMuted: false,
      isSpeakerOn: true,
      liveTranscript: ['হ্যালো! আসসালামু আলাইকুম। কেমন আছেন?'],
    };
    this.notifyActiveCall();

    // Start timer
    if (this.callTimer) clearInterval(this.callTimer);
    this.callTimer = setInterval(() => {
      if (this.currentActiveCall) {
        const nextSec = this.currentActiveCall.durationSeconds + 1;
        const newTranscript = [...this.currentActiveCall.liveTranscript];
        if (nextSec === 3) {
          newTranscript.push('আমি MYRA Autonomous Agent এর মাধ্যমে আপনার সাথে যুক্ত আছি।');
        }
        this.currentActiveCall = {
          ...this.currentActiveCall,
          durationSeconds: nextSec,
          liveTranscript: newTranscript,
        };
        this.notifyActiveCall();
      }
    }, 1000);
  }

  public rejectCall(): void {
    this.currentIncomingCall = null;
    this.notifyIncomingCall();
  }

  public endActiveCall(): void {
    if (this.callTimer) {
      clearInterval(this.callTimer);
      this.callTimer = null;
    }
    this.currentActiveCall = null;
    this.notifyActiveCall();
  }

  public toggleMute(): void {
    if (this.currentActiveCall) {
      this.currentActiveCall = {
        ...this.currentActiveCall,
        isMuted: !this.currentActiveCall.isMuted,
      };
      this.notifyActiveCall();
    }
  }

  public toggleSpeaker(): void {
    if (this.currentActiveCall) {
      this.currentActiveCall = {
        ...this.currentActiveCall,
        isSpeakerOn: !this.currentActiveCall.isSpeakerOn,
      };
      this.notifyActiveCall();
    }
  }

  private notifyIncomingCall() {
    this.incomingCallSubscribers.forEach((cb) => cb(this.currentIncomingCall));
  }

  private notifyActiveCall() {
    this.activeCallSubscribers.forEach((cb) => cb(this.currentActiveCall));
  }
}
