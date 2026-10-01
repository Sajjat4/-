import React from 'react';
import { PersistedTaskSnapshot } from '../../services/autonomous/types';
import { Bot, ChevronRight, X } from 'lucide-react';

interface ResumableTaskBannerProps {
  task: PersistedTaskSnapshot | null;
  onOpenTimeline: () => void;
  onDismiss: () => void;
}

export const ResumableTaskBanner: React.FC<ResumableTaskBannerProps> = ({
  task,
  onOpenTimeline,
  onDismiss,
}) => {
  if (!task || task.status === 'completed') return null;

  return (
    <div className="w-full max-w-md mx-auto px-4 mb-3">
      <div className="flex items-center justify-between p-3 bg-indigo-950/60 border border-indigo-800/60 rounded-xl shadow-lg">
        <div
          onClick={onOpenTimeline}
          className="flex items-center gap-2.5 flex-1 cursor-pointer min-w-0"
        >
          <Bot className="w-4 h-4 text-indigo-400 shrink-0" />
          <div className="min-w-0">
            <div className="text-xs font-semibold text-zinc-100 truncate">
              চলমান টাস্ক: {task.title}
            </div>
            <div className="text-[10px] text-indigo-300">
              {Math.round(task.progress * 100)}% সম্পন্ন • বিস্তারিত দেখুন
            </div>
          </div>
        </div>

        <button onClick={onDismiss} className="text-zinc-400 hover:text-zinc-200 p-1">
          <X className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
