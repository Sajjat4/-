import { PersistedTaskSnapshot, TaskStep } from './types';

export class TaskPlanner {
  public static planTask(instruction: string): PersistedTaskSnapshot {
    const id = `task_${Date.now()}`;
    const lower = instruction.toLowerCase();

    const appTarget = lower.includes('whatsapp')
      ? 'WhatsApp'
      : lower.includes('messenger')
      ? 'Messenger'
      : lower.includes('youtube')
      ? 'YouTube'
      : 'Chrome';

    const steps: TaskStep[] = [
      { id: 's1', description: `অ্যাপ চালু করা হচ্ছে: ${appTarget}`, status: 'running', timestamp: Date.now() },
      { id: 's2', description: 'ইউজার ইন্টারফেস ও এলিমেন্ট অনুসন্ধান', status: 'pending', timestamp: Date.now() },
      { id: 's3', description: 'নির্দেশনা অনুযায়ী ইনপুট ও অ্যাকশন চালনা', status: 'pending', timestamp: Date.now() },
      { id: 's4', description: 'অ্যাকশন সফলভাবে সম্পাদন ও ফলাফল যাচাই', status: 'pending', timestamp: Date.now() },
    ];

    return {
      id,
      title: instruction,
      appTarget,
      status: 'running',
      progress: 0.25,
      steps,
      currentStepIndex: 0,
      lastNarration: `MYRA অটোনোমাস টাস্ক শুরু হয়েছে: ${instruction}`,
      createdAt: Date.now(),
    };
  }
}
