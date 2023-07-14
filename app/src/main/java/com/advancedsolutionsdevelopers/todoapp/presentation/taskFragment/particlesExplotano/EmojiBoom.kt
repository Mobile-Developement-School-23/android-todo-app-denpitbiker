package com.advancedsolutionsdevelopers.todoapp.presentation.taskFragment.particlesExplotano

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun Emojible(
    modifier: Modifier = Modifier,
    emoji: String,
    count: Int = 25,
    content: @Composable () -> Unit
) {
    var onTap by remember { mutableStateOf(false) }
    var tapOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    Box(Modifier.pointerInput(Unit) {
        detectTapGestures { offset ->
            tapOffset = offset
            onTap = true
        }
    }) {
        content()
    }
    if (onTap) {
        GenerateEmojiSplash(emoji = emoji, offset = tapOffset, count = count)
    }
}

@Composable
fun GenerateEmojiSplash(emoji: String, offset: Offset, count: Int) {
    repeat(count) {
        boom(emoji, offset)
    }
}

@Composable
fun boom(emoji: String, offset: Offset) {
    val duration = (500..1500).random()
    val startRot = randomInRange(0f, 360f)
    val scale = remember {
        Animatable(initialValue = randomInRange(0.5f, 3f))
    }
    val rotation = remember {
        Animatable(initialValue = startRot)
    }
    val posX = remember {
        Animatable(initialValue = offset.x)
    }
    val posY = remember {
        Animatable(initialValue = offset.y)
    }
    val alpha = remember {
        Animatable(initialValue = 1f)
    }
    LaunchedEffect(offset) {
        scale.animateTo(randomInRange(0.5f, 3f), animationSpec = tween(durationMillis = duration))
    }
    LaunchedEffect(offset) {
        rotation.animateTo(
            randomInRange(0f, 360f),
            animationSpec = tween(durationMillis = duration)
        )
    }
    LaunchedEffect(offset) {
        posX.animateTo(randomInRange(0f, 100f), animationSpec = tween(durationMillis = duration))
    }
    LaunchedEffect(offset) {
        posY.animateTo(randomInRange(0f, 100f), animationSpec = tween(durationMillis = duration))
    }
    LaunchedEffect(offset) {
        alpha.snapTo(1f)
        alpha.animateTo(0f, animationSpec = tween(durationMillis = 300, delayMillis = duration))
    }
    Text(
        text = emoji, modifier = Modifier
            .rotate(rotation.value)
            .scale(scale = scale.value)
            .offset(posX.value.dp, posY.value.dp)
            .alpha(alpha.value)
    )

}