import React from 'react';
import { IncomingCallState } from '../../services/autonomous/communication/types';
import { Phone, PhoneOff } from 'lucide-react';

interface IncomingCallBannerProps {
  call: IncomingCallState | null;
  onAnswer: () => void;
  onReject: () => void;
}

export const IncomingCallBanner: React.FC<IncomingCallBannerProps> = ({
  call,
  onAnswer,
  onReject,
}) => {
  if (!call) return null;

  return (
    <div className="fixed top-4 left-4 right-4 z-50 max-w-md mx-auto">
      <div className="p-4 bg-zinc-900/95 border border-zinc-700/80 backdrop-blur-md rounded-2xl shadow-2xl flex items-center justify-between animate-in slide-in-from-top duration-300">
        <div className="min-w-0 pr-3">
          <div className="text-[10px] font-bold text-sky-400 uppercase tracking-wider">
            ইনকামিং {call.appName} কল
          </div>
          <div className="text-sm font-bold text-white truncate">{call.callerName}</div>
          <div className="text-[10px] text-zinc-400">
            বলুন: "ধরো" বা "কেটে দাও"
          </div>
        </div>

        <div className="flex items-center gap-2 shrink-0">
          <button
            onClick={onReject}
            className="p-3 bg-red-600 hover:bg-red-500 text-white rounded-full transition shadow-lg"
          >
            <PhoneOff className="w-5 h-5" />
          </button>
          <button
            onClick={onAnswer}
            className="p-3 bg-emerald-600 hover:bg-emerald-500 text-white rounded-full transition shadow-lg animate-pulse"
          >
            <Phone className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};
