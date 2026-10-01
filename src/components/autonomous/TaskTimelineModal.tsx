import React from 'react';
import { PersistedTaskSnapshot } from '../../services/autonomous/types';
import { X, CheckCircle2, CircleDashed, Clock, Bot } from 'lucide-react';

interface TaskTimelineModalProps {
  isOpen: boolean;
  task: PersistedTaskSnapshot | null;
  onClose: () => void;
  onCancelTask: () => void;
}

export const TaskTimelineModal: React.FC<TaskTimelineModalProps> = ({
  isOpen,
  task,
  onClose,
  onCancelTask,
}) => {
  if (!isOpen || !task) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
      <div className="w-full max-w-sm bg-zinc-900 border border-zinc-800 rounded-2xl shadow-2xl p-5 space-y-4 max-h-[85vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Bot className="w-5 h-5 text-indigo-400" />
            <h3 className="font-semibold text-sm text-zinc-100">টাস্ক এক্সিকিউশন টাইমলাইন</h3>
          </div>
          <button onClick={onClose} className="text-zinc-500 hover:text-zinc-300">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Info */}
        <div className="p-3 bg-zinc-950 border border-zinc-800 rounded-xl space-y-1">
          <div className="text-xs font-semibold text-zinc-200">{task.title}</div>
          <div className="text-[11px] text-zinc-400">টার্গেট অ্যাপ: {task.appTarget}</div>
        </div>

        {/* Steps */}
        <div className="flex-1 overflow-y-auto space-y-3 py-1">
          {task.steps.map((st, i) => (
            <div key={st.id} className="flex items-start gap-3">
              <div className="mt-0.5">
                {st.status === 'completed' ? (
                  <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                ) : st.status === 'running' ? (
                  <CircleDashed className="w-4 h-4 text-indigo-400 animate-spin" />
                ) : (
                  <Clock className="w-4 h-4 text-zinc-600" />
                )}
              </div>
              <div className="flex-1">
                <div
                  className={`text-xs font-medium ${
                    st.status === 'running'
                      ? 'text-indigo-300 font-semibold'
                      : st.status === 'completed'
                      ? 'text-zinc-300'
                      : 'text-zinc-500'
                  }`}
                >
                  {st.description}
                </div>
                {st.detail && (
                  <div className="text-[10px] text-zinc-500 mt-0.5">{st.detail}</div>
                )}
              </div>
            </div>
          ))}
        </div>

        <button
          onClick={() => {
            onCancelTask();
            onClose();
          }}
          className="w-full py-2 bg-red-600/20 hover:bg-red-600/30 text-red-400 border border-red-500/30 text-xs font-medium rounded-xl transition"
        >
          টাস্ক বাতিল করুন
        </button>
      </div>
    </div>
  );
};
