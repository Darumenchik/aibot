package com.chiper.kz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chiper.kz.theme.*
import com.chiper.kz.theme.glass.GlassTypography

@Composable
fun ChatDensitySelector(
    selectedDensity: ChatDensity,
    onDensityChange: (ChatDensity) -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Плотность чата",
                style = GlassTypography.TitleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Выберите отступы между сообщениями",
                style = GlassTypography.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChatDensity.values().forEach { density ->
                    val isSelected = selectedDensity == density
                    GlassButton(
                        text = density.displayName,
                        onClick = { onDensityChange(density) },
                        variant = if (isSelected) GlassButtonVariant.Primary else GlassButtonVariant.Secondary,
                        fullWidth = false,
                        leadingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

enum class ChatDensity(
    val displayName: String,
    val messageSpacing: Dp,
    val bubblePadding: Dp,
    val avatarSize: Dp
) {
    Compact("Компактная", 2.dp, 4.dp, 32.dp),
    Standard("Стандартная", 8.dp, 8.dp, 40.dp),
    Relaxed("Просторная", 16.dp, 12.dp, 48.dp)
}

@Composable
fun FontScaleSelector(
    currentScale: Float,
    onScaleChange: (Float) -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Размер шрифта",
                style = GlassTypography.TitleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Масштаб интерфейса",
                style = GlassTypography.BodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = currentScale,
                onValueChange = onScaleChange,
                valueRange = 0.8f..1.4f,
                steps = 6,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Маленький", style = GlassTypography.LabelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Обычный", style = GlassTypography.LabelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Большой", style = GlassTypography.LabelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HighContrastToggle(
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Высокий контраст",
                    style = GlassTypography.TitleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Увеличить контрастность для лучшей читаемости",
                    style = GlassTypography.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun RTLPreview() {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RTL Preview",
                style = GlassTypography.TitleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, GlassShapes.Small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LTR",
                        style = GlassTypography.LabelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, GlassShapes.Small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RTL",
                        style = GlassTypography.LabelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Превью RTL режима для арабского/иврита",
                style = GlassTypography.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun LandscapeModeNotice() {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = GlassShapes.Card,
        elevation = GlassElevation.Level1
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.ScreenRotation,
                contentDescription = "Landscape",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Альбомный режим",
                    style = GlassTypography.TitleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Оптимизация интерфейса для горизонтальной ориентации",
                    style = GlassTypography.BodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}