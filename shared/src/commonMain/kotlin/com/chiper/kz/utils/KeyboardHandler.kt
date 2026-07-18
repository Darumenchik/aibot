package com.chiper.kz.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KeyboardHandler(
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))
    }
}

@Composable
fun ImePaddingAware(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))
    }
}

@Composable
fun WindowInsetsHandler(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))
    }
}

@Composable
fun SafeAreaPadding(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}
