import React, { useRef, useEffect } from 'react';
import { StopCircle } from 'lucide-react';

interface ScreenSharePreviewProps {
  stream: MediaStream | null;
  onStop: () => void;
}

export const ScreenSharePreview: React.FC<ScreenSharePreviewProps> = ({ stream, onStop }) => {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  if (!stream) return null;

  return (
    <div className="w-full max-w-md mx-auto px-4 mb-3">
      <div className="relative rounded-2xl overflow-hidden bg-black border border-zinc-800 shadow-xl">
        <video ref={videoRef} autoPlay playsInline muted className="w-full h-44 object-cover" />
        <div className="absolute top-2 left-2 px-2.5 py-1 bg-red-600/80 backdrop-blur-md rounded-full flex items-center gap-1.5 text-[10px] font-semibold text-white">
          <span className="w-2 h-2 rounded-full bg-white animate-pulse" />
          <span>লাইভ স্ক্রিন শেয়ার সক্রিয়</span>
        </div>
        <button
          onClick={onStop}
          className="absolute bottom-2 right-2 px-3 py-1.5 bg-zinc-900/90 hover:bg-zinc-800 border border-zinc-700 rounded-lg text-xs text-zinc-200 flex items-center gap-1"
        >
          <StopCircle className="w-3.5 h-3.5 text-red-400" />
          <span>বন্ধ করুন</span>
        </button>
      </div>
    </div>
  );
};
