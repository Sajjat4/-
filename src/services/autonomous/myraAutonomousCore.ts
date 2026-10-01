import { PersistedTaskSnapshot } from './types';
import { TaskPlanner } from './taskPlanner';
import { TaskPersistenceStore } from './taskPersistenceStore';
import { AccessibilityController } from './accessibilityController';

export class MyraAutonomousCore {
  private static instance: MyraAutonomousCore;
  private taskSubscribers: Array<(task: PersistedTaskSnapshot) => void> = [];
  private narrationSubscribers: Array<(narration: string) => void> = [];
  private currentTask: PersistedTaskSnapshot | null = null;
  private accessibility = AccessibilityController.getInstance();

  private constructor() {
    this.currentTask = TaskPersistenceStore.loadTask();
  }

  public static getInstance(): MyraAutonomousCore {
    if (!MyraAutonomousCore.instance) {
      MyraAutonomousCore.instance = new MyraAutonomousCore();
    }
    return MyraAutonomousCore.instance;
  }

  public subscribeTaskUpdate(cb: (task: PersistedTaskSnapshot) => void): () => void {
    this.taskSubscribers.push(cb);
    if (this.currentTask) cb(this.currentTask);
    return () => {
      this.taskSubscribers = this.taskSubscribers.filter((s) => s !== cb);
    };
  }

  public subscribeNarration(cb: (narration: string) => void): () => void {
    this.narrationSubscribers.push(cb);
    return () => {
      this.narrationSubscribers = this.narrationSubscribers.filter((s) => s !== cb);
    };
  }

  public async startTask(instruction: string): Promise<void> {
    const planned = TaskPlanner.planTask(instruction);
    this.currentTask = planned;
    TaskPersistenceStore.saveTask(planned);
    this.notifyTaskUpdate();
    this.notifyNarration(planned.lastNarration);

    // Launch target app natively or via emulation
    await this.accessibility.launchApp(planned.appTarget);

    // Execute steps progressively
    for (let i = 0; i < planned.steps.length; i++) {
      await new Promise((r) => setTimeout(r, 2000));
      if (!this.currentTask || this.currentTask.id !== planned.id) break;

      const updatedSteps = this.currentTask.steps.map((st, idx) => {
        if (idx < i) return { ...st, status: 'completed' as const };
        if (idx === i) return { ...st, status: 'running' as const };
        return { ...st, status: 'pending' as const };
      });

      const progress = (i + 1) / planned.steps.length;
      const narration = `ধাপ ${i + 1}: ${planned.steps[i].description}`;

      this.currentTask = {
        ...this.currentTask,
        steps: updatedSteps,
        currentStepIndex: i,
        progress,
        lastNarration: narration,
      };

      TaskPersistenceStore.saveTask(this.currentTask);
      this.notifyTaskUpdate();
      this.notifyNarration(narration);
    }

    if (this.currentTask && this.currentTask.id === planned.id) {
      this.currentTask = {
        ...this.currentTask,
        status: 'completed',
        progress: 1.0,
        lastNarration: 'MYRA অটোনোমাস টাস্ক সফলভাবে সম্পন্ন হয়েছে।',
      };
      TaskPersistenceStore.saveTask(this.currentTask);
      this.notifyTaskUpdate();
      this.notifyNarration(this.currentTask.lastNarration);
    }
  }

  public cancelCurrentTask(): void {
    if (this.currentTask) {
      this.currentTask = null;
      TaskPersistenceStore.clearTask();
      this.notifyNarration('টাস্ক বাতিল করা হয়েছে।');
    }
  }

  private notifyTaskUpdate() {
    if (this.currentTask) {
      this.taskSubscribers.forEach((cb) => cb(this.currentTask!));
    }
  }

  private notifyNarration(text: string) {
    this.narrationSubscribers.forEach((cb) => cb(text));
  }
}
