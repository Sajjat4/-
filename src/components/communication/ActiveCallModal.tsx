import React from 'react';
import { ActiveCallState } from '../../services/autonomous/communication/types';
import { PhoneOff, Mic, MicOff, Volume2 } from 'lucide-react';

interface ActiveCallModalProps {
  call: ActiveCallState | null;
  onEndCall: () => void;
  onToggleMute: () => void;
  onToggleSpeaker: () => void;
}

export const ActiveCallModal: React.FC<ActiveCallModalProps> = ({
  call,
  onEndCall,
  onToggleMute,
  onToggleSpeaker,
}) => {
  if (!call) return null;

  const formatDuration = (sec: number) => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-zinc-950/95 backdrop-blur-md">
      <div className="w-full max-w-sm flex flex-col items-center justify-between h-[80vh] py-8">
        {/* Caller Info */}
        <div className="flex flex-col items-center space-y-2">
          <div className="w-24 h-24 rounded-full bg-gradient-to-tr from-indigo-600 to-purple-600 flex items-center justify-center text-3xl font-bold text-white shadow-2xl">
            {call.callerName.charAt(0)}
          </div>
          <h2 className="text-xl font-bold text-zinc-100">{call.callerName}</h2>
          <p className="text-xs text-indigo-400 font-medium">
            {call.appName} কল • {formatDuration(call.durationSeconds)}
          </p>
        </div>

        {/* Live Bengali Transcript */}
        <div className="w-full bg-zinc-900/80 border border-zinc-800 rounded-2xl p-4 flex-1 my-6 overflow-y-auto space-y-2">
          <div className="text-[11px] font-semibold text-zinc-400">লাইভ বাংলা ট্রান্সক্রিপশন</div>
          {call.liveTranscript.map((t, idx) => (
            <div key={idx} className="text-xs text-zinc-200 leading-relaxed bg-zinc-950/60 p-2.5 rounded-xl border border-zinc-800/50">
              {t}
            </div>
          ))}
        </div>

        {/* Controls */}
        <div className="flex items-center gap-6">
          <button
            onClick={onToggleMute}
            className={`p-4 rounded-full transition ${
              call.isMuted ? 'bg-red-500/20 text-red-400' : 'bg-zinc-800 text-zinc-300 hover:bg-zinc-700'
            }`}
          >
            {call.isMuted ? <MicOff className="w-6 h-6" /> : <Mic className="w-6 h-6" />}
          </button>

          <button
            onClick={onEndCall}
            className="p-5 rounded-full bg-red-600 hover:bg-red-500 text-white shadow-2xl transition"
          >
            <PhoneOff className="w-7 h-7" />
          </button>

          <button
            onClick={onToggleSpeaker}
            className={`p-4 rounded-full transition ${
              call.isSpeakerOn ? 'bg-indigo-500/20 text-indigo-400' : 'bg-zinc-800 text-zinc-300 hover:bg-zinc-700'
            }`}
          >
            <Volume2 className="w-6 h-6" />
          </button>
        </div>
      </div>
    </div>
  );
};
