import React from 'react';
import { LiveConnectionStatus } from '../types';

interface LiveVoiceOrbProps {
  status: LiveConnectionStatus;
  isAssistantSpeaking: boolean;
  onClick: () => void;
  interimText?: string;
}

export const LiveVoiceOrb: React.FC<LiveVoiceOrbProps> = ({
  status,
  isAssistantSpeaking,
  onClick,
  interimText,
}) => {
  const isConnected = status === 'connected';

  return (
    <div className="flex flex-col items-center justify-center p-6 select-none">
      <div
        onClick={onClick}
        className="relative flex items-center justify-center w-56 h-56 cursor-pointer"
      >
        {/* Pulsing Outer Rings */}
        {isConnected && (
          <>
            <div
              className={`absolute inset-0 rounded-full blur-xl opacity-60 animate-ping ${
                isAssistantSpeaking ? 'bg-emerald-500' : 'bg-indigo-500'
              }`}
              style={{ animationDuration: '3s' }}
            />
            <div
              className={`absolute inset-4 rounded-full blur-lg opacity-80 animate-pulse ${
                isAssistantSpeaking ? 'bg-cyan-400' : 'bg-purple-600'
              }`}
              style={{ animationDuration: '2s' }}
            />
          </>
        )}

        {/* Central Core Glowing Orb */}
        <div
          className={`relative z-10 flex flex-col items-center justify-center w-40 h-40 rounded-full shadow-2xl transition-all duration-500 ${
            status === 'connecting'
              ? 'bg-amber-600 shadow-amber-500/50 animate-pulse'
              : status === 'connected'
              ? isAssistantSpeaking
                ? 'bg-gradient-to-tr from-emerald-600 to-teal-400 shadow-emerald-500/50 scale-105'
                : 'bg-gradient-to-tr from-indigo-600 via-purple-600 to-pink-500 shadow-indigo-500/50'
              : 'bg-zinc-800 border-2 border-zinc-700 shadow-zinc-900/50'
          }`}
        >
          <span className="text-xl font-bold tracking-wider text-white">MYRA</span>
          <span className="text-xs text-zinc-200 mt-1">
            {status === 'connecting'
              ? 'সংযোগ হচ্ছে...'
              : isConnected
              ? isAssistantSpeaking
                ? 'বলছি...'
                : 'শুনছি...'
              : 'ট্যাপ করে শুরু করুন'}
          </span>
        </div>
      </div>

      {/* Live Interim Caption Under Orb */}
      {interimText && (
        <div className="mt-6 px-4 py-2 bg-zinc-900/80 border border-zinc-800 rounded-full max-w-sm text-center">
          <p className="text-sm text-indigo-300 font-medium truncate">{interimText}</p>
        </div>
      )}
    </div>
  );
};
