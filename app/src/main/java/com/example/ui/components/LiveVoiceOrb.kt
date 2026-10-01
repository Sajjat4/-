package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceState
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiveVoiceOrb(
    state: VoiceState,
    isMuted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        ),
        label = "wave"
    )

    val orbColor = when (state) {
        VoiceState.IDLE -> PrimaryViolet
        VoiceState.LISTENING -> AccentCyan
        VoiceState.THINKING -> AccentAmber
        VoiceState.SPEAKING -> AccentGreen
    }

    val secondaryColor = when (state) {
        VoiceState.IDLE -> SecondaryPurple
        VoiceState.LISTENING -> PrimaryViolet
        VoiceState.THINKING -> Color(0xFFF97316)
        VoiceState.SPEAKING -> AccentCyan
    }

    val stateText = when {
        isMuted -> "মাইক বন্ধ"
        state == VoiceState.IDLE -> "কথা বলুন"
        state == VoiceState.LISTENING -> "শুনছি..."
        state == VoiceState.THINKING -> "ভাবছি..."
        state == VoiceState.SPEAKING -> "বলছি..."
        else -> "MYRA AI"
    }

    Box(
        modifier = modifier
            .size(240.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val baseRadius = size.minDimension / 3.2f
            val currentRadius = if (state == VoiceState.LISTENING || state == VoiceState.SPEAKING) {
                baseRadius * pulseScale
            } else {
                baseRadius
            }

            // Outer Glowing Pulse Rings
            if (state != VoiceState.IDLE && !isMuted) {
                for (i in 1..3) {
                    val ringRadius = currentRadius + (i * 22.dp.toPx() * (pulseScale - 0.8f))
                    val alpha = (0.35f / i) * (1.15f - (pulseScale - 0.92f) / 0.2f)
                    drawCircle(
                        color = orbColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                        radius = ringRadius,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // Inner Gradient Orb
            val brush = Brush.radialGradient(
                colors = listOf(
                    orbColor,
                    secondaryColor,
                    Color.Transparent
                ),
                center = center,
                radius = currentRadius * 1.3f
            )

            drawCircle(
                brush = brush,
                radius = currentRadius,
                alpha = 0.88f
            )

            // Dynamic Audio Waves for Speaking / Listening
            if ((state == VoiceState.SPEAKING || state == VoiceState.LISTENING) && !isMuted) {
                val numBars = 12
                val barMaxHeight = 35.dp.toPx()
                for (i in 0 until numBars) {
                    val angle = (i * (360f / numBars) + rotationAngle) * (Math.PI / 180.0)
                    val heightFactor = ((sin(waveOffset + i * 0.5) + 1.0) / 2.0).toFloat()
                    val barHeight = 8.dp.toPx() + barMaxHeight * heightFactor
                    
                    val cosVal = cos(angle).toFloat()
                    val sinVal = sin(angle).toFloat()
                    val startX = center.x + (currentRadius + 6.dp.toPx()) * cosVal
                    val startY = center.y + (currentRadius + 6.dp.toPx()) * sinVal
                    val endX = center.x + (currentRadius + 6.dp.toPx() + barHeight) * cosVal
                    val endY = center.y + (currentRadius + 6.dp.toPx() + barHeight) * sinVal

                    drawLine(
                        color = secondaryColor.copy(alpha = 0.9f),
                        start = androidx.compose.ui.geometry.Offset(startX, startY),
                        end = androidx.compose.ui.geometry.Offset(endX, endY),
                        strokeWidth = 4.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MYRA",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stateText,
                color = TextPrimary.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
