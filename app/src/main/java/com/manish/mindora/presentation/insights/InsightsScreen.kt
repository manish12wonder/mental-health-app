package com.manish.mindora.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manish.mindora.R
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import com.manish.mindora.presentation.components.JournalEntryReadContent
import com.manish.mindora.presentation.components.entryMoodLabelRes
import com.manish.mindora.ui.theme.MindoraMoodChart
import com.manish.mindora.ui.theme.MindoraPalette
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedEntry = remember(selectedEntryId, state.recentEntries) {
        selectedEntryId?.let { id -> state.recentEntries.firstOrNull { it.id == id } }
    }
    val entryTimeFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val entryDetailTimeFormat = remember {
        SimpleDateFormat("EEE, MMM d, yyyy • h:mm a", Locale.getDefault())
    }
    val readSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
        item {
            InsightsHeader(weekRangeLabel = state.weekRangeLabel)
        }

        item {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        InsightMiniCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.insights_journaled_title),
                            body = stringResource(
                                R.string.insights_journaled_subtitle,
                                state.daysWithMoodThisWeek,
                            ),
                            container = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        )
                        InsightMiniCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.insights_top_mood_title),
                            body = topMoodLine(state.topMood),
                            emoji = state.topMood?.let { topMoodEmoji(it) },
                            container = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.insights_weekly_trend),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MindoraPalette.TextPrimary,
            )
            Text(
                text = stringResource(R.string.insights_weekly_rule),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
        }

        item {
            if (!state.isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        WeeklyMoodChart(slots = state.weeklySlots)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            state.weeklySlots.forEach { slot ->
                                Text(
                                    text = dayLetter(slot.dayStartMillis),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.insights_chart_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
                MoodLegendRow()
            }
        }

        item {
            SpotlightCard()
        }

        item {
            Text(
                text = stringResource(R.string.insights_recent_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            if (state.recentEntries.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.journal_past_entries_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        if (state.recentEntries.isEmpty() && !state.isLoading) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.insights_no_entries),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(state.recentEntries, key = { it.id }) { entry ->
                JournalEntryRow(
                    entry = entry,
                    timeFormat = entryTimeFormat,
                    onOpen = { selectedEntryId = entry.id },
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
        }

        val openEntry = selectedEntry
        if (openEntry != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedEntryId = null },
                sheetState = readSheetState,
            ) {
                JournalEntryReadContent(
                    entry = openEntry,
                    detailTimeFormat = entryDetailTimeFormat,
                    onClose = { selectedEntryId = null },
                )
            }
        }
    }
}

@Composable
private fun InsightsHeader(weekRangeLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.insights_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MindoraPalette.TextPrimary,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "‹",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = weekRangeLabel.ifEmpty { "—" },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InsightMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    container: Color,
    emoji: String? = null,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MindoraPalette.TextSecondary,
            )
            if (emoji != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(text = emoji, fontSize = 22.sp)
                    Text(
                        text = body,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MindoraPalette.TextPrimary,
                    )
                }
            } else {
                Text(
                    text = body,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MindoraPalette.TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun topMoodLine(mood: Mood?): String = when (mood) {
    null -> stringResource(R.string.insights_top_mood_none)
    Mood.Happy -> stringResource(R.string.insights_top_mood_calm)
    Mood.Sad -> stringResource(R.string.mood_display_sad)
    Mood.Angry -> stringResource(R.string.mood_display_anxious)
    Mood.Neutral -> stringResource(R.string.mood_display_neutral)
}

@Composable
private fun topMoodEmoji(mood: Mood): String = stringResource(
    when (mood) {
        Mood.Happy -> R.string.mood_emoji_peaceful
        Mood.Sad -> R.string.mood_emoji_sad
        Mood.Angry -> R.string.mood_emoji_anxious
        Mood.Neutral -> R.string.mood_emoji_neutral
    },
)

@Composable
private fun MoodLegendRow() {
    Column(
        modifier = Modifier.padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.insights_legend_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LegendDot(color = MindoraMoodChart.Happy, label = stringResource(R.string.mood_happy))
            LegendDot(color = MindoraMoodChart.Sad, label = stringResource(R.string.mood_sad))
            LegendDot(color = MindoraMoodChart.Neutral, label = stringResource(R.string.mood_neutral))
            LegendDot(color = MindoraMoodChart.Angry, label = stringResource(R.string.insights_legend_anxious))
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SpotlightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.insights_spotlight_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Text(
                    text = stringResource(R.string.insights_spotlight_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f),
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
            ) {
                Text(
                    text = stringResource(R.string.insights_spotlight_stat),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MindoraPalette.TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun JournalEntryRow(
    entry: JournalEntry,
    timeFormat: SimpleDateFormat,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(role = Role.Button, onClick = onOpen),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                ) {
                    Text(
                        text = stringResource(entryMoodLabelRes(entry.mood)),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
                Text(
                    text = timeFormat.format(Date(entry.createdAtEpochMillis)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = entry.text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (entry.feedback.isNotBlank()) {
                Text(
                    text = entry.feedback,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private fun dayLetter(millis: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "S"
        Calendar.MONDAY -> "M"
        Calendar.TUESDAY -> "T"
        Calendar.WEDNESDAY -> "W"
        Calendar.THURSDAY -> "T"
        Calendar.FRIDAY -> "F"
        Calendar.SATURDAY -> "S"
        else -> "?"
    }
}

