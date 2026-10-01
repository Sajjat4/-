/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  TabType,
  AppSettings,
  ChatMessage,
  LiveCaption,
  NewsItem,
  YouTubeVideoItem,
  DeviceActionType,
} from './types';
import { LiveClient, LiveConnectionStatus } from './services/liveClient';
import { NewsService } from './services/newsService';
import { YouTubeService } from './services/youtubeService';
import { ScreenShareService } from './services/screenShareService';
import { BottomNav } from './components/BottomNav';
import { HomeMenu } from './components/HomeMenu';
import { ChatView } from './components/ChatView';
import { SettingsView } from './components/SettingsView';
import { LiveVoiceOrb } from './components/LiveVoiceOrb';
import { ScreenSharePreview } from './components/ScreenSharePreview';
import { AccessibilityModal } from './components/AccessibilityModal';
import { FloatingNewsCard } from './components/FloatingNewsCard';
import { YouTubePlayerModal } from './components/YouTubePlayerModal';
import { AndroidEmulatorOverlay } from './components/AndroidEmulatorOverlay';

// MYRA Universal Autonomous AI Agent Engine
import { MyraAutonomousCore } from './services/autonomous/myraAutonomousCore';
import { ConversationIntentRouter } from './services/autonomous/conversationIntentRouter';
import { PersistedTaskSnapshot } from './services/autonomous/types';
import { AutonomousTaskCard } from './components/autonomous/AutonomousTaskCard';
import { ResumableTaskBanner } from './components/autonomous/ResumableTaskBanner';
import { TaskTimelineModal } from './components/autonomous/TaskTimelineModal';
import { AccessibilityController } from './services/autonomous/accessibilityController';

// MYRA Universal Communication Extension
import { UniversalCommunicationController } from './services/autonomous/communication/universalCommunicationController';
import {
  IncomingCallState,
  ActiveCallState,
} from './services/autonomous/communication/types';
import { IncomingCallBanner } from './components/communication/IncomingCallBanner';
import { ActiveCallModal } from './components/communication/ActiveCallModal';

const SETTINGS_STORAGE_KEY = 'bongolive_settings_v2';
const CHAT_STORAGE_KEY = 'bongolive_messages_v2';

const DEFAULT_SETTINGS: AppSettings = {
  customApiKey: '',
  useCustomApiKey: false,
  youtubeApiKey: '',
  useCustomYoutubeApiKey: false,
  liveModel: 'gemini-2.5-flash',
  chatModel: 'gemini-2.5-flash',
  voice: 'Kore',
  systemInstruction: `আপনি 'MYRA (মায়রা)' - একজন ইউনিভার্সাল অটোনোমাস এআই সহকারী (Autonomous AI Agent)।
আপনার ক্ষমতা:
১. ব্যবহারকারীর সাথে সাবলীল, সুন্দর বাংলায় কথা বলা ও প্রশ্নের উত্তর দেওয়া।
২. WhatsApp, Messenger, Telegram, Signal-এ স্বয়ংক্রিয়ভাবে মেসেজ পাঠানো এবং অডিও/ভিডিও কল পরিচালনা করা।
৩. ইনকামিং কল এলে ব্যবহারকারীকে জানানো এবং "ধরো" বা "কেটে দাও" নির্দেশ অনুযায়ী পদক্ষেপ নেওয়া।
৪. স্ক্রিন দেখে স্বয়ংক্রিয়ভাবে জটিল টাস্ক সম্পন্ন করা (যেমন YouTube সার্চ, অ্যাপ খোলা)।
৫. কাজের প্রতিটি ধাপে পর্যবেক্ষণ ও ফলাফল যাচাই করা।`,
  enableLiveCaptions: true,
  enableBackgroundMode: true,
  enableWakeLock: true,
  accessibilityServiceEnabled: true,
  floatingOverlayEnabled: true,
  locationPermissionEnabled: true,
  notificationsPermissionEnabled: true,
  contactsPermissionEnabled: true,
  cameraPermissionEnabled: true,
  phoneStatePermissionEnabled: true,
  batteryOptimizationIgnored: true,
  speechRate: 1.0,
  highContrast: false,
  fontSize: 'normal',
  bengaliDialect: 'standard',
  defaultGroundingMode: 'auto',
};

export default function App() {
  const [currentTab, setCurrentTab] = useState<TabType>('home');
  const [settings, setSettings] = useState<AppSettings>(() => {
    try {
      const saved = localStorage.getItem(SETTINGS_STORAGE_KEY);
      return saved ? { ...DEFAULT_SETTINGS, ...JSON.parse(saved) } : DEFAULT_SETTINGS;
    } catch (e) {
      return DEFAULT_SETTINGS;
    }
  });

  const [messages, setMessages] = useState<ChatMessage[]>(() => {
    try {
      const saved = localStorage.getItem(CHAT_STORAGE_KEY);
      return saved ? JSON.parse(saved) : [
        {
          id: 'welcome_1',
          role: 'assistant',
          content: 'স্বাগতম! আমি MYRA (মায়রা) - আপনার সার্বক্ষণিক বাংলা ভয়েস ও অটোনোমাস সহায়ক। আপনি আমাকে যেকোনো প্রশ্ন করতে পারেন অথবা অ্যাপ চালনার নির্দেশ দিতে পারেন।',
          timestamp: Date.now(),
        }
      ];
    } catch (e) {
      return [];
    }
  });

  const [liveStatus, setLiveStatus] = useState<LiveConnectionStatus>('idle');
  const [isAssistantSpeaking, setIsAssistantSpeaking] = useState(false);
  const [isScreenSharing, setIsScreenSharing] = useState(false);
  const [screenStream, setScreenStream] = useState<MediaStream | null>(null);
  const [interimCaption, setInterimCaption] = useState<string>('');
  const [isAccessibilityOpen, setIsAccessibilityOpen] = useState(false);

  // Autonomous Task states
  const [activeTaskSnapshot, setActiveTaskSnapshot] =
    useState<PersistedTaskSnapshot | null>(null);
  const [isTaskTimelineOpen, setIsTaskTimelineOpen] = useState(false);
  const [isResumableBannerDismissed, setIsResumableBannerDismissed] =
    useState(false);

  // Universal Communication states
  const [incomingCallState, setIncomingCallState] =
    useState<IncomingCallState | null>(null);
  const [activeCallState, setActiveCallState] = useState<ActiveCallState | null>(
    null
  );

  // Floating News Card states
  const [isNewsOpen, setIsNewsOpen] = useState(false);
  const [newsList, setNewsList] = useState<NewsItem[]>([]);

  // YouTube states
  const [isYouTubeOpen, setIsYouTubeOpen] = useState(false);
  const [activeVideo, setActiveVideo] = useState<YouTubeVideoItem | null>(null);
  const [youTubePlaylist, setYouTubePlaylist] = useState<YouTubeVideoItem[]>([]);

  // Device Gesture / Action states
  const [activeGesture, setActiveGesture] = useState<{
    type: DeviceActionType;
    text?: string;
  } | null>(null);

  const liveClientRef = useRef<LiveClient | null>(null);
  const screenShareService = useRef(new ScreenShareService());

  // Autonomous Core & Communication Controller references
  const autonomousCore = useRef<MyraAutonomousCore>(
    MyraAutonomousCore.getInstance()
  );
  const commController = useRef<UniversalCommunicationController>(
    UniversalCommunicationController.getInstance()
  );

  // Save Settings
  useEffect(() => {
    localStorage.setItem(SETTINGS_STORAGE_KEY, JSON.stringify(settings));
  }, [settings]);

  // Save Messages
  useEffect(() => {
    localStorage.setItem(CHAT_STORAGE_KEY, JSON.stringify(messages));
  }, [messages]);

  // Load News on mount
  useEffect(() => {
    NewsService.fetchTopNews().then(setNewsList);
  }, []);

  // Subscribe to Autonomous Core Task updates & Narration
  useEffect(() => {
    const core = autonomousCore.current;

    const unsubTask = core.subscribeTaskUpdate((snapshot) => {
      setActiveTaskSnapshot({ ...snapshot });
    });

    const unsubNarration = core.subscribeNarration((narration) => {
      const now = Date.now();
      setMessages((prev) => [
        ...prev,
        {
          id: `narr_${now}`,
          role: 'assistant',
          content: narration,
          timestamp: now,
        },
      ]);
    });

    const unsubIncoming = commController.current.subscribeIncomingCall(
      (call) => setIncomingCallState(call ? { ...call } : null)
    );

    const unsubActive = commController.current.subscribeActiveCall((call) =>
      setActiveCallState(call ? { ...call } : null)
    );

    return () => {
      unsubTask();
      unsubNarration();
      unsubIncoming();
      unsubActive();
    };
  }, []);

  const handleToggleVoiceOrb = async () => {
    if (liveStatus === 'connected') {
      liveClientRef.current?.disconnect();
    } else {
      if (!liveClientRef.current) {
        liveClientRef.current = new LiveClient(settings, {
          onStatusChange: setLiveStatus,
          onAssistantSpeaking: setIsAssistantSpeaking,
          onInterimCaption: setInterimCaption,
          onFinalCaption: (speaker, text) => {
            setMessages((prev) => [
              ...prev,
              {
                id: `msg_${Date.now()}`,
                role: speaker,
                content: text,
                timestamp: Date.now(),
              },
            ]);
            setInterimCaption('');
          },
          onError: (err) => {
            console.error('Live client error:', err);
          },
        });
      }
      await liveClientRef.current.connect();
    }
  };

  const handleSendMessage = (text: string) => {
    const newMsg: ChatMessage = {
      id: `msg_${Date.now()}`,
      role: 'user',
      content: text,
      timestamp: Date.now(),
    };
    setMessages((prev) => [...prev, newMsg]);

    // Route Intent
    const route = ConversationIntentRouter.route(text);
    if (route.intent === 'autonomous_task') {
      autonomousCore.current.startTask(text);
      setActiveGesture({ type: 'launch_app', text: 'WhatsApp' });
      setTimeout(() => setActiveGesture(null), 3000);
    } else if (route.intent === 'youtube') {
      handleOpenYouTube(route.payload || 'রবীন্দ্রসঙ্গীত');
    } else if (route.intent === 'news') {
      setIsNewsOpen(true);
    } else {
      // Simulate or call AI Response
      setTimeout(() => {
        let reply = 'আমি আপনার বার্তাটি পেয়েছি। আপনার সেবায় আমি সর্বদা প্রস্তুত।';
        const lower = text.toLowerCase();
        if (lower.includes('কেমন আছো') || lower.includes('হ্যালো')) {
          reply = 'আমি ভালো আছি! আমি আপনার বাংলা অটোনোমাস এআই সহকারী মাইরা। কী সাহায্য করতে পারি?';
        } else if (lower.includes('আবহাওয়া')) {
          reply = 'আজকের তাপমাত্রা প্রায় ২৮° সেলসিয়াস, আকাশ আংশিক মেঘলা এবং আবহাওয়া বেশ মনোরম।';
        } else if (lower.includes('গল্প')) {
          reply = 'একদা এক সুন্দর নদীর তীরে এক কিশোর স্বপ্ন দেখত এমন এক প্রযুক্তির যা মানুষের ভাষাকে সহজেই বুঝবে। আজ সেই স্বপ্নই বাস্তবে রূপান্তরিত।';
        }
        setMessages((prev) => [
          ...prev,
          {
            id: `msg_${Date.now()}`,
            role: 'assistant',
            content: reply,
            timestamp: Date.now(),
          },
        ]);
      }, 800);
    }
  };

  const handleToggleScreenShare = async () => {
    if (isScreenSharing) {
      screenShareService.current.stopCapture();
      setScreenStream(null);
      setIsScreenSharing(false);
    } else {
      const stream = await screenShareService.current.startCapture();
      if (stream) {
        setScreenStream(stream);
        setIsScreenSharing(true);
      }
    }
  };

  const handleOpenYouTube = async (query: string = 'রবীন্দ্রসঙ্গীত') => {
    const vids = await YouTubeService.searchVideos(query);
    setYouTubePlaylist(vids);
    setActiveVideo(vids[0] || null);
    setIsYouTubeOpen(true);
  };

  const handleQuickAction = (action: string) => {
    if (action === 'whatsapp') {
      autonomousCore.current.startTask('WhatsApp-এ স্বয়ংক্রিয় বার্তা পাঠান');
    } else if (action === 'youtube') {
      handleOpenYouTube('সেরা বাংলা গান');
    } else if (action === 'news') {
      setIsNewsOpen(true);
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-[#09090b] text-[#f4f4f5]">
      {/* Emulator Action Gesture Overlay */}
      <AndroidEmulatorOverlay activeGesture={activeGesture} />

      {/* Incoming Call Banner */}
      <IncomingCallBanner
        call={incomingCallState}
        onAnswer={() => commController.current.answerCall()}
        onReject={() => commController.current.rejectCall()}
      />

      {/* Active Call Modal */}
      <ActiveCallModal
        call={activeCallState}
        onEndCall={() => commController.current.endActiveCall()}
        onToggleMute={() => commController.current.toggleMute()}
        onToggleSpeaker={() => commController.current.toggleSpeaker()}
      />

      {/* Resumable Task Banner */}
      {!isResumableBannerDismissed && (
        <div className="pt-safe">
          <ResumableTaskBanner
            task={activeTaskSnapshot}
            onOpenTimeline={() => setIsTaskTimelineOpen(true)}
            onDismiss={() => setIsResumableBannerDismissed(true)}
          />
        </div>
      )}

      {/* Screen Share Live Box */}
      <ScreenSharePreview
        stream={screenStream}
        onStop={handleToggleScreenShare}
      />

      {/* Main Tab Content */}
      <main className="flex-1 overflow-y-auto">
        {currentTab === 'home' && (
          <div className="flex flex-col items-center justify-start py-4 space-y-6">
            <div className="text-center px-4 pt-2">
              <h1 className="text-2xl font-bold tracking-tight bg-gradient-to-r from-indigo-400 via-purple-300 to-pink-400 bg-clip-text text-transparent">
                BongoLive AI
              </h1>
              <p className="text-xs text-zinc-400 mt-1">
                MYRA Universal Bengali Voice & Autonomous Screen Assistant
              </p>
            </div>

            <LiveVoiceOrb
              status={liveStatus}
              isAssistantSpeaking={isAssistantSpeaking}
              onClick={handleToggleVoiceOrb}
              interimText={interimCaption}
            />

            <div className="flex items-center gap-3">
              <button
                onClick={handleToggleScreenShare}
                className={`px-4 py-2 rounded-xl text-xs font-semibold transition ${
                  isScreenSharing
                    ? 'bg-red-600 hover:bg-red-500 text-white'
                    : 'bg-zinc-800 hover:bg-zinc-700 text-zinc-200 border border-zinc-700'
                }`}
              >
                {isScreenSharing ? 'স্ক্রিন শেয়ার বন্ধ করুন' : 'স্ক্রিন শেয়ার চালু করুন'}
              </button>
            </div>

            <HomeMenu
              onQuickAction={handleQuickAction}
              onOpenAccessibility={() => setIsAccessibilityOpen(true)}
              onSimulateCall={() => commController.current.triggerSimulatedIncomingCall()}
            />
          </div>
        )}

        {currentTab === 'chat' && (
          <ChatView
            messages={messages}
            onSendMessage={handleSendMessage}
            onClearChat={() => setMessages([])}
          />
        )}

        {currentTab === 'tasks' && (
          <AutonomousTaskCard
            task={activeTaskSnapshot}
            onOpenTimeline={() => setIsTaskTimelineOpen(true)}
            onStartCustomTask={(inst) => autonomousCore.current.startTask(inst)}
          />
        )}

        {currentTab === 'settings' && (
          <SettingsView
            settings={settings}
            onUpdateSettings={setSettings}
            onOpenAccessibility={() => setIsAccessibilityOpen(true)}
          />
        )}
      </main>

      {/* Modals */}
      <AccessibilityModal
        isOpen={isAccessibilityOpen}
        onClose={() => setIsAccessibilityOpen(false)}
      />

      <FloatingNewsCard
        isOpen={isNewsOpen}
        newsList={newsList}
        onClose={() => setIsNewsOpen(false)}
        onSpeakNews={(news) => handleSendMessage(`খবরটি পড়ে শোনাও: ${news.title}`)}
      />

      <YouTubePlayerModal
        isOpen={isYouTubeOpen}
        activeVideo={activeVideo}
        playlist={youTubePlaylist}
        onClose={() => setIsYouTubeOpen(false)}
        onSelectVideo={setActiveVideo}
        onSearch={handleOpenYouTube}
      />

      <TaskTimelineModal
        isOpen={isTaskTimelineOpen}
        task={activeTaskSnapshot}
        onClose={() => setIsTaskTimelineOpen(false)}
        onCancelTask={() => autonomousCore.current.cancelCurrentTask()}
      />

      {/* Bottom Navigation */}
      <BottomNav currentTab={currentTab} onTabChange={setCurrentTab} />
    </div>
  );
}
