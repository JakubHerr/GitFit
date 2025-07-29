package io.github.jakubherr.gitfit.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.navigate_back
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitFitTopAppBar(
    title: String?,
    showBackButton: Boolean,
    onBack: () -> Unit = { },
) {
    AnimatedVisibility(
        title != null,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Companion.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Companion.Center),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(title ?: "") },
            navigationIcon = {
                if (showBackButton) {
                    IconButton({ if (title != null) onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.navigate_back))
                    }
                }
            },
        )
    }
}
