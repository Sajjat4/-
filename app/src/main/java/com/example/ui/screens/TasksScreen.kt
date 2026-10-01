package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeTask by viewModel.activeTask.collectAsState()
    var customTaskPrompt by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "MYRA অটোনোমাস টাস্ক ইঞ্জিন",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "স্ক্রিন অ্যানালাইসিস ও স্বয়ংক্রিয় অ্যাপ পরিচালনা",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Create New Task Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "নতুন অটোনোমাস টাস্ক লিখুন:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customTaskPrompt,
                    onValueChange = { customTaskPrompt = it },
                    placeholder = { Text("যেমন: WhatsApp-এ রাজুকে হাই পাঠাও", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = SurfaceVariantDark
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (customTaskPrompt.isNotBlank()) {
                            viewModel.startAutonomousTask(customTaskPrompt)
                            customTaskPrompt = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_autonomous_task_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অটোনোমাস টাস্ক রান করুন", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "চলমান/সাম্প্রতিক টাস্ক:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (activeTask != null) {
            val task = activeTask!!
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
                        Text(
                            text = task.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (task.status) {
                                TaskStatus.COMPLETED -> AccentGreen.copy(alpha = 0.2f)
                                TaskStatus.RUNNING -> AccentCyan.copy(alpha = 0.2f)
                                else -> PrimaryViolet.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = task.status.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (task.status) {
                                    TaskStatus.COMPLETED -> AccentGreen
                                    TaskStatus.RUNNING -> AccentCyan
                                    else -> PrimaryViolet
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { task.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = AccentCyan,
                        trackColor = SurfaceVariantDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "সর্বশেষ বিবরণ: ${task.lastNarration}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { viewModel.setShowTaskTimeline(true) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("টাইমলাইন বিশদ দেখুন", color = PrimaryViolet, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(SurfaceDark, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোন সক্রিয় টাস্ক চলমান নেই",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
