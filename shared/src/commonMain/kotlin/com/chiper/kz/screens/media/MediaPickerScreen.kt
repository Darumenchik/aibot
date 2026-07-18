package com.chiper.kz.screens.media

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.chiper.kz.components.glass.*
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography
import kotlinx.coroutines.delay

class MediaPickerScreen(
    val onImageSelected: (String) -> Unit,
    val onVideoSelected: (String) -> Unit,
    val onFileSelected: (String) -> Unit,
    val onCameraClick: () -> Unit,
    val onGalleryClick: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<MediaPickerViewModel>()

        MediaPickerContent(
            state = viewModel.uiState,
            onTabChanged = { viewModel.setTab(it) },
            onImageClick = { onImageSelected(it) },
            onVideoClick = { onVideoSelected(it) },
            onFileClick = { onFileSelected(it) },
            onCameraClick = { onCameraClick() },
            onGalleryClick = { onGalleryClick() },
            onClose = { navigator.pop() }
        )
    }
}

class MediaPickerViewModel : androidx.lifecycle.ViewModel() {
    var uiState by mutableStateOf(MediaPickerUiState())
        private set
}

data class MediaPickerUiState(
    val selectedTab: Int = 0, // 0: gallery, 1: video, 2: files
    val images: List<MediaItem> = generateMockImages(),
    val videos: List<MediaItem> = generateMockVideos(),
    val files: List<MediaItem> = generateMockFiles()
)

data class MediaItem(
    val uri: String,
    val thumbnail: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val duration: Int = 0 // for videos
)

private fun generateMockImages(): List<MediaItem> {
    return (1..20).map { i ->
        MediaItem(
            uri = "file:///storage/emulated/0/DCIM/IMG_$i.jpg",
            thumbnail = "thumb_$i",
            name = "IMG_$i.jpg",
            size = (Math.random() * 5_000_000 + 500_000).toLong(),
            mimeType = "image/jpeg"
        )
    }
}

private fun generateMockVideos(): List<MediaItem> {
    return (1..10).map { i ->
        MediaItem(
            uri = "file:///storage/emulated/0/DCIM/VID_$i.mp4",
            thumbnail = "vid_thumb_$i",
            name = "VID_$i.mp4",
            size = (Math.random() * 50_000_000 + 5_000_000).toLong(),
            mimeType = "video/mp4",
            duration = (Math.random() * 300 + 10).toInt()
        )
    }
}

private fun generateMockFiles(): List<MediaItem> {
    return (1..15).map { i ->
        val ext = listOf("pdf", "docx", "txt", "zip", "apk")[i % 5]
        MediaItem(
            uri = "file:///storage/emulated/0/Download/file_$i.$ext",
            thumbnail = "",
            name = "file_$i.$ext",
            size = (Math.random() * 20_000_000 + 100_000).toLong(),
            mimeType = "application/$ext"
        )
    }
}

@Composable
fun MediaPickerContent(
    state: MediaPickerUiState,
    onTabChanged: (Int) -> Unit,
    onImageClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    onFileClick: (String) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onClose: () -> Unit
) {
    var cameraAnimProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        cameraAnimProgress = 1f
    }

    ChiperTheme {
        GlassBackground(animated = true) {
            Scaffold(
                topBar = {
                    GlassTopAppBar(
                        title = when (state.selectedTab) {
                            0 -> "Галерея"
                            1 -> "Видео"
                            else -> "Файлы"
                        },
                        leadingIcon = {
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
                            }
                        },
                        trailingIcons = listOf(
                            {
                                IconButton(onClick = onCameraClick) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Камера", tint = Color.White)
                                }
                            },
                            {
                                IconButton(onClick = onGalleryClick) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Галерея", tint = Color.White)
                                }
                            }
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Tab bar
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = GlassShapes.Card,
                        elevation = GlassElevation.Level1
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            MediaTabItem(text = "Фото", selected = state.selectedTab == 0, onClick = { onTabChanged(0) })
                            MediaTabItem(text = "Видео", selected = state.selectedTab == 1, onClick = { onTabChanged(1) })
                            MediaTabItem(text = "Файлы", selected = state.selectedTab == 2, onClick = { onTabChanged(2) })
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid/List
                    when (state.selectedTab) {
                        0 -> ImageGrid(images = state.images, onClick = onImageClick)
                        1 -> VideoGrid(videos = state.videos, onClick = onVideoClick)
                        2 -> FileList(files = state.files, onClick = onFileClick)
                    }
                }
            }
        }
    }
}

@Composable
fun MediaTabItem(text: String, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ImageGrid(
    images: List<MediaItem>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(images.chunked(3)) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { image ->
                    MediaGridItem(
                        item = image,
                        onClick = { onClick(image.uri) }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaGridItem(
    item: MediaItem,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh)
    )

    GlassSurface(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick()
                pressed = false
            }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = GlassElevation.Level1
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = formatFileSize(item.size),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun VideoGrid(
    videos: List<MediaItem>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(videos) { video ->
            VideoGridItem(
                item = video,
                onClick = { onClick(video.uri) }
            )
        }
    }
}

@Composable
fun VideoGridItem(
    item: MediaItem,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = GlassTypography.TitleMedium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatDuration(item.duration),
                        style = GlassTypography.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = GlassTypography.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = formatFileSize(item.size),
                        style = GlassTypography.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun FileList(
    files: List<MediaItem>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(files) { file ->
            FileListItem(
                item = file,
                onClick = { onClick(file.uri) }
            )
        }
    }
}

@Composable
fun FileListItem(
    item: MediaItem,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (item.mimeType) {
                    "application/pdf" -> Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(24.dp))
                    "application/zip" -> Icon(Icons.Default.Archive, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF2AABEE), modifier = Modifier.size(24.dp))
                    else -> Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = GlassTypography.BodyMedium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatFileSize(item.size),
                    style = GlassTypography.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000 -> "%.1f GB".format(bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> "%.1f MB".format(bytes / 1_000_000.0)
        bytes >= 1_000 -> "%.1f KB".format(bytes / 1_000.0)
        else -> "$bytes B"
    }
}