import React from 'react';
import { AppSettings, BengaliDialect } from '../types';
import { ShieldCheck, Save, Key, Globe, Mic } from 'lucide-react';
import { AccessibilityController } from '../services/autonomous/accessibilityController';

interface SettingsViewProps {
  settings: AppSettings;
  onUpdateSettings: (newSettings: AppSettings) => void;
  onOpenAccessibility: () => void;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  settings,
  onUpdateSettings,
  onOpenAccessibility,
}) => {
  const [formData, setFormData] = React.useState<AppSettings>(settings);
  const [savedAlert, setSavedAlert] = React.useState(false);
  const accessibility = AccessibilityController.getInstance();

  const handleSave = () => {
    onUpdateSettings(formData);
    setSavedAlert(true);
    setTimeout(() => setSavedAlert(false), 2000);
  };

  const dialects: { id: BengaliDialect; label: string }[] = [
    { id: 'standard', label: 'প্রমিত বাংলা (Standard)' },
    { id: 'dhakai', label: 'ঢাকার আঞ্চলিক (Dhakai)' },
    { id: 'sylheti', label: 'সিলেটি (Sylheti)' },
    { id: 'chittagonian', label: 'চট্টগ্রামের আঞ্চলিক (Chittagonian)' },
    { id: 'noakhali', label: 'নোয়াখালী (Noakhali)' },
  ];

  return (
    <div className="max-w-md mx-auto px-4 pb-24 pt-4 space-y-5 overflow-y-auto">
      <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
        <h2 className="font-semibold text-lg text-zinc-100">সেটিংস ও কনফিগারেশন</h2>
        <button
          onClick={handleSave}
          className="flex items-center gap-1.5 px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-medium transition"
        >
          <Save className="w-3.5 h-3.5" />
          <span>সংরক্ষণ</span>
        </button>
      </div>

      {savedAlert && (
        <div className="p-3 bg-emerald-500/20 border border-emerald-500/50 rounded-xl text-emerald-400 text-xs text-center font-medium">
          সেটিংস সফলভাবে সংরক্ষিত হয়েছে!
        </div>
      )}

      {/* Native Bridge Status */}
      <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-xl">
        <div className="flex items-center justify-between">
          <div>
            <div className="text-sm font-semibold text-zinc-200">Android Native Bridge</div>
            <div className="text-xs text-zinc-500">
              {accessibility.hasNativeBridge() ? 'সক্রিয় (Active)' : 'Web Mode (Fallback)'}
            </div>
          </div>
          <button
            onClick={onOpenAccessibility}
            className="px-3 py-1 bg-zinc-800 hover:bg-zinc-700 text-indigo-400 text-xs rounded-lg transition"
          >
            পারমিশন চেক
          </button>
        </div>
      </div>

      {/* API Key */}
      <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-xl space-y-3">
        <div className="flex items-center gap-2 text-zinc-200 font-semibold text-sm">
          <Key className="w-4 h-4 text-indigo-400" />
          <span>Gemini API Key</span>
        </div>
        <input
          type="password"
          value={formData.customApiKey}
          onChange={(e) => setFormData({ ...formData, customApiKey: e.target.value })}
          placeholder="AI Studio Gemini API Key..."
          className="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3 py-2 text-xs text-zinc-200 focus:outline-none focus:border-indigo-500"
        />
      </div>

      {/* Dialect */}
      <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-xl space-y-3">
        <div className="flex items-center gap-2 text-zinc-200 font-semibold text-sm">
          <Globe className="w-4 h-4 text-indigo-400" />
          <span>বাংলা ডায়ালেক্ট ও উপভাষা</span>
        </div>
        <div className="space-y-2">
          {dialects.map((d) => (
            <label
              key={d.id}
              className="flex items-center gap-2 text-xs text-zinc-300 cursor-pointer"
            >
              <input
                type="radio"
                name="dialect"
                checked={formData.bengaliDialect === d.id}
                onChange={() => setFormData({ ...formData, bengaliDialect: d.id })}
                className="text-indigo-600 focus:ring-0"
              />
              <span>{d.label}</span>
            </label>
          ))}
        </div>
      </div>

      {/* System Prompt */}
      <div className="p-4 bg-zinc-900 border border-zinc-800 rounded-xl space-y-3">
        <div className="text-zinc-200 font-semibold text-sm">সিস্টেম প্রম্পট নির্দেশিকা</div>
        <textarea
          rows={5}
          value={formData.systemInstruction}
          onChange={(e) => setFormData({ ...formData, systemInstruction: e.target.value })}
          className="w-full bg-zinc-950 border border-zinc-800 rounded-lg p-3 text-xs text-zinc-200 focus:outline-none focus:border-indigo-500 font-mono"
        />
      </div>
    </div>
  );
};
