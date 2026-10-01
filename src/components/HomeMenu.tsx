import React from 'react';
import { MessageSquare, Play, Newspaper, PhoneCall, ShieldCheck, Sparkles } from 'lucide-react';

interface HomeMenuProps {
  onQuickAction: (action: string) => void;
  onOpenAccessibility: () => void;
  onSimulateCall: () => void;
}

export const HomeMenu: React.FC<HomeMenuProps> = ({
  onQuickAction,
  onOpenAccessibility,
  onSimulateCall,
}) => {
  return (
    <div className="w-full max-w-md mx-auto px-4 space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-zinc-400 uppercase tracking-wider">
          MYRA অটোনোমাস অ্যাকশন
        </h3>
        <button
          onClick={onOpenAccessibility}
          className="flex items-center gap-1.5 text-xs text-indigo-400 hover:text-indigo-300"
        >
          <ShieldCheck className="w-4 h-4" />
          <span>অনুমতি ও পারমিশন</span>
        </button>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <button
          onClick={() => onQuickAction('whatsapp')}
          className="flex items-center gap-3 p-3.5 bg-zinc-900/80 hover:bg-zinc-800/90 border border-zinc-800 rounded-xl transition text-left"
        >
          <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-lg">
            <MessageSquare className="w-5 h-5" />
          </div>
          <div>
            <div className="font-semibold text-sm text-zinc-200">WhatsApp</div>
            <div className="text-xs text-zinc-500">মেসেজ পাঠান</div>
          </div>
        </button>

        <button
          onClick={() => onQuickAction('youtube')}
          className="flex items-center gap-3 p-3.5 bg-zinc-900/80 hover:bg-zinc-800/90 border border-zinc-800 rounded-xl transition text-left"
        >
          <div className="p-2.5 bg-red-500/20 text-red-400 rounded-lg">
            <Play className="w-5 h-5" />
          </div>
          <div>
            <div className="font-semibold text-sm text-zinc-200">YouTube</div>
            <div className="text-xs text-zinc-500">ভিডিও ও গান</div>
          </div>
        </button>

        <button
          onClick={() => onQuickAction('news')}
          className="flex items-center gap-3 p-3.5 bg-zinc-900/80 hover:bg-zinc-800/90 border border-zinc-800 rounded-xl transition text-left"
        >
          <div className="p-2.5 bg-sky-500/20 text-sky-400 rounded-lg">
            <Newspaper className="w-5 h-5" />
          </div>
          <div>
            <div className="font-semibold text-sm text-zinc-200">তাজা খবর</div>
            <div className="text-xs text-zinc-500">ব্রেকিং নিউজ</div>
          </div>
        </button>

        <button
          onClick={onSimulateCall}
          className="flex items-center gap-3 p-3.5 bg-zinc-900/80 hover:bg-zinc-800/90 border border-zinc-800 rounded-xl transition text-left"
        >
          <div className="p-2.5 bg-indigo-500/20 text-indigo-400 rounded-lg">
            <PhoneCall className="w-5 h-5" />
          </div>
          <div>
            <div className="font-semibold text-sm text-zinc-200">কল টেস্ট</div>
            <div className="text-xs text-zinc-500">ইনকামিং কল</div>
          </div>
        </button>
      </div>
    </div>
  );
};
