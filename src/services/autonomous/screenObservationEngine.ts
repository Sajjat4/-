import { AccessibilityController, ScreenNode } from './accessibilityController';

export class ScreenObservationEngine {
  private static instance: ScreenObservationEngine;
  private accessibility = AccessibilityController.getInstance();

  public static getInstance(): ScreenObservationEngine {
    if (!ScreenObservationEngine.instance) {
      ScreenObservationEngine.instance = new ScreenObservationEngine();
    }
    return ScreenObservationEngine.instance;
  }

  public observeScreen(): ScreenNode[] {
    return this.accessibility.inspectScreen();
  }

  public findElement(query: string): ScreenNode | null {
    const nodes = this.observeScreen();
    const q = query.toLowerCase();
    return nodes.find((n) => (n.text && n.text.toLowerCase().includes(q)) || (n.viewId && n.viewId.toLowerCase().includes(q))) || null;
  }
}
