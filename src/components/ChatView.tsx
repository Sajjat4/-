import React, { useState, useRef, useEffect } from 'react';
import { ChatMessage } from '../types';
import { Send, Trash2, Bot, User, Sparkles } from 'lucide-react';

interface ChatViewProps {
  messages: ChatMessage[];
  onSendMessage: (text: string) => void;
  onClearChat: () => void;
}

export const ChatView: React.FC<ChatViewProps> = ({
  messages,
  onSendMessage,
  onClearChat,
}) => {
  const [input, setInput] = useState('');
  const endRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = () => {
    if (!input.trim()) return;
    onSendMessage(input.trim());
    setInput('');
  };

  const samplePrompts = [
    'ঢাকার বর্তমান আবহাওয়া কেমন?',
    'একটি মিষ্টি অনুপ্রেরণামূলক গল্প বলো',
    'WhatsApp-এ বার্তা পাঠাও',
    'ইউটিউবে নজরুলগীতি চালাও',
  ];

  return (
    <div className="flex flex-col h-full max-w-md mx-auto px-4 pb-20 pt-4">
      {/* Header */}
      <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
        <div className="flex items-center gap-2">
          <Bot className="w-5 h-5 text-indigo-400" />
          <h2 className="font-semibold text-zinc-100">MYRA AI বাংলা চ্যাট</h2>
        </div>
        <button
          onClick={onClearChat}
          className="text-zinc-500 hover:text-red-400 transition"
          title="চ্যাট হিস্ট্রি মুছুন"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto py-4 space-y-3">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex items-start gap-2.5 ${
              msg.role === 'user' ? 'justify-end' : 'justify-start'
            }`}
          >
            {msg.role !== 'user' && (
              <div className="w-7 h-7 rounded-full bg-indigo-600/30 text-indigo-400 flex items-center justify-center shrink-0 text-xs">
                AI
              </div>
            )}
            <div
              className={`max-w-[80%] rounded-2xl px-4 py-2.5 text-sm leading-relaxed ${
                msg.role === 'user'
                  ? 'bg-indigo-600 text-white rounded-br-none'
                  : 'bg-zinc-800/90 text-zinc-100 border border-zinc-700/50 rounded-bl-none'
              }`}
            >
              {msg.content}
            </div>
            {msg.role === 'user' && (
              <div className="w-7 h-7 rounded-full bg-zinc-700 text-zinc-300 flex items-center justify-center shrink-0 text-xs">
                <User className="w-4 h-4" />
              </div>
            )}
          </div>
        ))}
        <div ref={endRef} />
      </div>

      {/* Suggestions */}
      {messages.length <= 2 && (
        <div className="py-2 overflow-x-auto flex gap-2 no-scrollbar">
          {samplePrompts.map((p, i) => (
            <button
              key={i}
              onClick={() => onSendMessage(p)}
              className="text-xs shrink-0 px-3 py-1.5 bg-zinc-900 border border-zinc-800 rounded-full text-zinc-300 hover:bg-zinc-800"
            >
              {p}
            </button>
          ))}
        </div>
      )}

      {/* Input */}
      <div className="flex items-center gap-2 pt-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder="বাংলায় কিছু লিখুন..."
          className="flex-1 bg-zinc-900 border border-zinc-800 rounded-xl px-4 py-3 text-sm text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-indigo-500"
        />
        <button
          onClick={handleSend}
          className="p-3 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl transition"
        >
          <Send className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
