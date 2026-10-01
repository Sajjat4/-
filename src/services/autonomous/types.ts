export type TaskStatus = 'pending' | 'running' | 'verifying' | 'completed' | 'failed' | 'paused';

export interface TaskStep {
  id: string;
  description: string;
  status: TaskStatus;
  timestamp: number;
  detail?: string;
}

export interface PersistedTaskSnapshot {
  id: string;
  title: string;
  appTarget: string;
  status: TaskStatus;
  progress: number;
  steps: TaskStep[];
  currentStepIndex: number;
  lastNarration: string;
  createdAt: number;
}
