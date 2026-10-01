import React from 'react';
import { PersistedTaskSnapshot } from '../../services/autonomous/types';
import { Bot, Play, CheckCircle2, Clock } from 'lucide-react';

interface AutonomousTaskCardProps {
  task: PersistedTaskSnapshot | null;
  onOpenTimeline: () => void;
  onStartCustomTask: (instruction: string) => void;
}

export const AutonomousTaskCard: React.FC<AutonomousTaskCardProps> = ({
  task,
  onOpenTimeline,
  onStartCustomTask,
}) => {
  const [input, setInput] = React.useState('');

  const handleRun = () => {
    if (!input.trim()) return;
    onStartCustomTask(input.trim());
    setInput('');
  };

  return (
    <div className="max-w-md mx-auto px-4 py-4 space-y-4">
      <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-2xl space-y-3">
        <div className="flex items-center gap-2">
          <Bot className="w-5 h-5 text-indigo-400" />
          <h3 className="text-sm font-semibold text-zinc-100">নতুন অটোনোমাস টাস্ক চালু করুন</h3>
        </div>
        <p className="text-xs text-zinc-400">
          MYRA নিজে স্ক্রিন দেখে WhatsApp-এ মেসেজ পাঠাতে বা অন্য যেকোনো কাজ স্বয়ংক্রিয়ভাবে করতে পারে।
        </p>

        <div className="flex gap-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleRun()}
            placeholder="যেমন: WhatsApp-এ রাজুকে হাই পাঠাও..."
            className="flex-1 bg-zinc-950 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-zinc-200 focus:outline-none focus:border-indigo-500"
          />
          <button
            onClick={handleRun}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-medium flex items-center gap-1 transition"
          >
            <Play className="w-3.5 h-3.5" />
            <span>রান</span>
          </button>
        </div>
      </div>

      {task && (
        <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-2xl space-y-3">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-zinc-200">{task.title}</span>
            <span className="text-[10px] px-2 py-0.5 rounded-full bg-indigo-500/20 text-indigo-400 uppercase font-bold">
              {task.status}
            </span>
          </div>

          <div className="w-full bg-zinc-800 h-2 rounded-full overflow-hidden">
            <div
              className="bg-indigo-500 h-full transition-all duration-300"
              style={{ width: `${task.progress * 100}%` }}
            />
          </div>

          <div className="text-xs text-amber-300/90 font-medium">
            {task.lastNarration}
          </div>

          <button
            onClick={onOpenTimeline}
            className="w-full py-2 bg-zinc-800 hover:bg-zinc-700 text-zinc-300 text-xs rounded-xl font-medium transition"
          >
            ধাপসমূহের বিস্তারিত টাইমলাইন দেখুন
          </button>
        </div>
      )}
    </div>
  );
};
