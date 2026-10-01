export class ScreenShareService {
  private stream: MediaStream | null = null;

  public async startCapture(): Promise<MediaStream | null> {
    try {
      if (navigator.mediaDevices && navigator.mediaDevices.getDisplayMedia) {
        this.stream = await navigator.mediaDevices.getDisplayMedia({
          video: true,
          audio: false,
        });
        return this.stream;
      }
    } catch (e) {
      console.warn('Display capture error:', e);
    }
    return null;
  }

  public stopCapture(): void {
    if (this.stream) {
      this.stream.getTracks().forEach((t) => t.stop());
      this.stream = null;
    }
  }
}
