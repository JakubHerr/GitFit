package io.github.jakubherr.gitfit.presentation.shared

import androidx.compose.runtime.Composable
import gitfit.composeapp.generated.resources.Res
import gitfit.composeapp.generated.resources.enum_muscle_group_abs
import gitfit.composeapp.generated.resources.enum_muscle_group_arms
import gitfit.composeapp.generated.resources.enum_muscle_group_back
import gitfit.composeapp.generated.resources.enum_muscle_group_chest
import gitfit.composeapp.generated.resources.enum_muscle_group_forearms
import gitfit.composeapp.generated.resources.enum_muscle_group_legs
import gitfit.composeapp.generated.resources.enum_muscle_group_shoulders
import io.github.jakubherr.gitfit.domain.model.MuscleGroup
import org.jetbrains.compose.resources.stringResource

fun Double.toPrettyString(): String = if (this % 1.0 == 0.0) this.toInt().toString() else toString()

@Composable
fun MuscleGroup.translation() =
    when (this) {
        MuscleGroup.ARMS -> stringResource(Res.string.enum_muscle_group_arms)
        MuscleGroup.LEGS -> stringResource(Res.string.enum_muscle_group_legs)
        MuscleGroup.SHOULDERS -> stringResource(Res.string.enum_muscle_group_shoulders)
        MuscleGroup.BACK -> stringResource(Res.string.enum_muscle_group_back)
        MuscleGroup.ABS -> stringResource(Res.string.enum_muscle_group_abs)
        MuscleGroup.CHEST -> stringResource(Res.string.enum_muscle_group_chest)
        MuscleGroup.FOREARMS -> stringResource(Res.string.enum_muscle_group_forearms)
    }
