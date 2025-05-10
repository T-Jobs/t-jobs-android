package ru.nativespeakers.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.datetime.LocalDateTime
import ru.nativespeakers.core.ui.R.string as coreUiStrings

@Composable
fun LocalDateTime?.toUi(): String {
    if (this == null) return "--:--"

    val monthString = monthNumberToString(monthNumber)
    val hourFormat = "%02d".format(hour)
    val minuteFormat = "%02d".format(minute)
    return "$dayOfMonth $monthString $hourFormat:$minuteFormat"
}

@Composable
private fun monthNumberToString(month: Int) = when (month) {
    1 -> stringResource(coreUiStrings.core_ui_january)
    2 -> stringResource(coreUiStrings.core_ui_february)
    3 -> stringResource(coreUiStrings.core_ui_march)
    4 -> stringResource(coreUiStrings.core_ui_april)
    5 -> stringResource(coreUiStrings.core_ui_may)
    6 -> stringResource(coreUiStrings.core_ui_june)
    7 -> stringResource(coreUiStrings.core_ui_july)
    8 -> stringResource(coreUiStrings.core_ui_august)
    9 -> stringResource(coreUiStrings.core_ui_september)
    10 -> stringResource(coreUiStrings.core_ui_october)
    11 -> stringResource(coreUiStrings.core_ui_november)
    12 -> stringResource(coreUiStrings.core_ui_december)
    else -> throw IllegalStateException("Months are numbered from 1 to 12")
}