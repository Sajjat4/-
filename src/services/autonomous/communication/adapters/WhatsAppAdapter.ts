import { CommunicationAppAdapter } from './CommunicationAppAdapter';
import { AccessibilityController } from '../../accessibilityController';

export class WhatsAppAdapter implements CommunicationAppAdapter {
  appName = 'WhatsApp';
  packageName = 'com.whatsapp';
  private accessibility = AccessibilityController.getInstance();

  async sendMessage(recipient: string, message: string): Promise<boolean> {
    await this.accessibility.launchApp(this.packageName);
    await this.accessibility.performClickOnNode('search_holder', recipient);
    await this.accessibility.performInputText(message);
    await this.accessibility.performClickOnNode('send');
    return true;
  }

  async answerCall(): Promise<boolean> {
    return this.accessibility.performClickOnNode('answer_call');
  }

  async rejectCall(): Promise<boolean> {
    return this.accessibility.performClickOnNode('reject_call');
  }
}

export class MessengerAdapter implements CommunicationAppAdapter {
  appName = 'Messenger';
  packageName = 'com.facebook.orca';
  private accessibility = AccessibilityController.getInstance();

  async sendMessage(recipient: string, message: string): Promise<boolean> {
    await this.accessibility.launchApp(this.packageName);
    return true;
  }
  async answerCall(): Promise<boolean> {
    return this.accessibility.performClickOnNode('answer');
  }
  async rejectCall(): Promise<boolean> {
    return this.accessibility.performClickOnNode('decline');
  }
}

export class TelegramAdapter implements CommunicationAppAdapter {
  appName = 'Telegram';
  packageName = 'org.telegram.messenger';
  private accessibility = AccessibilityController.getInstance();

  async sendMessage(recipient: string, message: string): Promise<boolean> {
    await this.accessibility.launchApp(this.packageName);
    return true;
  }
  async answerCall(): Promise<boolean> {
    return true;
  }
  async rejectCall(): Promise<boolean> {
    return true;
  }
}

export class SignalAdapter implements CommunicationAppAdapter {
  appName = 'Signal';
  packageName = 'org.thoughtcrime.securesms';
  private accessibility = AccessibilityController.getInstance();

  async sendMessage(recipient: string, message: string): Promise<boolean> {
    await this.accessibility.launchApp(this.packageName);
    return true;
  }
  async answerCall(): Promise<boolean> {
    return true;
  }
  async rejectCall(): Promise<boolean> {
    return true;
  }
}

export class GenericMessagingAdapter implements CommunicationAppAdapter {
  appName = 'Default Messaging';
  packageName = 'com.google.android.apps.messaging';
  private accessibility = AccessibilityController.getInstance();

  async sendMessage(recipient: string, message: string): Promise<boolean> {
    await this.accessibility.launchApp(this.packageName);
    return true;
  }
  async answerCall(): Promise<boolean> {
    return true;
  }
  async rejectCall(): Promise<boolean> {
    return true;
  }
}
