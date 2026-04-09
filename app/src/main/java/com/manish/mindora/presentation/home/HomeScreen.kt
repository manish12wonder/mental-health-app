package com.manish.mindora.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manish.mindora.R
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.ui.theme.MindoraMoodChart
import com.manish.mindora.ui.theme.MindoraPalette

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenJournal: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val savingMood by viewModel.quickMoodSaving.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    val fallbackName = stringResource(R.string.home_display_name_fallback)
    val userName = state.userDisplayName.ifBlank { fallbackName }
    val initials = remember(userName) {
        userName.trim().take(2).uppercase()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HomeTopBar(
            greetingResId = state.greetingResId,
            userName = userName,
            initials = initials,
            onOpenSettings = onOpenSettings,
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            else -> {
                TodayMoodSummaryCard(entry = state.todayEntry)
                MoodPickerCard(
                    savingMood = savingMood,
                    onPick = { viewModel.saveQuickMood(it) },
                )
            }
        }

        FilledTonalButton(
            onClick = onOpenJournal,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(Icons.Outlined.EditNote, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.home_cta_journal))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = stringResource(R.string.crisis_line_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    greetingResId: Int,
    userName: String,
    initials: String,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(greetingResId),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.home_hey_user, userName),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings_open_content_description),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp),
                tonalElevation = 2.dp,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayMoodSummaryCard(
    entry: com.manish.mindora.domain.model.JournalEntry?,
) {
    val borderGreen = MindoraPalette.MintGreen.copy(alpha = 0.85f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.5.dp, borderGreen),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (entry == null) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_today_mood_heading),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.home_today_mood_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } else {
                Text(
                    text = moodEmoji(entry.mood),
                    fontSize = 40.sp,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_today_mood_heading),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = moodDisplayLabel(entry.mood),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MindoraPalette.TextPrimary,
                    )
                    Text(
                        text = stringResource(
                            R.string.home_confidence_short,
                            (entry.confidence * 100).toInt().coerceIn(0, 100),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodPickerCard(
    savingMood: Mood?,
    onPick: (Mood) -> Unit,
) {
    val borderBlue = MindoraPalette.SkyBlue.copy(alpha = 0.9f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.5.dp, borderBlue),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.home_mood_prompt_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MindoraPalette.SurfaceBluishWhite,
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MoodPickTile(
                        modifier = Modifier.weight(1f),
                        emoji = stringResource(R.string.mood_emoji_peaceful),
                        label = stringResource(R.string.mood_happy),
                        containerColor = MindoraMoodChart.Happy.copy(alpha = 0.35f),
                        loading = savingMood == Mood.Happy,
                        onClick = { onPick(Mood.Happy) },
                    )
                    MoodPickTile(
                        modifier = Modifier.weight(1f),
                        emoji = stringResource(R.string.mood_emoji_sad),
                        label = stringResource(R.string.mood_sad),
                        containerColor = MindoraMoodChart.Sad.copy(alpha = 0.35f),
                        loading = savingMood == Mood.Sad,
                        onClick = { onPick(Mood.Sad) },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MoodPickTile(
                        modifier = Modifier.weight(1f),
                        emoji = stringResource(R.string.mood_emoji_anxious),
                        label = stringResource(R.string.mood_angry),
                        containerColor = MindoraMoodChart.Angry.copy(alpha = 0.3f),
                        loading = savingMood == Mood.Angry,
                        onClick = { onPick(Mood.Angry) },
                    )
                    MoodPickTile(
                        modifier = Modifier.weight(1f),
                        emoji = stringResource(R.string.mood_emoji_neutral),
                        label = stringResource(R.string.mood_neutral),
                        containerColor = MindoraMoodChart.Neutral.copy(alpha = 0.35f),
                        loading = savingMood == Mood.Neutral,
                        onClick = { onPick(Mood.Neutral) },
                    )
                }
            }
            if (savingMood != null) {
                Text(
                    text = stringResource(R.string.home_quick_mood_saving),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun MoodPickTile(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    containerColor: Color,
    loading: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = !loading,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = emoji, fontSize = 26.sp)
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun moodDisplayLabel(mood: Mood): String = stringResource(
    when (mood) {
        Mood.Happy -> R.string.mood_display_peaceful
        Mood.Sad -> R.string.mood_display_sad
        Mood.Angry -> R.string.mood_display_anxious
        Mood.Neutral -> R.string.mood_display_neutral
    },
)

@Composable
private fun moodEmoji(mood: Mood): String = stringResource(
    when (mood) {
        Mood.Happy -> R.string.mood_emoji_peaceful
        Mood.Sad -> R.string.mood_emoji_sad
        Mood.Angry -> R.string.mood_emoji_anxious
        Mood.Neutral -> R.string.mood_emoji_neutral
    },
)
