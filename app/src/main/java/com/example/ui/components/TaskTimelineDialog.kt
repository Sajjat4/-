package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TaskSnapshot
import com.example.data.model.TaskStatus
import com.example.ui.theme.*

@Composable
fun TaskTimelineDialog(
    task: TaskSnapshot?,
    onDismiss: () -> Unit
) {
    if (task == null) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("task_timeline_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MYRA Autonomous Task Timeline",
                            fontSize = 12.sp,
                            color = PrimaryViolet,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = task.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { task.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = AccentCyan,
                    trackColor = SurfaceVariantDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    itemsIndexed(task.steps) { index, step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = when (step.status) {
                                    TaskStatus.COMPLETED -> Icons.Default.CheckCircle
                                    TaskStatus.RUNNING -> Icons.Default.PlayArrow
                                    else -> Icons.Default.Pending
                                },
                                contentDescription = null,
                                tint = when (step.status) {
                                    TaskStatus.COMPLETED -> AccentGreen
                                    TaskStatus.RUNNING -> AccentCyan
                                    else -> TextSecondary
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = step.description,
                                    fontSize = 14.sp,
                                    fontWeight = if (index == task.currentStepIndex) FontWeight.Bold else FontWeight.Normal,
                                    color = if (index <= task.currentStepIndex) TextPrimary else TextSecondary
                                )
                                step.detail?.let { d ->
                                    Text(
                                        text = d,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MYRA Narration: ${task.lastNarration}",
                    fontSize = 12.sp,
                    color = AccentAmber,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
