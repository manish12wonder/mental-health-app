package com.manish.mindora.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.mindora.R
import com.manish.mindora.domain.model.JournalEntry
import com.manish.mindora.domain.model.Mood
import java.text.SimpleDateFormat
import java.util.Date

fun entryMoodLabelRes(mood: Mood): Int = when (mood) {
    Mood.Happy -> R.string.mood_display_peaceful
    Mood.Sad -> R.string.mood_display_sad
    Mood.Angry -> R.string.mood_display_anxious
    Mood.Neutral -> R.string.mood_display_neutral
}

fun entryMoodEmojiRes(mood: Mood): Int = when (mood) {
    Mood.Happy -> R.string.mood_emoji_peaceful
    Mood.Sad -> R.string.mood_emoji_sad
    Mood.Angry -> R.string.mood_emoji_anxious
    Mood.Neutral -> R.string.mood_emoji_neutral
}

@Composable
fun JournalEntryReadContent(
    entry: JournalEntry,
    detailTimeFormat: SimpleDateFormat,
    onClose: () -> Unit,
) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(horizontal = 24.dp)
            .padding(bottom = 8.dp)
            .navigationBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.journal_read_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = detailTimeFormat.format(Date(entry.createdAtEpochMillis)),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(entryMoodEmojiRes(entry.mood)),
                fontSize = 28.sp,
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
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
                text = stringResource(
                    R.string.journal_read_confidence,
                    (entry.confidence * 100).toInt().coerceIn(0, 100),
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.journal_read_entry_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = entry.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (entry.feedback.isNotBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.journal_read_support_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = entry.feedback,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(stringResource(R.string.journal_read_close))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
