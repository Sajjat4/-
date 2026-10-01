import { TaskStep } from './types';

export class RecoveryEngine {
  public static handleStepFailure(step: TaskStep): TaskStep {
    return {
      ...step,
      detail: 'রিকভারি ইঞ্জিন দ্বারা পুনরায় চেষ্টা করা হচ্ছে...',
    };
  }
}
