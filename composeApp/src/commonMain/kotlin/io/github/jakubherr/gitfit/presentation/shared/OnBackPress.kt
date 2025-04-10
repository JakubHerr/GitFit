package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.runtime.Composable

// https://medium.com/@kerry.bisset/implementing-soft-navigation-requests-in-jetpack-compose-navigation-part-2-the-back-press-15b661fa550d
@Composable
expect fun OnBackPress(handler: () -> Unit)
