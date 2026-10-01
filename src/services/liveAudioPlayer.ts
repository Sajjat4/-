import { base64ToArrayBuffer } from './audioUtils';

export class LiveAudioPlayer {
  private audioCtx: AudioContext | null = null;
  private isPlaying = false;
  private queue: ArrayBuffer[] = [];

  constructor() {
    // AudioContext will be initialized on first user gesture
  }

  private initContext() {
    if (!this.audioCtx) {
      const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
      this.audioCtx = new AudioContextClass({ sampleRate: 24000 });
    }
  }

  public playChunk(base64Pcm: string) {
    this.initContext();
    if (!this.audioCtx) return;
    const buffer = base64ToArrayBuffer(base64Pcm);
    this.queue.push(buffer);
    if (!this.isPlaying) {
      this.processQueue();
    }
  }

  private async processQueue() {
    if (this.queue.length === 0 || !this.audioCtx) {
      this.isPlaying = false;
      return;
    }
    this.isPlaying = true;
    const rawPcm = this.queue.shift()!;
    try {
      // Decode 16-bit PCM 24kHz mono
      const int16 = new Int16Array(rawPcm);
      const float32 = new Float32Array(int16.length);
      for (let i = 0; i < int16.length; i++) {
        float32[i] = int16[i] / 32768.0;
      }
      const audioBuffer = this.audioCtx.createBuffer(1, float32.length, 24000);
      audioBuffer.getChannelData(0).set(float32);

      const source = this.audioCtx.createBufferSource();
      source.buffer = audioBuffer;
      source.connect(this.audioCtx.destination);
      source.onended = () => this.processQueue();
      source.start();
    } catch (e) {
      console.error('Audio playback error:', e);
      this.processQueue();
    }
  }

  public stop() {
    this.queue = [];
    this.isPlaying = false;
    if (this.audioCtx && this.audioCtx.state !== 'closed') {
      this.audioCtx.suspend();
    }
  }
}
