package com.chiper.kz.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.rosemoe.editor.text.MarkdownParser
import io.github.rosemoe.editor.text.MarkdownRenderer

@Composable
fun MarkdownMessage(
    text: String,
    style: TextStyle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal
    ),
    modifier: Modifier = Modifier,
    color: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
    linkColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    codeBackground: Color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
) {
    val annotatedString = remember(text) {
        parseMarkdown(text, style, color, linkColor, codeBackground)
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier,
        overflow = TextOverflow.Visible,
        softWrap = true
    )
}

private fun parseMarkdown(
    text: String,
    baseStyle: TextStyle,
    color: Color,
    linkColor: Color,
    codeBackground: Color
): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val segments = parseMarkdownSegments(text)

    segments.forEach { segment ->
        when (segment.type) {
            MarkdownSegmentType.Text -> {
                builder.append(segment.content, baseStyle.copy(color = color))
            }
            MarkdownSegmentType.Bold -> {
                builder.append(segment.content, baseStyle.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                ))
            }
            MarkdownSegmentType.Italic -> {
                builder.append(segment.content, baseStyle.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = color
                ))
            }
            MarkdownSegmentType.Code -> {
                builder.append(segment.content, baseStyle.copy(
                    fontFamily = FontFamily.Monospace,
                    background = codeBackground,
                    color = color
                ))
            }
            MarkdownSegmentType.CodeBlock -> {
                builder.append("\n")
                builder.append(segment.content, baseStyle.copy(
                    fontFamily = FontFamily.Monospace,
                    background = codeBackground,
                    color = color,
                    fontSize = 13.sp
                ))
                builder.append("\n")
            }
            MarkdownSegmentType.Link -> {
                val parts = segment.content.split("|")
                val displayText = parts.getOrElse(0) { "" }
                val url = parts.getOrElse(1) { "" }
                builder.append(displayText, baseStyle.copy(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                ))
                // TODO: Handle click on URL
            }
            MarkdownSegmentType.Heading1 -> {
                builder.append(segment.content, baseStyle.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                ))
                builder.append("\n")
            }
            MarkdownSegmentType.Heading2 -> {
                builder.append(segment.content, baseStyle.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                ))
                builder.append("\n")
            }
            MarkdownSegmentType.Heading3 -> {
                builder.append(segment.content, baseStyle.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                ))
                builder.append("\n")
            }
            MarkdownSegmentType.BlockQuote -> {
                builder.append("│ ", baseStyle.copy(
                    color = color.copy(alpha = 0.5f)
                ))
                builder.append(segment.content, baseStyle.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = color.copy(alpha = 0.8f)
                ))
                builder.append("\n")
            }
            MarkdownSegmentType.BulletList -> {
                builder.append("• ", baseStyle.copy(color = color))
                builder.append(segment.content, baseStyle.copy(color = color))
                builder.append("\n")
            }
            MarkdownSegmentType.NumberedList -> {
                builder.append("${segment.metadata?.getOrDefault("index", "1")}. ", baseStyle.copy(color = color))
                builder.append(segment.content, baseStyle.copy(color = color))
                builder.append("\n")
            }
            MarkdownSegmentType.Strikethrough -> {
                builder.append(segment.content, baseStyle.copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = color.copy(alpha = 0.6f)
                ))
            }
        }
    }

    return builder.toAnnotatedString()
}

enum class MarkdownSegmentType {
    Text, Bold, Italic, Code, CodeBlock, Link,
    Heading1, Heading2, Heading3,
    BlockQuote, BulletList, NumberedList, Strikethrough
}

data class MarkdownSegment(
    val type: MarkdownSegmentType,
    val content: String,
    val metadata: Map<String, String>? = null
)

private fun parseMarkdownSegments(text: String): List<MarkdownSegment> {
    val segments = mutableListOf<MarkdownSegment>()
    var remaining = text
    var listIndex = 0

    while (remaining.isNotEmpty()) {
        // Code blocks (```...```)
        val codeBlockMatch = Regex("```([\\s\\S]*?)```").find(remaining)
        if (codeBlockMatch != null && codeBlockMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.CodeBlock, codeBlockMatch.groupValues[1].trim()))
            remaining = remaining.substring(codeBlockMatch.range.last)
            continue
        }

        // Inline code (`...`)
        val inlineCodeMatch = Regex("`([^`]+)`").find(remaining)
        if (inlineCodeMatch != null && inlineCodeMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.Code, inlineCodeMatch.groupValues[1]))
            remaining = remaining.substring(inlineCodeMatch.range.last)
            continue
        }

        // Bold (**...** or __...__)
        val boldMatch = Regex("\\*\\*([^*]+)\\*\\*|__([^_]+)__").find(remaining)
        if (boldMatch != null && boldMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.Bold, boldMatch.groupValues[1]?.ifBlank { boldMatch.groupValues[2] } ?: ""))
            remaining = remaining.substring(boldMatch.range.last)
            continue
        }

        // Italic (*...* or _..._)
        val italicMatch = Regex("\\*([^*]+)\\*|_([^_]+)_").find(remaining)
        if (italicMatch != null && italicMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.Italic, italicMatch.groupValues[1]?.ifBlank { italicMatch.groupValues[2] } ?: ""))
            remaining = remaining.substring(italicMatch.range.last)
            continue
        }

        // Strikethrough (~~...~~)
        val strikeMatch = Regex("~~([^~]+)~~").find(remaining)
        if (strikeMatch != null && strikeMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.Strikethrough, strikeMatch.groupValues[1]))
            remaining = remaining.substring(strikeMatch.range.last)
            continue
        }

        // Links [text](url)
        val linkMatch = Regex("\\[([^\\]]+)\\]\\(([^)]+)\\)").find(remaining)
        if (linkMatch != null && linkMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.Link, "${linkMatch.groupValues[1]}|${linkMatch.groupValues[2]}"))
            remaining = remaining.substring(linkMatch.range.last)
            continue
        }

        // Headings
        val headingMatch = Regex("^(#{1,3})\\s+(.+)$", RegexOption.MULTILINE).find(remaining)
        if (headingMatch != null && headingMatch.range.first == 0) {
            val level = headingMatch.groupValues[1].length
            val headingType = when (level) {
                1 -> MarkdownSegmentType.Heading1
                2 -> MarkdownSegmentType.Heading2
                else -> MarkdownSegmentType.Heading3
            }
            segments.add(MarkdownSegment(headingType, headingMatch.groupValues[2]))
            remaining = remaining.substring(headingMatch.range.last)
            continue
        }

        // Block quotes
        val quoteMatch = Regex("^>\\s*(.+)$", RegexOption.MULTILINE).find(remaining)
        if (quoteMatch != null && quoteMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.BlockQuote, quoteMatch.groupValues[1]))
            remaining = remaining.substring(quoteMatch.range.last)
            continue
        }

        // Bullet lists
        val bulletMatch = Regex("^[\\-\\*\\+]\\s+(.+)$", RegexOption.MULTILINE).find(remaining)
        if (bulletMatch != null && bulletMatch.range.first == 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.BulletList, bulletMatch.groupValues[1]))
            remaining = remaining.substring(bulletMatch.range.last)
            continue
        }

        // Numbered lists
        val numberedMatch = Regex("^\\d+\\.\\s+(.+)$", RegexOption.MULTILINE).find(remaining)
        if (numberedMatch != null && numberedMatch.range.first == 0) {
            listIndex++
            segments.add(MarkdownSegment(MarkdownSegmentType.NumberedList, numberedMatch.groupValues[1], mapOf("index" to listIndex.toString())))
            remaining = remaining.substring(numberedMatch.range.last)
            continue
        }

        // Regular text - take until next special character
        val nextSpecial = findNextSpecialChar(remaining)
        if (nextSpecial > 0) {
            segments.add(MarkdownSegment(MarkdownSegmentType.Text, remaining.substring(0, nextSpecial)))
            remaining = remaining.substring(nextSpecial)
        } else {
            segments.add(MarkdownSegment(MarkdownSegmentType.Text, remaining))
            remaining = ""
        }
    }

    return segments
}

private fun findNextSpecialChar(text: String): Int {
    val specialChars = setOf('*', '_', '`', '[', '>', '-', '~', '#')
    return text.indexOfFirst { it in specialChars || it.isDigit() && text.length > text.indexOf(it) + 1 && text[text.indexOf(it) + 1] == '.' }
}

@Composable
fun CodeBlock(
    code: String,
    language: String = "",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.ui.graphics.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (language.isNotEmpty()) {
                Text(
                    text = language,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = code,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}