package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.cancel
import gitfit.composeapp.generated.resources.confirm
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.ceil

@Composable
@Preview
fun TimePickerDialog(
    onDismiss: () -> Unit = {},
    onConfirm: (Long) -> Unit = {},
) {
    val timeSeconds =
        listOf(
            0,
            5,
            10,
            15,
            20,
            25,
            30,
            35,
            40,
            45,
            50,
            55,
            60,
            70,
            80,
            90,
            100,
            110,
            120,
            130,
            140,
            150,
            160,
            170,
            180,
            195,
            210,
            225,
            240,
            255,
            270,
            285,
            300,
        )

    val middle = ceil(timeSeconds.lastIndex / 2.0).toInt()
    var selectedTime by remember { mutableIntStateOf(middle) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier.width(256.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                ListPicker(
                    timeSeconds[middle],
                    timeSeconds,
                    format = { "${this / 60}m${this % 60}s" },
                    onValueChange = {
                        selectedTime = it
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(
                            stringResource(Res.string.cancel),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    TextButton(
                        onClick = { onConfirm(selectedTime.toLong()) },
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
