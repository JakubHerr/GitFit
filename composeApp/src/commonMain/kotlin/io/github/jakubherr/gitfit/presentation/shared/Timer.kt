package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun Timer(
    time: Long,
    timeLeft: Long,
    onSkip: () -> Unit = {},
    onChangeTimer: (Long) -> Unit = {},
) {
    Card(
        Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().wrapContentHeight().padding(8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onChangeTimer(-15) }
                ) {
                    Text("-15s")
                }

                Text("${timeLeft / 60}m:${timeLeft % 60}s")

                TextButton(
                    onClick = { onChangeTimer(+15) }
                ) {
                    Text("+15s")
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LinearProgressIndicator(progress = { 1.0f - (timeLeft.toFloat() / time.toFloat()) })

                Button(
                    onClick = onSkip
                ) {
                    Text("Skip")
                }
            }
        }
    }
}
