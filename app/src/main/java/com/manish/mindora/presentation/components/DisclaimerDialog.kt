package com.manish.mindora.presentation.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.manish.mindora.R

@Composable
fun DisclaimerDialog(
    onAcknowledge: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(R.string.disclaimer_title)) },
        text = {
            Text(
                text = stringResource(R.string.disclaimer_body),
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = onAcknowledge) {
                Text(stringResource(R.string.disclaimer_acknowledge))
            }
        },
    )
}
