import React, { useState } from 'react';
import { YouTubeVideoItem } from '../types';
import { X, Play, Search } from 'lucide-react';

interface YouTubePlayerModalProps {
  isOpen: boolean;
  activeVideo: YouTubeVideoItem | null;
  playlist: YouTubeVideoItem[];
  onClose: () => void;
  onSelectVideo: (video: YouTubeVideoItem) => void;
  onSearch: (query: string) => void;
}

export const YouTubePlayerModal: React.FC<YouTubePlayerModalProps> = ({
  isOpen,
  activeVideo,
  playlist,
  onClose,
  onSelectVideo,
  onSearch,
}) => {
  const [query, setQuery] = useState('');

  if (!isOpen || !activeVideo) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
      <div className="w-full max-w-sm bg-zinc-900 border border-zinc-800 rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[85vh]">
        {/* Header */}
        <div className="flex items-center justify-between p-3.5 border-b border-zinc-800">
          <span className="font-semibold text-xs text-zinc-200">MYRA YouTube Player</span>
          <button onClick={onClose} className="text-zinc-500 hover:text-zinc-300">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Video Player Box */}
        <div className="relative aspect-video bg-black flex items-center justify-center">
          <img
            src={activeVideo.thumbnailUrl}
            alt={activeVideo.title}
            className="w-full h-full object-cover opacity-80"
          />
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-14 h-14 rounded-full bg-red-600/90 flex items-center justify-center shadow-lg">
              <Play className="w-6 h-6 text-white fill-white ml-0.5" />
            </div>
          </div>
        </div>

        {/* Video Info */}
        <div className="p-3 border-b border-zinc-800">
          <h4 className="font-semibold text-xs text-zinc-100 line-clamp-1">{activeVideo.title}</h4>
          <p className="text-[11px] text-zinc-500 mt-0.5">{activeVideo.channelTitle} • {activeVideo.views}</p>
        </div>

        {/* Search */}
        <div className="p-3 border-b border-zinc-800 flex gap-2">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch(query)}
            placeholder="ইউটিউবে খুঁজুন..."
            className="flex-1 bg-zinc-950 border border-zinc-800 rounded-lg px-3 py-1.5 text-xs text-zinc-200 focus:outline-none focus:border-indigo-500"
          />
          <button
            onClick={() => onSearch(query)}
            className="px-3 py-1.5 bg-indigo-600 text-white rounded-lg text-xs"
          >
            <Search className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Playlist */}
        <div className="flex-1 overflow-y-auto p-3 space-y-2">
          {playlist.map((item) => (
            <div
              key={item.id}
              onClick={() => onSelectVideo(item)}
              className={`flex items-center gap-2.5 p-2 rounded-xl cursor-pointer transition ${
                item.id === activeVideo.id ? 'bg-zinc-800' : 'hover:bg-zinc-950'
              }`}
            >
              <img
                src={item.thumbnailUrl}
                alt=""
                className="w-16 h-10 object-cover rounded-lg shrink-0"
              />
              <div className="min-w-0">
                <div className="text-xs text-zinc-200 font-medium truncate">{item.title}</div>
                <div className="text-[10px] text-zinc-500">{item.channelTitle}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
