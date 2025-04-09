package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.ic_launcher_foreground
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GitFitLogo() {
    Text("GitFit", style = MaterialTheme.typography.headlineLarge)
    Image(
        imageVector = vectorResource(Res.drawable.ic_launcher_foreground),
        "",
        modifier = Modifier.size(128.dp),
        contentScale = ContentScale.FillBounds,
    )
}