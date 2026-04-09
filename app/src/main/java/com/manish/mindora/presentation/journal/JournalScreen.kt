package com.manish.mindora.presentation.journal

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manish.mindora.R
import com.manish.mindora.presentation.util.countWords
import com.manish.mindora.ui.theme.MindoraPalette
import com.manish.mindora.ui.theme.MindoraSaveGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen(
    modifier: Modifier = Modifier,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    var text by rememberSaveable { mutableStateOf("") }
    val saveUiState by viewModel.saveUiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val genericError = stringResource(R.string.journal_save_error_generic)
    val wordLimitTemplate = stringResource(R.string.journal_word_limit_error, JournalViewModel.MAX_WORDS)

    val tagReflective = stringResource(R.string.journal_tag_reflective)
    val tagGrateful = stringResource(R.string.journal_tag_grateful)
    val tagTired = stringResource(R.string.journal_tag_tired)
    val tagOptions = remember(tagReflective, tagGrateful, tagTired) {
        listOf(tagReflective, tagGrateful, tagTired)
    }
    var selectedTags by rememberSaveable { mutableStateOf(setOf<String>()) }

    val dateChipText = remember {
        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
    }
    val wordCount = remember(text) { countWords(text) }
    val scroll = rememberScrollState()

    LaunchedEffect(saveUiState) {
        when (val s = saveUiState) {
            is JournalSaveUiState.Success -> {
                snackbarHostState.showSnackbar(s.feedback)
                text = ""
                selectedTags = emptySet()
                viewModel.consumeSaveUiState()
            }
            is JournalSaveUiState.Error -> {
                snackbarHostState.showSnackbar(s.message.ifBlank { genericError })
                viewModel.consumeSaveUiState()
            }
            is JournalSaveUiState.WordLimitExceeded -> {
                snackbarHostState.showSnackbar(wordLimitTemplate)
                viewModel.consumeSaveUiState()
            }
            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoStories,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                    Text(
                        text = stringResource(R.string.journal_header_primary),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f),
                ) {
                    Text(
                        text = dateChipText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                tagOptions.forEach { label ->
                    val selected = label in selectedTags
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedTags = if (selected) selectedTags - label else selectedTags + label
                        },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MindoraPalette.MintGreen.copy(alpha = 0.55f),
                            selectedLabelColor = MindoraPalette.TextPrimary,
                        ),
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                ) {
                    if (saveUiState is JournalSaveUiState.Saving) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.tertiary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Text(
                                text = stringResource(R.string.journal_ai_analyzing),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }

                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .testTag("journal_body_input")
                                    .drawBehind {
                                val lineHeight = 44.dp.toPx()
                                var y = lineHeight
                                val lineColor = MindoraPalette.Divider.copy(alpha = 0.35f)
                                while (y < size.height) {
                                    drawLine(
                                        color = lineColor,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = 1f,
                                    )
                                    y += lineHeight
                                }
                            },
                        minLines = 10,
                        maxLines = 14,
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text(stringResource(R.string.journal_placeholder)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        ),
                    )
                    Text(
                        text = stringResource(
                            R.string.journal_word_count_format,
                            wordCount,
                            JournalViewModel.MAX_WORDS,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp, end = 4.dp),
                    )
                }
            }

            Button(
                onClick = { viewModel.saveEntry(text, selectedTags) },
                enabled = saveUiState !is JournalSaveUiState.Saving && text.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("journal_save_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MindoraSaveGreen,
                    contentColor = Color.White,
                    disabledContainerColor = MindoraSaveGreen.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f),
                ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (saveUiState is JournalSaveUiState.Saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        stringResource(R.string.journal_submit),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
