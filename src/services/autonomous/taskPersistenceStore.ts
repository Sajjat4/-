import { PersistedTaskSnapshot } from './types';

const STORAGE_KEY = 'myra_persisted_task_v1';

export class TaskPersistenceStore {
  public static saveTask(task: PersistedTaskSnapshot): void {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(task));
    } catch (e) {
      console.error('Failed to persist task:', e);
    }
  }

  public static loadTask(): PersistedTaskSnapshot | null {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      return data ? JSON.parse(data) : null;
    } catch (e) {
      return null;
    }
  }

  public static clearTask(): void {
    localStorage.removeItem(STORAGE_KEY);
  }
}
