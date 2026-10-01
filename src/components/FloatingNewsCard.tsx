import React, { useState } from 'react';
import { NewsItem } from '../types';
import { X, Volume2, Newspaper } from 'lucide-react';

interface FloatingNewsCardProps {
  newsList: NewsItem[];
  isOpen: boolean;
  onClose: () => void;
  onSpeakNews: (news: NewsItem) => void;
}

export const FloatingNewsCard: React.FC<FloatingNewsCardProps> = ({
  newsList,
  isOpen,
  onClose,
  onSpeakNews,
}) => {
  const [selectedCat, setSelectedCat] = useState<'All' | 'Top' | 'Tech' | 'Sports'>('All');

  if (!isOpen) return null;

  const filtered = selectedCat === 'All' ? newsList : newsList.filter((n) => n.category === selectedCat);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
      <div className="w-full max-w-sm max-h-[80vh] flex flex-col bg-zinc-900 border border-zinc-800 rounded-2xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b border-zinc-800">
          <div className="flex items-center gap-2">
            <Newspaper className="w-5 h-5 text-indigo-400" />
            <h3 className="font-semibold text-zinc-100 text-sm">লাইভ বাংলা সংবাদ বুলেটিন</h3>
          </div>
          <button onClick={onClose} className="text-zinc-500 hover:text-zinc-300">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Category Filter */}
        <div className="flex gap-2 p-3 overflow-x-auto border-b border-zinc-800/50">
          {(['All', 'Top', 'Tech', 'Sports'] as const).map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCat(cat)}
              className={`px-3 py-1 rounded-full text-xs font-medium transition ${
                selectedCat === cat
                  ? 'bg-indigo-600 text-white'
                  : 'bg-zinc-800 text-zinc-400 hover:bg-zinc-700'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* List */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {filtered.map((news) => (
            <div
              key={news.id}
              className="p-3 bg-zinc-950 border border-zinc-800/80 rounded-xl space-y-1.5"
            >
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-semibold text-indigo-400">{news.source} • {news.timeAgo}</span>
                <button
                  onClick={() => onSpeakNews(news)}
                  className="p-1 hover:bg-zinc-800 text-zinc-400 hover:text-indigo-300 rounded"
                  title="সংবাদ পড়ে শোনাও"
                >
                  <Volume2 className="w-3.5 h-3.5" />
                </button>
              </div>
              <h4 className="text-xs font-semibold text-zinc-100">{news.title}</h4>
              <p className="text-[11px] text-zinc-400 leading-relaxed">{news.summary}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
