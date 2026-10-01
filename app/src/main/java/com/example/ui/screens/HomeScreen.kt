package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiveVoiceOrb
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val voiceState by viewModel.voiceState.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isScreenSharing by viewModel.isScreenSharing.collectAsState()

    val samplePrompts = listOf(
        "WhatsApp-এ বার্তা পাঠাও",
        "ইউটিউবে রবীন্দ্রসঙ্গীত শোনাও",
        "আজকের সেরা খবর কী?",
        "একটি সুন্দর গল্প বলো",
        "ঢাকার আবহাওয়া কেমন?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MYRA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "সর্বজনীন বাংলা এআই ও অটোনোমাস সহকারী",
                    fontSize = 12.sp,
                    color = AccentCyan
                )
            }

            IconButton(
                onClick = { viewModel.setShowAccessibilityModal(true) },
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceVariantDark, CircleShape)
            ) {
                Icon(
                    Icons.Default.Accessibility,
                    contentDescription = "Accessibility",
                    tint = PrimaryViolet
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Central Animated Voice Orb
        LiveVoiceOrb(
            state = voiceState,
            isMuted = isMuted,
            onClick = { viewModel.toggleVoiceOrb() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mic Controls Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.toggleMute() },
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isMuted) AccentRed.copy(alpha = 0.2f) else SurfaceDark, CircleShape)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = if (isMuted) AccentRed else Color.White
                )
            }

            Button(
                onClick = { viewModel.toggleScreenSharing() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScreenSharing) AccentGreen else PrimaryViolet
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.testTag("screen_share_button")
            ) {
                Icon(
                    imageVector = if (isScreenSharing) Icons.Default.ScreenShare else Icons.Default.StopScreenShare,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScreenSharing) "স্ক্রিন শেয়ার হচ্ছে" else "স্ক্রিন শেয়ার চালু করুন",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Suggestion Chips
        Text(
            text = "দ্রুত বাংলা নির্দেশনাবলী:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(samplePrompts) { prompt ->
                SuggestionChip(
                    onClick = { viewModel.processVoicePrompt(prompt) },
                    label = { Text(prompt, color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = SurfaceDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Action Tools Grid Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MYRA টেস্ট অ্যান্ড অ্যাকশন কন্ট্রোল:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // WhatsApp Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.startAutonomousTask("WhatsApp-এ স্বয়ংক্রিয় বার্তা পাঠান") }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF25D366).copy(alpha = 0.2f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("WhatsApp", fontSize = 12.sp, color = TextPrimary)
                    }

                    // YouTube Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.openYouTubeSearch("Bengali Music") }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Red.copy(alpha = 0.2f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Red)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("YouTube", fontSize = 12.sp, color = TextPrimary)
                    }

                    // News Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.setShowNewsSheet(true) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Newspaper, contentDescription = null, tint = AccentCyan)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("খবর", fontSize = 12.sp, color = TextPrimary)
                    }

                    // Simulate Call Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.triggerIncomingCallSimulation("WhatsApp", "তানভীর আহমেদ") }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryViolet.copy(alpha = 0.2f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = PrimaryViolet)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("কল টেস্ট", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}
