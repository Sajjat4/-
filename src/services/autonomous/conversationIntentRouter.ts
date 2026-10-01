export interface IntentRouteResult {
  intent: 'chat' | 'autonomous_task' | 'communication' | 'youtube' | 'news';
  actionType?: string;
  payload?: string;
  extractedText?: string;
}

export class ConversationIntentRouter {
  public static route(text: string): IntentRouteResult {
    const lower = text.toLowerCase();

    if (lower.includes('whatsapp') || lower.includes('মেসেজ') || lower.includes('পাঠাও')) {
      return {
        intent: 'autonomous_task',
        actionType: 'whatsapp_send',
        extractedText: text,
      };
    }

    if (lower.includes('ইউটিউব') || lower.includes('youtube') || lower.includes('গান')) {
      return {
        intent: 'youtube',
        actionType: 'play_youtube',
        payload: 'রবীন্দ্রসঙ্গীত',
      };
    }

    if (lower.includes('খবর') || lower.includes('news')) {
      return {
        intent: 'news',
        actionType: 'get_news',
      };
    }

    return {
      intent: 'chat',
      extractedText: text,
    };
  }
}
