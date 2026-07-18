package com.chiper.kz.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.ui.platform.LocalView

@Composable
fun KeyboardHandler(
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val configuration = LocalConfiguration.current

    val imePadding by remember {
        mutableStateOf(PaddingValues(0.dp))
    }

    // Handle keyboard insets
    androidx.compose.runtime.LaunchedEffect(configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.setWindowInsetsListener(WindowInsetsCompat.Type.ime()) { _, insets ->
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                imePadding = PaddingValues(bottom = imeHeight.dp)
                insets
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(imePadding)
    ) {
        content(imePadding)
    }
}

@Composable
fun ImePaddingAware(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current

    val imePadding by remember {
        mutableStateOf(PaddingValues(0.dp))
    }

    // Observe IME animations
    androidx.compose.runtime.DisposableEffect(Unit) {
        val view = view
        val listener = object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                val heightDiff = oldBottom - bottom
                if (heightDiff > 200) { // Keyboard likely shown
                    imePadding = PaddingValues(bottom = heightDiff.dp)
                } else {
                    imePadding = PaddingValues(0.dp)
                }
            }
        }
        view.addOnLayoutChangeListener(listener)
        onDispose { view.removeOnLayoutChangeListener(listener) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(imePadding)
    ) {
        content(imePadding)
    }
}

@Composable
fun WindowInsetsHandler(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val view = LocalView.current
    val systemBarsPadding by remember { mutableStateOf(PaddingValues(0.dp)) }
    val imePadding by remember { mutableStateOf(PaddingValues(0.dp)) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val listener = androidx.core.view.OnApplyWindowInsetsListener { v, insets ->
                val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
                val system = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                imePadding = PaddingValues(bottom = ime.bottom.dp)
                systemBarsPadding = PaddingValues(
                    top = system.top.dp,
                    bottom = system.bottom.dp,
                    start = system.left.dp,
                    end = system.right.dp
                )
                insets
            }
            ViewCompat.setOnApplyWindowInsetsListener(view, listener)
            onDispose {
                ViewCompat.setOnApplyWindowInsetsListener(view, null)
            }
        } else {
            // Pre-R fallback
            val listener = object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?, left: Int, top: Int, right: Int, bottom: Int,
                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    val heightDiff = oldBottom - bottom
                    if (heightDiff > 100) {
                        imePadding = PaddingValues(bottom = heightDiff.dp)
                    } else {
                        imePadding = PaddingValues(0.dp)
                    }
                }
            }
            view.addOnLayoutChangeListener(listener)
            onDispose { view.removeOnLayoutChangeListener(listener) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(systemBarsPadding)
            .padding(imePadding)
    ) {
        val combined = PaddingValues(
            top = systemBarsPadding.calculateTopPadding(),
            bottom = max(systemBarsPadding.calculateBottomPadding(), imePadding.calculateBottomPadding()),
            start = systemBarsPadding.calculateStartPadding(),
            end = systemBarsPadding.calculateEndPadding()
        )
        content(combined)
    }
}

// Edge-to-edge support
fun Activity.enableEdgeToEdge() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    window.navigationBarColor = android.graphics.Color.TRANSPARENT
}

@Composable
fun SafeAreaPadding(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val padding by remember { mutableStateOf(PaddingValues(0.dp)) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val listener = androidx.core.view.OnApplyWindowInsetsListener { v, insets ->
                val system = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                padding = PaddingValues(
                    top = system.top.dp,
                    bottom = system.bottom.dp,
                    start = system.left.dp,
                    end = system.right.dp
                )
                insets
            }
            ViewCompat.setOnApplyWindowInsetsListener(view, listener)
            onDispose { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
        }
        onDispose {}
    }

    Box(modifier = modifier.fillMaxSize().padding(padding)) {
        content()
    }
}