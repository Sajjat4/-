package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.BongoLiveRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {

    private val repository = BongoLiveRepository()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _currentTab = MutableStateFlow(TabType.HOME)
    val currentTab: StateFlow<TabType> = _currentTab.asStateFlow()

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _activeTask = MutableStateFlow<TaskSnapshot?>(null)
    val activeTask: StateFlow<TaskSnapshot?> = _activeTask.asStateFlow()

    private val _incomingCall = MutableStateFlow<IncomingCallState?>(null)
    val incomingCall: StateFlow<IncomingCallState?> = _incomingCall.asStateFlow()

    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()

    private val _newsList = MutableStateFlow<List<NewsItem>>(repository.getInitialNews())
    val newsList: StateFlow<List<NewsItem>> = _newsList.asStateFlow()

    private val _youTubePlaylist = MutableStateFlow<List<YouTubeItem>>(repository.getInitialYouTubeVideos())
    val youTubePlaylist: StateFlow<List<YouTubeItem>> = _youTubePlaylist.asStateFlow()

    private val _activeYouTubeVideo = MutableStateFlow<YouTubeItem?>(null)
    val activeYouTubeVideo: StateFlow<YouTubeItem?> = _activeYouTubeVideo.asStateFlow()

    private val _isScreenSharing = MutableStateFlow(false)
    val isScreenSharing: StateFlow<Boolean> = _isScreenSharing.asStateFlow()

    private val _showNewsSheet = MutableStateFlow(false)
    val showNewsSheet: StateFlow<Boolean> = _showNewsSheet.asStateFlow()

    private val _showYouTubeModal = MutableStateFlow(false)
    val showYouTubeModal: StateFlow<Boolean> = _showYouTubeModal.asStateFlow()

    private val _showTaskTimeline = MutableStateFlow(false)
    val showTaskTimeline: StateFlow<Boolean> = _showTaskTimeline.asStateFlow()

    private val _showAccessibilityModal = MutableStateFlow(false)
    val showAccessibilityModal: StateFlow<Boolean> = _showAccessibilityModal.asStateFlow()

    init {
        // Initial welcome message in Bengali
        _chatMessages.value = listOf(
            ChatMessage(
                id = "welcome_1",
                role = "assistant",
                content = "স্বাগতম! আমি MYRA (মায়রা) - আপনার সার্বক্ষণিক বাংলা ভয়েস ও অটোনোমাস সহায়ক। আপনি আমাকে যেকোনো প্রশ্ন করতে পারেন অথবা অ্যাপ চালনার নির্দেশ দিতে পারেন।"
            )
        )
    }

    fun setTab(tab: TabType) {
        _currentTab.value = tab
    }

    fun updateSettings(newSettings: AppSettings) {
        _appSettings.value = newSettings
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleScreenSharing() {
        _isScreenSharing.value = !_isScreenSharing.value
    }

    fun toggleVoiceOrb() {
        if (_voiceState.value == VoiceState.IDLE) {
            _voiceState.value = VoiceState.LISTENING
            // Simulate voice input detection
            viewModelScope.launch {
                delay(2500)
                if (_voiceState.value == VoiceState.LISTENING) {
                    processVoicePrompt("হ্যালো মাইরা, আজকের খবর ও আবহাওয়া সম্পর্কে বলো")
                }
            }
        } else {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = userText
        )

        _chatMessages.value = _chatMessages.value + userMsg
        _voiceState.value = VoiceState.THINKING

        viewModelScope.launch {
            val responseText = repository.generateAiResponse(
                prompt = userText,
                settings = _appSettings.value,
                history = _chatMessages.value
            )

            val assistantMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = responseText
            )

            _chatMessages.value = _chatMessages.value + assistantMsg
            _voiceState.value = VoiceState.SPEAKING

            // Check if action command detected
            if (userText.contains("WhatsApp", ignoreCase = true) || userText.contains("মেসেজ", ignoreCase = true)) {
                startAutonomousTask("WhatsApp-এ বার্তা পাঠানোর প্রক্রিয়া সক্রিয় করা হচ্ছে")
            } else if (userText.contains("YouTube", ignoreCase = true) || userText.contains("গান", ignoreCase = true)) {
                openYouTubeSearch("রবীন্দ্রনাথের গান")
            } else if (userText.contains("খবর", ignoreCase = true) || userText.contains("news", ignoreCase = true)) {
                _showNewsSheet.value = true
            }

            delay(3000)
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun processVoicePrompt(prompt: String) {
        sendChatMessage(prompt)
    }

    fun startAutonomousTask(instruction: String) {
        val newTask = repository.createSampleTask(instruction)
        _activeTask.value = newTask
        _showTaskTimeline.value = true

        viewModelScope.launch {
            for (stepIndex in newTask.steps.indices) {
                delay(2000)
                val current = _activeTask.value ?: break
                val updatedSteps = current.steps.mapIndexed { idx, step ->
                    when {
                        idx < stepIndex -> step.copy(status = TaskStatus.COMPLETED)
                        idx == stepIndex -> step.copy(status = TaskStatus.RUNNING)
                        else -> step.copy(status = TaskStatus.PENDING)
                    }
                }
                val progress = (stepIndex + 1) / current.steps.size.toFloat()
                _activeTask.value = current.copy(
                    steps = updatedSteps,
                    currentStepIndex = stepIndex,
                    progress = progress,
                    lastNarration = "ধাপ ${stepIndex + 1}: ${current.steps[stepIndex].description}"
                )
            }

            delay(1500)
            _activeTask.value?.let { completed ->
                _activeTask.value = completed.copy(
                    status = TaskStatus.COMPLETED,
                    progress = 1.0f,
                    lastNarration = "অটোনোমাস টাস্ক সফলভাবে সম্পন্ন হয়েছে।"
                )
            }
        }
    }

    fun triggerIncomingCallSimulation(appName: String = "WhatsApp", callerName: String = "তানভীর আহমেদ") {
        _incomingCall.value = IncomingCallState(
            callerName = callerName,
            appName = appName
        )
    }

    fun answerIncomingCall() {
        val call = _incomingCall.value ?: return
        _incomingCall.value = null
        _activeCall.value = ActiveCallState(
            callerName = call.callerName,
            appName = call.appName,
            liveTranscript = listOf("হ্যালো! আসসালামু আলাইকুম, কেমন আছেন?")
        )

        viewModelScope.launch {
            while (_activeCall.value != null) {
                delay(1000)
                val current = _activeCall.value ?: break
                val newTranscript = if (current.durationSeconds == 3) {
                    current.liveTranscript + "আমি MYRA AI এর মাধ্যমে কথা বলছি।"
                } else current.liveTranscript

                _activeCall.value = current.copy(
                    durationSeconds = current.durationSeconds + 1,
                    liveTranscript = newTranscript
                )
            }
        }
    }

    fun rejectIncomingCall() {
        _incomingCall.value = null
    }

    fun endActiveCall() {
        _activeCall.value = null
    }

    fun toggleCallMute() {
        _activeCall.value?.let { current ->
            _activeCall.value = current.copy(isMuted = !current.isMuted)
        }
    }

    fun toggleCallSpeaker() {
        _activeCall.value?.let { current ->
            _activeCall.value = current.copy(isSpeakerOn = !current.isSpeakerOn)
        }
    }

    fun openYouTubeSearch(query: String) {
        val results = repository.getInitialYouTubeVideos()
        _youTubePlaylist.value = results
        _activeYouTubeVideo.value = results.firstOrNull()
        _showYouTubeModal.value = true
    }

    fun selectYouTubeVideo(item: YouTubeItem) {
        _activeYouTubeVideo.value = item
    }

    fun closeYouTubeModal() {
        _showYouTubeModal.value = false
    }

    fun setShowNewsSheet(show: Boolean) {
        _showNewsSheet.value = show
    }

    fun setShowTaskTimeline(show: Boolean) {
        _showTaskTimeline.value = show
    }

    fun setShowAccessibilityModal(show: Boolean) {
        _showAccessibilityModal.value = show
    }

    fun clearChatHistory() {
        _chatMessages.value = emptyList()
    }
}
