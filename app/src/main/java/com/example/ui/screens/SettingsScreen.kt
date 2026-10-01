package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BengaliDialect
import com.example.data.model.GroundingMode
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()

    var apiKey by remember(settings) { mutableStateOf(settings.apiKey) }
    var youtubeApiKey by remember(settings) { mutableStateOf(settings.youtubeApiKey) }
    var systemInstruction by remember(settings) { mutableStateOf(settings.systemInstruction) }
    var liveCaptionsEnabled by remember(settings) { mutableStateOf(settings.enableLiveCaptions) }
    var backgroundModeEnabled by remember(settings) { mutableStateOf(settings.enableBackgroundMode) }
    var selectedDialect by remember(settings) { mutableStateOf(settings.bengaliDialect) }
    var groundingMode by remember(settings) { mutableStateOf(settings.groundingMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "সেটিংস ও কনফিগারেশন",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Gemini API, ভয়েস ও ডায়ালেক্ট সেটিংস",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // API Key Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gemini API Key (কাস্টম)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = { Text("AI Studio Gemini API Key লিখুন...", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = SurfaceVariantDark
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "YouTube Data API Key",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = youtubeApiKey,
                    onValueChange = { youtubeApiKey = it },
                    placeholder = { Text("YouTube API Key লিখুন...", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = SurfaceVariantDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bengali Dialect Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "বাংলা ডায়ালেক্ট ও আঞ্চলিক ভাষা সেটিংস:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                BengaliDialect.values().forEach { dialect ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDialect == dialect,
                            onClick = { selectedDialect = dialect },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryViolet)
                        )
                        Text(
                            text = dialect.displayName,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggles Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("লাইভ ক্যাপশন প্রদর্শন", color = Color.White)
                    Switch(
                        checked = liveCaptionsEnabled,
                        onCheckedChange = { liveCaptionsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryViolet)
                    )
                }

                Divider(color = SurfaceVariantDark, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ব্যাকগ্রাউন্ড সার্ভিস মোড", color = Color.White)
                    Switch(
                        checked = backgroundModeEnabled,
                        onCheckedChange = { backgroundModeEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryViolet)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System Instruction Prompt
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MYRA AI সিস্টেম প্রম্পট নির্দেশিকা:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = systemInstruction,
                    onValueChange = { systemInstruction = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = SurfaceVariantDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.updateSettings(
                    settings.copy(
                        apiKey = apiKey,
                        youtubeApiKey = youtubeApiKey,
                        systemInstruction = systemInstruction,
                        enableLiveCaptions = liveCaptionsEnabled,
                        enableBackgroundMode = backgroundModeEnabled,
                        bengaliDialect = selectedDialect,
                        groundingMode = groundingMode
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_settings_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("সেটিংস সংরক্ষণ করুন", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
