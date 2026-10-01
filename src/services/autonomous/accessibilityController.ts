/**
 * MYRA Native Accessibility Controller
 * Connects directly to Android native AccessibilityService via window.AndroidBridge
 * when running inside the Android application, with browser fallback.
 */

export interface ScreenNode {
  text?: string;
  viewId?: string;
  className?: string;
  bounds?: { left: number; top: number; right: number; bottom: number };
  clickable?: boolean;
}

export class AccessibilityController {
  private static instance: AccessibilityController;

  private constructor() {}

  public static getInstance(): AccessibilityController {
    if (!AccessibilityController.instance) {
      AccessibilityController.instance = new AccessibilityController();
    }
    return AccessibilityController.instance;
  }

  public hasNativeBridge(): boolean {
    return typeof window !== 'undefined' && !!window.AndroidBridge;
  }

  public getSetupStatus(): { microphone: boolean; notifications: boolean; accessibility: boolean; overlay: boolean } {
    if (this.hasNativeBridge()) {
      try {
        const raw = window.AndroidBridge!.getSetupStatus();
        return JSON.parse(raw);
      } catch (e) {
        console.error('Failed to get setup status from bridge:', e);
      }
    }
    return {
      microphone: true,
      notifications: true,
      accessibility: false,
      overlay: true,
    };
  }

  public requestPermissionsFlow(): void {
    if (this.hasNativeBridge()) {
      try {
        window.AndroidBridge!.requestPermissionsFlow();
      } catch (e) {
        console.error('Failed to trigger permissions flow:', e);
      }
    }
  }

  public isAccessibilityServiceEnabled(): boolean {
    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.isAccessibilityServiceEnabled();
      } catch (e) {
        console.error('Failed to check accessibility status from bridge:', e);
      }
    }
    return false;
  }

  public openAccessibilitySettings(): void {
    if (this.hasNativeBridge()) {
      window.AndroidBridge!.openAccessibilitySettings();
    } else {
      console.warn('Native AndroidBridge not present. Running in Web environment.');
    }
  }

  public openOverlaySettings(): void {
    if (this.hasNativeBridge()) {
      window.AndroidBridge!.openOverlaySettings();
    }
  }

  public async performClick(x: number, y: number): Promise<boolean> {
    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.performClick(x, y);
      } catch (e) {
        console.error('Native click execution error:', e);
      }
    }
    console.log(`[Web Emulation] Click at (${x}, ${y})`);
    return true;
  }

  public async performClickOnNode(viewId: string, text: string = ''): Promise<boolean> {
    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.performClickOnNode(viewId, text);
      } catch (e) {
        console.error('Native click on node error:', e);
      }
    }
    console.log(`[Web Emulation] Click on node viewId=${viewId}, text=${text}`);
    return true;
  }

  public async performInputText(text: string): Promise<boolean> {
    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.performInputText(text);
      } catch (e) {
        console.error('Native input text error:', e);
      }
    }
    console.log(`[Web Emulation] Type text: "${text}"`);
    return true;
  }

  public async performScroll(direction: 'up' | 'down' | 'forward' | 'backward'): Promise<boolean> {
    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.performScroll(direction);
      } catch (e) {
        console.error('Native scroll error:', e);
      }
    }
    console.log(`[Web Emulation] Scroll ${direction}`);
    return true;
  }

  public async performGlobalAction(action: 'back' | 'home' | 'recents' | 'notifications'): Promise<boolean> {
    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.performGlobalAction(action);
      } catch (e) {
        console.error('Native global action error:', e);
      }
    }
    console.log(`[Web Emulation] Global action: ${action}`);
    return true;
  }

  public async launchApp(appName: string): Promise<boolean> {
    const packageMap: Record<string, string> = {
      whatsapp: 'com.whatsapp',
      messenger: 'com.facebook.orca',
      telegram: 'org.telegram.messenger',
      signal: 'org.thoughtcrime.securesms',
      youtube: 'com.google.android.youtube',
      chrome: 'com.android.chrome',
      settings: 'com.android.settings',
      camera: 'com.android.camera2',
    };

    const targetKey = appName.toLowerCase().trim();
    const pkg = packageMap[targetKey] || appName;

    if (this.hasNativeBridge()) {
      try {
        return window.AndroidBridge!.launchApp(pkg);
      } catch (e) {
        console.error('Native launchApp error:', e);
      }
    }
    console.log(`[Web Emulation] Launch app package: ${pkg}`);
    return true;
  }

  public inspectScreen(): ScreenNode[] {
    if (this.hasNativeBridge()) {
      try {
        const jsonStr = window.AndroidBridge!.inspectScreen();
        if (jsonStr) {
          return JSON.parse(jsonStr) as ScreenNode[];
        }
      } catch (e) {
        console.error('Native inspectScreen error:', e);
      }
    }
    return [
      { text: 'হোম', clickable: true },
      { text: 'সার্চ বার', viewId: 'search_input', clickable: true },
      { text: 'সেন্ড বাটন', viewId: 'send_btn', clickable: true },
    ];
  }
}
