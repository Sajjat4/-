export type TabType = 'home' | 'chat' | 'settings' | 'tasks';

export type LiveConnectionStatus = 'idle' | 'connecting' | 'connected' | 'disconnecting' | 'error';

export type BengaliDialect = 'standard' | 'dhakai' | 'sylheti' | 'chittagonian' | 'noakhali';

export type GroundingFilterMode = 'auto' | 'on' | 'off';

export interface AppSettings {
  customApiKey: string;
  useCustomApiKey: boolean;
  youtubeApiKey: string;
  useCustomYoutubeApiKey: boolean;
  liveModel: string;
  chatModel: string;
  voice: string;
  systemInstruction: string;
  enableLiveCaptions: boolean;
  enableBackgroundMode: boolean;
  enableWakeLock: boolean;
  accessibilityServiceEnabled: boolean;
  floatingOverlayEnabled: boolean;
  locationPermissionEnabled: boolean;
  notificationsPermissionEnabled: boolean;
  contactsPermissionEnabled: boolean;
  cameraPermissionEnabled: boolean;
  phoneStatePermissionEnabled: boolean;
  batteryOptimizationIgnored: boolean;
  speechRate: number;
  highContrast: boolean;
  fontSize: 'small' | 'normal' | 'large';
  bengaliDialect: BengaliDialect;
  defaultGroundingMode: GroundingFilterMode;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: number;
  groundingUrls?: string[];
  isVoiceInput?: boolean;
}

export interface LiveCaption {
  id: string;
  speaker: 'user' | 'assistant';
  text: string;
  timestamp: number;
  isFinal: boolean;
}

export interface NewsItem {
  id: string;
  title: string;
  source: string;
  timeAgo: string;
  category: 'Top' | 'Tech' | 'Sports' | 'Business';
  summary: string;
  url?: string;
}

export interface YouTubeVideoItem {
  id: string;
  title: string;
  channelTitle: string;
  duration: string;
  views: string;
  thumbnailUrl: string;
}

export type DeviceActionType =
  | 'launch_app'
  | 'play_youtube'
  | 'get_news'
  | 'scroll_down'
  | 'scroll_up'
  | 'toggle_flashlight'
  | 'go_back'
  | 'go_home'
  | 'click_element'
  | 'input_text'
  | 'inspect_screen';

export interface AndroidBridgeInterface {
  getSetupStatus(): string;
  requestPermissionsFlow(): void;
  isAccessibilityServiceEnabled(): boolean;
  openAccessibilitySettings(): void;
  openOverlaySettings(): void;
  requestAllPermissions(): void;
  checkPermission(permission: string): boolean;
  inspectScreen(): string;
  performClick(x: number, y: number): boolean;
  performClickOnNode(viewId: string, text: string): boolean;
  performInputText(text: string): boolean;
  performScroll(direction: string): boolean;
  performGlobalAction(action: string): boolean;
  launchApp(packageName: string): boolean;
  startForegroundService(): void;
  stopForegroundService(): void;
  showToast(msg: string): void;
  triggerHaptic(effect: string): void;
}

declare global {
  interface Window {
    AndroidBridge?: AndroidBridgeInterface;
  }
}
