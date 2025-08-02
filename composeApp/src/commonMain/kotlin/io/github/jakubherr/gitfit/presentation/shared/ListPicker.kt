package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A composable function that allows users to select an item from a list using a scrollable list with a text field for editing.
 *
 * @param initialValue The initial value to be selected in the list.
 * @param values The list of items.
 * @param modifier Modifier for customizing the appearance of the `ListPicker`.
 * @param wrapSelectorWheel Boolean flag indicating whether the list should wrap around like a selector wheel.
 * @param format A lambda function that formats an item into a string for display.
 * @param onValueChange A callback function that is invoked when the selected item changes.
 * @param onIsErrorChange A callback function that is invoked when the isError changes.
 * @param parse A lambda function that parses a string into an item.
 * @param enableEdition Boolean flag indicating whether the user can edit the selected item using a text field.
 * @param beyondViewportPageCount The number of pages to display on either side of the selected item.
 * @param textStyle The text style for the displayed items.
 * @param verticalPadding The vertical padding between items.
 * @param dividerColor The color of the horizontal dividers.
 * @param dividerThickness The thickness of the horizontal dividers.
 *
 * @author Reda El Madini - For support, contact gladiatorkilo@gmail.com
 */
@Composable
fun <E> ListPicker(
    initialValue: E,
    values: List<E>,
    onValueChange: (E) -> Unit,
    modifier: Modifier = Modifier,
    wrapSelectorWheel: Boolean = false,
    format: E.() -> String = { toString() },
    parse: (String.() -> E?)? = null,
    onIsErrorChange: (Boolean) -> Unit = {},
    enableEdition: Boolean = parse != null,
    beyondViewportPageCount: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current,
    verticalPadding: Dp = 16.dp,
    dividerColor: Color = MaterialTheme.colorScheme.outline,
    dividerThickness: Dp = 1.dp,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val listSize = values.size
    val coercedOutOfBoundsPageCount = beyondViewportPageCount.coerceIn(0..listSize / 2)
    val visibleItemsCount = 1 + coercedOutOfBoundsPageCount * 2
    val iteration =
        run {
            if (wrapSelectorWheel) {
                remember(key1 = coercedOutOfBoundsPageCount, key2 = listSize) {
                    (Int.MAX_VALUE - 2 * coercedOutOfBoundsPageCount) / listSize
                }
            } else {
                1
            }
        }
    val intervals =
        remember(key1 = coercedOutOfBoundsPageCount, key2 = iteration, key3 = listSize) {
            listOf(
                0,
                coercedOutOfBoundsPageCount,
                coercedOutOfBoundsPageCount + iteration * listSize,
                coercedOutOfBoundsPageCount + iteration * listSize + coercedOutOfBoundsPageCount,
            )
        }
    val scrollOfItemIndex = { it: Int ->
        it + (listSize * (iteration / 2))
    }
    val scrollOfItem = { item: E ->
        values.indexOf(item).takeIf { it != -1 }?.let { index -> scrollOfItemIndex(index) }
    }
    val lazyListState =
        rememberLazyListState(
            initialFirstVisibleItemIndex =
                remember(
                    key1 = initialValue,
                    key2 = listSize,
                    key3 = iteration,
                ) {
                    scrollOfItem(initialValue) ?: 0
                },
        )
    LaunchedEffect(key1 = values) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }.collectLatest {
            onValueChange(values[it % listSize])
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        var edit by rememberSaveable { mutableStateOf(false) }
        ComposeScope {
            AnimatedContent(
                targetState = edit,
                label = "AnimatedContent",
            ) { showTextField ->
                if (showTextField) {
                    var isError by rememberSaveable { mutableStateOf(false) }
                    val initialSelectedItem =
                        remember {
                            values[lazyListState.firstVisibleItemIndex % listSize]
                        }
                    var value by rememberSaveable {
                        mutableStateOf(initialSelectedItem.format())
                    }
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(key1 = Unit) {
                        focusRequester.requestFocus()
                    }
                    val coroutineScope = rememberCoroutineScope()
                    ComposeScope {
                        TextField(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                            value = value,
                            onValueChange = { string ->
                                value = string
                                parse?.invoke(string).let { item ->
                                    isError =
                                        kotlin.run {
                                            if (item != null) {
                                                !values.contains(item) // true: item not found
                                            } else {
                                                true // string cannot be parsed
                                            }
                                        }
                                    if (isError) {
                                        onValueChange(initialSelectedItem)
                                    } else {
                                        onValueChange(item ?: initialSelectedItem)
                                    }
                                    onIsErrorChange(isError)
                                }
                            },
                            textStyle = textStyle.copy(textAlign = TextAlign.Center),
                            keyboardOptions =
                                KeyboardOptions.Default.copy(
                                    keyboardType = keyboardType,
                                    imeAction = if (!isError) ImeAction.Done else ImeAction.Default,
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        if (!isError) {
                                            parse?.invoke(value)?.let { item ->
                                                scrollOfItem(item)?.let { scroll ->
                                                    coroutineScope.launch {
                                                        lazyListState.scrollToItem(scroll)
                                                    }
                                                }
                                            }
                                            edit = false
                                        }
                                    },
                                ),
                            isError = isError,
                            colors =
                                TextFieldDefaults.colors().copy(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    errorContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                    errorTextColor = MaterialTheme.colorScheme.error,
                                ),
                        )
                    }
                } else {
                    val itemHeight = textStyle.lineHeight.toDp() + verticalPadding * 2F
                    LazyColumn(
                        state = lazyListState,
                        flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(itemHeight * visibleItemsCount)
                                .fadingEdge(
                                    brush =
                                        remember {
                                            Brush.verticalGradient(
                                                0F to Color.Transparent,
                                                0.5F to Color.Black,
                                                1F to Color.Transparent,
                                            )
                                        },
                                ),
                    ) {
                        items(
                            count = intervals.last(),
                            key = { it },
                        ) { index ->
                            val enabled by remember(key1 = index, key2 = enableEdition) {
                                derivedStateOf {
                                    enableEdition && (index == (lazyListState.firstVisibleItemIndex + coercedOutOfBoundsPageCount))
                                }
                            }
                            val textModifier = Modifier.padding(vertical = verticalPadding)
                            when (index) {
                                in intervals[0]..<intervals[1] ->
                                    Text(
                                        text =
                                            if (wrapSelectorWheel) {
                                                values[(index - coercedOutOfBoundsPageCount + listSize) % listSize]
                                                    .format()
                                            } else {
                                                ""
                                            },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = textStyle,
                                        modifier = textModifier,
                                    )

                                in intervals[1]..<intervals[2] -> {
                                    Text(
                                        text = values[(index - coercedOutOfBoundsPageCount) % listSize].format(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = textStyle,
                                        modifier =
                                            textModifier.then(
                                                Modifier.clickable(
                                                    onClick = { edit = true },
                                                    enabled = enabled,
                                                ),
                                            ),
                                    )
                                }

                                in intervals[2]..<intervals[3] ->
                                    Text(
                                        text =
                                            if (wrapSelectorWheel) {
                                                values[(index - coercedOutOfBoundsPageCount) % listSize].format()
                                            } else {
                                                ""
                                            },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = textStyle,
                                        modifier = textModifier,
                                    )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.offset(y = itemHeight * coercedOutOfBoundsPageCount - dividerThickness / 2),
                        thickness = dividerThickness,
                        color = dividerColor,
                    )

                    HorizontalDivider(
                        modifier = Modifier.offset(y = itemHeight * (coercedOutOfBoundsPageCount + 1) - dividerThickness / 2),
                        thickness = dividerThickness,
                        color = dividerColor,
                    )
                }
            }
        }
    }
}

@Composable
fun ComposeScope(content: @Composable () -> Unit) {
    content()
}

@Stable
fun Modifier.fadingEdge(brush: Brush) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.DstIn)
        }

@Composable
fun TextUnit.toDp() = LocalDensity.current.spToDp(this)

fun Density.spToDp(sp: TextUnit) = if (sp.isSpecified) sp.toDp() else Dp.Unspecified
