import React from 'react';
import { TabType } from '../types';
import { Mic, MessageSquare, ListTodo, Settings } from 'lucide-react';

interface BottomNavProps {
  currentTab: TabType;
  onTabChange: (tab: TabType) => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ currentTab, onTabChange }) => {
  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-zinc-950/90 backdrop-blur-md border-t border-zinc-800 pb-safe">
      <div className="flex items-center justify-around h-16 max-w-md mx-auto px-4">
        <button
          onClick={() => onTabChange('home')}
          className={`flex flex-col items-center gap-1 transition-all ${
            currentTab === 'home' ? 'text-indigo-400 scale-105' : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <Mic className="w-5 h-5" />
          <span className="text-[11px] font-medium">হোম</span>
        </button>

        <button
          onClick={() => onTabChange('chat')}
          className={`flex flex-col items-center gap-1 transition-all ${
            currentTab === 'chat' ? 'text-indigo-400 scale-105' : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <MessageSquare className="w-5 h-5" />
          <span className="text-[11px] font-medium">চ্যাট</span>
        </button>

        <button
          onClick={() => onTabChange('tasks')}
          className={`flex flex-col items-center gap-1 transition-all ${
            currentTab === 'tasks' ? 'text-indigo-400 scale-105' : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <ListTodo className="w-5 h-5" />
          <span className="text-[11px] font-medium">টাস্ক</span>
        </button>

        <button
          onClick={() => onTabChange('settings')}
          className={`flex flex-col items-center gap-1 transition-all ${
            currentTab === 'settings' ? 'text-indigo-400 scale-105' : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          <Settings className="w-5 h-5" />
          <span className="text-[11px] font-medium">সেটিংস</span>
        </button>
      </div>
    </nav>
  );
};
