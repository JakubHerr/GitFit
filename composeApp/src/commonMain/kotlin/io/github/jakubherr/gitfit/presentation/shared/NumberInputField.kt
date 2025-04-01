package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.jakubherr.gitfit.domain.isNonNegativeDouble
import io.github.jakubherr.gitfit.domain.isNonNegativeInt
import io.github.jakubherr.gitfit.domain.validDecimals

// For user input sanitation, one of these input fields should ALWAYS be used
// maxLength prevents users from entering numbers larger than the KoalaGraph library can handle
// for example, calculating workout volume with extreme weight and repetitions will cause the library to freeze or crash

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: Int = 0,
    label: String? = null,
    isError: Boolean = false,
    maxlength: Int,
) {
    OutlinedTextField(
        value,
        onValueChange = { if (it.length <= maxlength) onValueChange(it) },
        modifier = modifier,
        placeholder = { Text(placeholder.toString(), Modifier.alpha(0.6f)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        singleLine = true,
    )
}

@Composable
fun IntegerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.width(64.dp),
    placeholder: Int = 0,
    label: String? = null,
    isError: Boolean = !value.isNonNegativeInt(),
    maxlength: Int = 3, // nobody is doing more than 999 kg or 999 reps
) {
    NumberInputField(
        value,
        onValueChange,
        modifier,
        placeholder,
        label,
        isError,
        maxlength
    )
}

// by default, this constrains numbers with decimal input to two decimal places
@Composable
fun DoubleInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.width(64.dp),
    placeholder: Int = 0,
    label: String? = null,
    isError: Boolean = !value.isNonNegativeDouble(),
    maxlength: Int = 6,
    maxDecimals: Int = 2,
) {
    NumberInputField(
        value,
        onValueChange = {
            if (it.validDecimals(maxDecimals)) onValueChange(it)
        },
        modifier,
        placeholder,
        label,
        isError,
        maxlength
    )
}
