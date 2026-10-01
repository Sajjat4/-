package com.example.data.model

import kotlinx.serialization.Serializable

enum class TabType {
    HOME, CHAT, TASKS, NEWS, SETTINGS
}

enum class VoiceState {
    IDLE, LISTENING, THINKING, SPEAKING
}

enum class GroundingMode {
    AUTO, SEARCH_ON, SEARCH_OFF
}

enum class BengaliDialect(val displayName: String, val promptContext: String) {
    STANDARD("প্রমিত বাংলা (Standard)", "Speak in clear Standard Bengali (প্রমিত বাংলা)."),
    DHAKAI("ঢাকার আঞ্চলিক (Dhakai)", "Speak in Dhakai Bengali dialect with authentic colloquial phrases."),
    SYLHETI("সিলেটি (Sylheti)", "Speak in Sylheti Bengali dialect with local expressions."),
    CHITTAGONIAN("চট্টগ্রামের আঞ্চলিক (Chittagonian)", "Speak in Chittagonian Bengali dialect."),
    NOAKHALI("নোয়াখালী (Noakhali)", "Speak in Noakhali Bengali dialect.")
}

@Serializable
data class AppSettings(
    val apiKey: String = "",
    val youtubeApiKey: String = "",
    val liveModel: String = "gemini-2.5-flash",
    val chatModel: String = "gemini-2.5-flash",
    val voice: String = "Kore",
    val systemInstruction: String = """আপনি 'MYRA (মায়রা)' - একজন ইউনিভার্সাল অটোনোমাস এআই সহকারী (Autonomous AI Agent)।
আপনার ক্ষমতা:
১. ব্যবহারকারীর সাথে সাবলীল, সুন্দর বাংলায় কথা বলা ও প্রশ্নের উত্তর দেওয়া।
২. WhatsApp, Messenger, Telegram, Signal-এ স্বয়ংক্রিয়ভাবে মেসেজ পাঠানো এবং অডিও/ভিডিও কল পরিচালনা করা।
৩. ইনকামিং কল এলে ব্যবহারকারীকে জানানো এবং "ধরো" বা "কেটে দাও" নির্দেশ অনুযায়ী পদক্ষেপ নেওয়া।
৪. স্ক্রিন দেখে স্বয়ংক্রিয়ভাবে জটিল টাস্ক সম্পন্ন করা (যেমন YouTube সার্চ, অ্যাপ খোলা)।
৫. প্রতিটি কাজের ফলাফল যাচাই ও আপডেট করা।""",
    val enableLiveCaptions: Boolean = true,
    val enableBackgroundMode: Boolean = true,
    val accessibilityServiceEnabled: Boolean = true,
    val floatingOverlayEnabled: Boolean = true,
    val locationPermissionEnabled: Boolean = true,
    val notificationsPermissionEnabled: Boolean = true,
    val speechRate: Float = 1.0f,
    val highContrast: Boolean = false,
    val fontSize: String = "normal",
    val bengaliDialect: BengaliDialect = BengaliDialect.STANDARD,
    val groundingMode: GroundingMode = GroundingMode.AUTO
)

@Serializable
data class ChatMessage(
    val id: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSpoken: Boolean = false,
    val groundingUrls: List<String> = emptyList()
)

enum class TaskStatus {
    PENDING, RUNNING, VERIFYING, COMPLETED, FAILED, PAUSED
}

@Serializable
data class TaskStep(
    val id: String,
    val description: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val detail: String? = null
)

@Serializable
data class TaskSnapshot(
    val id: String,
    val title: String,
    val appTarget: String, // e.g. "WhatsApp", "YouTube", "Chrome"
    val status: TaskStatus,
    val progress: Float, // 0.0 to 1.0
    val steps: List<TaskStep>,
    val currentStepIndex: Int = 0,
    val lastNarration: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val category: String, // "Top", "Tech", "Sports", "Business"
    val summary: String,
    val url: String = ""
)

@Serializable
data class YouTubeItem(
    val id: String,
    val title: String,
    val channelTitle: String,
    val duration: String,
    val views: String,
    val thumbnailUrl: String
)

data class IncomingCallState(
    val callerName: String,
    val appName: String, // "WhatsApp", "Messenger", "Telegram", "Signal"
    val callType: String = "Voice Call", // "Voice Call" or "Video Call"
    val timestamp: Long = System.currentTimeMillis()
)

data class ActiveCallState(
    val callerName: String,
    val appName: String,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val liveTranscript: List<String> = emptyList()
)
