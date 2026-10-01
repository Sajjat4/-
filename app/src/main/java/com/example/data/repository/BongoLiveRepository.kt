package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class BongoLiveRepository {

    private val httpClient = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Mock initial news data in Bengali and English
    fun getInitialNews(): List<NewsItem> {
        return listOf(
            NewsItem(
                id = "news_1",
                title = "বাংলাদেশে কৃত্রিম বুদ্ধিমত্তা ও এআই প্রযুক্তির প্রসারে নতুন উদ্যোগ",
                source = "প্রথম আলো AI Tech",
                timeAgo = "১০ মিনিট আগে",
                category = "Tech",
                summary = "বাংলা ভাষায় ভয়েস অ্যাসিস্ট্যান্ট এবং অটোনোমাস এজেন্ট তৈরি নিয়ে এক বিশেষ কর্মশালা অনুষ্ঠিত হয়েছে।"
            ),
            NewsItem(
                id = "news_2",
                title = "স্মার্টফোন ব্যবহারকারীদের জন্য মাইরা ভয়েস অ্যাসিস্ট্যান্টের শুভ সূচনা",
                source = "সমকাল টেকনোলজি",
                timeAgo = "২৫ মিনিট আগে",
                category = "Top",
                summary = "সম্পূর্ণ বাংলায় কাজ করতে সক্ষম অটোনোমাস এআই সহকারী মাইরা মেসেজিং অ্যাপ ও স্ক্রিন শেয়ারিং পরিচালনা করছে।"
            ),
            NewsItem(
                id = "news_3",
                title = "বিশ্বকাপ ক্রিকেট বাছাইপর্বে বাংলাদেশের রোমাঞ্চকর জয়",
                source = "বিডিনিউজ২৪ স্পোর্টস",
                timeAgo = "১ ঘণ্টা আগে",
                category = "Sports",
                summary = "শেষ ওভারে দুর্দান্ত বোলিংয়ে ম্যাচ জিতে নতুন ইতিহাস গড়ল বাংলাদেশ জাতীয় ক্রিকেট দল।"
            ),
            NewsItem(
                id = "news_4",
                title = "গুগল জেমিনাই ৩.৮ লাইভ মডেল ও আন্তর্জাতিক টেক প্রযুক্তি আপডেট",
                source = "টেকটিউনস",
                timeAgo = "২ ঘণ্টা আগে",
                category = "Tech",
                summary = "রিয়েল-টাইম ভয়েস ও অডিও ইন্টারঅ্যাকশনের নতুন ফিচার সংযোজন করেছে টেক জায়ান্টরা।"
            )
        )
    }

    // Mock initial YouTube videos
    fun getInitialYouTubeVideos(): List<YouTubeItem> {
        return listOf(
            YouTubeItem(
                id = "yt_1",
                title = "Bengali Classical Rabindra Sangeet - Best Playlist",
                channelTitle = "Music Bengal",
                duration = "45:20",
                views = "1.2M views",
                thumbnailUrl = "https://picsum.photos/seed/yt1/320/180"
            ),
            YouTubeItem(
                id = "yt_2",
                title = "MYRA Autonomous AI Agent Demo in Bengali",
                channelTitle = "BongoLive Tech",
                duration = "12:05",
                views = "250K views",
                thumbnailUrl = "https://picsum.photos/seed/yt2/320/180"
            ),
            YouTubeItem(
                id = "yt_3",
                title = "Top 10 Tech News in Bangladesh 2026",
                channelTitle = "Tech Bangla Daily",
                duration = "18:40",
                views = "89K views",
                thumbnailUrl = "https://picsum.photos/seed/yt3/320/180"
            )
        )
    }

    // Generate intelligent responses using Gemini REST API or Bengali Intelligent Fallback
    suspend fun generateAiResponse(
        prompt: String,
        settings: AppSettings,
        history: List<ChatMessage>
    ): String {
        val apiKey = settings.apiKey.ifEmpty { System.getenv("GEMINI_API_KEY") ?: "" }
        if (apiKey.isNotEmpty()) {
            try {
                return callGeminiRestApi(prompt, apiKey, settings, history)
            } catch (e: Exception) {
                // Fallback on error
            }
        }
        return generateBengaliFallbackResponse(prompt, settings)
    }

    private fun callGeminiRestApi(
        prompt: String,
        apiKey: String,
        settings: AppSettings,
        history: List<ChatMessage>
    ): String {
        val model = if (settings.chatModel.contains("flash")) "gemini-2.5-flash" else "gemini-2.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val contentsArray = JSONArray()

        // System Instruction context + Bengali Dialect instruction
        val dialectInstruction = settings.bengaliDialect.promptContext
        val fullSystemPrompt = "${settings.systemInstruction}\n$dialectInstruction"

        // History
        history.takeLast(6).forEach { msg ->
            val role = if (msg.role == "user") "user" else "model"
            contentsArray.put(
                JSONObject().put("role", role).put(
                    "parts", JSONArray().put(JSONObject().put("text", msg.content))
                )
            )
        }

        // Current user prompt
        contentsArray.put(
            JSONObject().put("role", "user").put(
                "parts", JSONArray().put(JSONObject().put("text", "$fullSystemPrompt\n\nUser Question: $prompt"))
            )
        )

        val requestBodyJson = JSONObject().put("contents", contentsArray)

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", "")
                    }
                }
            }
        }

        return generateBengaliFallbackResponse(prompt, settings)
    }

    fun generateBengaliFallbackResponse(prompt: String, settings: AppSettings): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("নমস্কার") || lower.contains("হ্যালো") || lower.contains("হাই") || lower.contains("কেমন আছো") -> {
                "হ্যালো! আমি মাইরা (MYRA), আপনার বাংলা অটোনোমাস এআই সহকারী। আমি আপনাকে কীভাবে সাহায্য করতে পারি? আপনি আমাকে মেসেজ পাঠানো, ইউটিউবে গান চালানো বা খবর জানার নির্দেশ দিতে পারেন।"
            }
            lower.contains("আবহাওয়া") || lower.contains("weather") -> {
                "আজকের আবহাওয়া বেশ চমৎকার। তাপমাত্রা প্রায় ২৮° সেলসিয়াস, আংশিক মেঘলা আকাশ এবং হালকা হাওয়া প্রবাহিত হচ্ছে।"
            }
            lower.contains("গল্প") || lower.contains("story") -> {
                "একদা এক সবুজ গ্রামে ছিল এক মেধাবী শিশু ও তার এআই বন্ধু। তারা প্রতিদিন নতুন নতুন প্রযুক্তি শিখত এবং গ্রামের মানুষকে সাহায্য করত। তারা শিখল যে প্রযুক্তির সঠিক ব্যবহার মানুষের জীবনকে অনেক সুন্দর করে তুলতে পারে।"
            }
            lower.contains("হোয়াটসঅ্যাপ") || lower.contains("whatsapp") || lower.contains("মেসেজ") -> {
                "[ACTION:whatsapp_send] আমি WhatsApp অ্যাপ চালু করে মেসেজ পাঠানোর প্রস্তুতি নিচ্ছি। কার কাছে কী বার্তা পাঠাতে চান বলুন?"
            }
            lower.contains("ইউটিউব") || lower.contains("youtube") || lower.contains("গান") -> {
                "[ACTION:play_youtube] ইউটিউবে আপনার পছন্দের গান বা ভিডিও চালনা করা হচ্ছে।"
            }
            lower.contains("খবর") || lower.contains("news") -> {
                "[ACTION:get_news] সর্বশেষ তাজা খবর লোড করা হচ্ছে..."
            }
            else -> {
                "ধন্যবাদ আপনার প্রশ্নের জন্য। আমি MYRA অটোনোমাস এআই হিসেবে আপনার নির্দেশটি বুঝতে পেরেছি। আপনার কাজ সম্পন্ন করতে আমি স্বয়ংক্রিয়ভাবে স্ক্রিন এবং অ্যাপ চালনা করছি।"
            }
        }
    }

    // Flow generator for initial sample autonomous tasks
    fun createSampleTask(userInstruction: String): TaskSnapshot {
        val taskId = "task_${UUID.randomUUID().toString().take(6)}"
        val appTarget = when {
            userInstruction.contains("WhatsApp", ignoreCase = true) -> "WhatsApp"
            userInstruction.contains("YouTube", ignoreCase = true) -> "YouTube"
            userInstruction.contains("Messenger", ignoreCase = true) -> "Messenger"
            else -> "Chrome"
        }

        val steps = listOf(
            TaskStep("s1", "অ্যাপ খুলছে: $appTarget", TaskStatus.RUNNING, detail = "স্ক্রিনের উপাদান সনাক্ত করা হচ্ছে"),
            TaskStep("s2", "ইউজার ইন্টারফেস স্ক্যানিং ও এলিমেন্ট সিলেকশন", TaskStatus.PENDING, detail = "Accessibility Controller সক্রিয়"),
            TaskStep("s3", "কমান্ড ইনপুট ও অপ্টিমাইজেশন", TaskStatus.PENDING, detail = "অটোনোমাস অ্যাকশন এক্সিকিউশন"),
            TaskStep("s4", "ফলাফল যাচাই এবং টাস্ক সম্পন্ন", TaskStatus.PENDING, detail = "ভেরিফিকেশন ইঞ্জিন চেকিং")
        )

        return TaskSnapshot(
            id = taskId,
            title = userInstruction,
            appTarget = appTarget,
            status = TaskStatus.RUNNING,
            progress = 0.25f,
            steps = steps,
            currentStepIndex = 0,
            lastNarration = "অটোনোমাস টাস্ক শুরু হয়েছে: $userInstruction"
        )
    }
}
