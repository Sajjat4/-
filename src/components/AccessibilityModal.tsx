import React, { useState, useEffect } from 'react';
import { X, Shield, CheckCircle2, AlertCircle, ExternalLink, Mic, Bell } from 'lucide-react';
import { AccessibilityController } from '../services/autonomous/accessibilityController';

interface AccessibilityModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AccessibilityModal: React.FC<AccessibilityModalProps> = ({ isOpen, onClose }) => {
  const accessibility = AccessibilityController.getInstance();
  const [status, setStatus] = useState(() => accessibility.getSetupStatus());

  useEffect(() => {
    if (isOpen) {
      setStatus(accessibility.getSetupStatus());
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const hasBridge = accessibility.hasNativeBridge();

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
      <div className="w-full max-w-sm bg-zinc-900 border border-zinc-800 rounded-2xl p-5 shadow-2xl space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Shield className="w-5 h-5 text-indigo-400" />
            <h3 className="font-semibold text-zinc-100 text-sm">MYRA Android পারমিশন ও এক্সেস</h3>
          </div>
          <button onClick={onClose} className="text-zinc-500 hover:text-zinc-300">
            <X className="w-4 h-4" />
          </button>
        </div>

        <p className="text-xs text-zinc-400 leading-relaxed">
          MYRA অটোনোমাস ভয়েস ও স্ক্রিন অটোমেশন সেবা পরিচালনার জন্য নিচের Android পারমিশনসমূহ প্রয়োজন:
        </p>

        <div className="space-y-2">
          {/* Bridge */}
          <div className="flex items-center justify-between p-2.5 bg-zinc-950 border border-zinc-800 rounded-xl">
            <div>
              <div className="text-xs font-medium text-zinc-200">Native Android Bridge</div>
              <div className="text-[10px] text-zinc-500">
                {hasBridge ? 'ইনস্টলড Android APK সচল' : 'ওয়েব ডেভেলপমেন্ট মোড'}
              </div>
            </div>
            {hasBridge ? (
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            ) : (
              <AlertCircle className="w-4 h-4 text-amber-400" />
            )}
          </div>

          {/* Mic */}
          <div className="flex items-center justify-between p-2.5 bg-zinc-950 border border-zinc-800 rounded-xl">
            <div className="flex items-center gap-2">
              <Mic className="w-4 h-4 text-indigo-400" />
              <div>
                <div className="text-xs font-medium text-zinc-200">মাইক্রোফোন (Microphone)</div>
                <div className="text-[10px] text-zinc-500">
                  {status.microphone ? 'অনুমোদিত (Granted)' : 'অনুমতি প্রয়োজন'}
                </div>
              </div>
            </div>
            {status.microphone ? (
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            ) : (
              <AlertCircle className="w-4 h-4 text-amber-400" />
            )}
          </div>

          {/* Notifications */}
          <div className="flex items-center justify-between p-2.5 bg-zinc-950 border border-zinc-800 rounded-xl">
            <div className="flex items-center gap-2">
              <Bell className="w-4 h-4 text-indigo-400" />
              <div>
                <div className="text-xs font-medium text-zinc-200">নোটিফিকেশন (Foreground Service)</div>
                <div className="text-[10px] text-zinc-500">
                  {status.notifications ? 'সক্রিয় (Active)' : 'অনুমতি প্রয়োজন'}
                </div>
              </div>
            </div>
            {status.notifications ? (
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            ) : (
              <AlertCircle className="w-4 h-4 text-amber-400" />
            )}
          </div>

          {/* Accessibility Service */}
          <div className="flex items-center justify-between p-2.5 bg-zinc-950 border border-zinc-800 rounded-xl">
            <div>
              <div className="text-xs font-medium text-zinc-200">Accessibility Service (অটোমেশন)</div>
              <div className="text-[10px] text-zinc-500">
                {status.accessibility ? 'MYRA সার্ভিস চালু আছে' : 'Android সেটিংস থেকে চালু করুন'}
              </div>
            </div>
            {status.accessibility ? (
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            ) : (
              <AlertCircle className="w-4 h-4 text-amber-400" />
            )}
          </div>
        </div>

        <div className="pt-2 space-y-2">
          {hasBridge ? (
            <>
              {(!status.microphone || !status.notifications) && (
                <button
                  onClick={() => {
                    accessibility.requestPermissionsFlow();
                    setTimeout(() => setStatus(accessibility.getSetupStatus()), 1000);
                  }}
                  className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl text-xs font-medium transition"
                >
                  পারমিশনসমূহ অনুমোদন করুন
                </button>
              )}

              <button
                onClick={() => {
                  accessibility.openAccessibilitySettings();
                  setTimeout(() => setStatus(accessibility.getSetupStatus()), 2000);
                }}
                className="w-full py-2.5 bg-zinc-800 hover:bg-zinc-700 text-zinc-200 rounded-xl text-xs font-medium flex items-center justify-center gap-1.5 transition"
              >
                <span>Android Accessibility সেটিংস খুলুন</span>
                <ExternalLink className="w-3.5 h-3.5 text-indigo-400" />
              </button>
            </>
          ) : (
            <button
              onClick={onClose}
              className="w-full py-2.5 bg-zinc-800 hover:bg-zinc-700 text-zinc-200 rounded-xl text-xs font-medium transition"
            >
              বন্ধ করুন
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
