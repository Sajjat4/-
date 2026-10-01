package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.model.TabType
import com.example.data.model.TaskStatus
import com.example.ui.components.*
import com.example.ui.theme.DarkBackground
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    onOpenAccessibilitySettings: () -> Unit = {}
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val incomingCall by viewModel.incomingCall.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    val activeTask by viewModel.activeTask.collectAsState()
    val showTaskTimeline by viewModel.showTaskTimeline.collectAsState()
    val showYouTubeModal by viewModel.showYouTubeModal.collectAsState()
    val showNewsSheet by viewModel.showNewsSheet.collectAsState()
    val showAccessibilityModal by viewModel.showAccessibilityModal.collectAsState()
    val activeYouTubeVideo by viewModel.activeYouTubeVideo.collectAsState()
    val youTubePlaylist by viewModel.youTubePlaylist.collectAsState()
    val newsList by viewModel.newsList.collectAsState()

    // Handle Native Android Back Button safely
    BackHandler(
        enabled = currentTab != TabType.HOME ||
                showAccessibilityModal ||
                showNewsSheet ||
                showYouTubeModal ||
                showTaskTimeline ||
                incomingCall != null
    ) {
        when {
            showAccessibilityModal -> viewModel.setShowAccessibilityModal(false)
            showNewsSheet -> viewModel.setShowNewsSheet(false)
            showYouTubeModal -> viewModel.setShowYouTubeModal(false)
            showTaskTimeline -> viewModel.setShowTaskTimeline(false)
            incomingCall != null -> viewModel.rejectCall()
            currentTab != TabType.HOME -> viewModel.setTab(TabType.HOME)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            // Main Tab Content
            when (currentTab) {
                TabType.HOME -> HomeScreen(viewModel = viewModel)
                TabType.CHAT -> ChatScreen(viewModel = viewModel)
                TabType.TASKS -> TasksScreen(viewModel = viewModel)
                TabType.NEWS -> NewsScreen(viewModel = viewModel)
                TabType.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }

            // Top Floating Banners
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                // Incoming Call Banner
                IncomingCallBanner(
                    callState = incomingCall,
                    onAnswerCall = { viewModel.answerCall() },
                    onRejectCall = { viewModel.rejectCall() }
                )

                // Resumable / Paused Task Banner
                if (activeTask != null && activeTask?.status == TaskStatus.PAUSED) {
                    ResumableTaskBanner(
                        task = activeTask,
                        onOpenTimeline = { viewModel.setShowTaskTimeline(true) }
                    )
                }
            }

            // Active Call Fullscreen Modal
            if (activeCall != null) {
                ActiveCallModal(
                    callState = activeCall,
                    onEndCall = { viewModel.endCall() },
                    onToggleMute = { /* handled in ViewModel */ },
                    onToggleSpeaker = { /* handled in ViewModel */ }
                )
            }

            // Task Timeline Dialog
            if (showTaskTimeline && activeTask != null) {
                TaskTimelineDialog(
                    task = activeTask,
                    onDismiss = { viewModel.setShowTaskTimeline(false) }
                )
            }

            // YouTube Player Modal
            if (showYouTubeModal) {
                YouTubePlayerModal(
                    activeVideo = activeYouTubeVideo,
                    playlist = youTubePlaylist,
                    onClose = { viewModel.setShowYouTubeModal(false) },
                    onSelectVideo = { video -> viewModel.playYouTubeVideo(video) },
                    onSearch = { query -> viewModel.openYouTubeSearch(query) }
                )
            }

            // Floating News Sheet
            if (showNewsSheet) {
                FloatingNewsSheet(
                    newsList = newsList,
                    onClose = { viewModel.setShowNewsSheet(false) },
                    onSpeakNews = { item -> viewModel.readNewsItem(item) }
                )
            }

            // Accessibility Educational Modal
            if (showAccessibilityModal) {
                AccessibilityModal(
                    isOpen = showAccessibilityModal,
                    onClose = { viewModel.setShowAccessibilityModal(false) },
                    onEnableAccessibility = {
                        viewModel.setShowAccessibilityModal(false)
                        onOpenAccessibilitySettings()
                    }
                )
            }
        }
    }
}
