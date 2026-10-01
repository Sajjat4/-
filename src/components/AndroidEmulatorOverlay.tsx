import React from 'react';
import { DeviceActionType } from '../types';

interface AndroidEmulatorOverlayProps {
  activeGesture: { type: DeviceActionType; text?: string } | null;
}

export const AndroidEmulatorOverlay: React.FC<AndroidEmulatorOverlayProps> = ({
  activeGesture,
}) => {
  if (!activeGesture) return null;

  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 pointer-events-none">
      <div className="px-4 py-2 bg-indigo-600/90 backdrop-blur-md border border-indigo-400/50 rounded-full shadow-2xl flex items-center gap-2 animate-bounce">
        <span className="w-2 h-2 rounded-full bg-emerald-400" />
        <span className="text-xs font-semibold text-white">
          অ্যাকশন: {activeGesture.type} {activeGesture.text ? `(${activeGesture.text})` : ''}
        </span>
      </div>
    </div>
  );
};
