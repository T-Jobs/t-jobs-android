package ru.nativespeakers.core.ui.date

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.nativespeakers.core.designsystem.Base1
import ru.nativespeakers.core.designsystem.Base6
import ru.nativespeakers.core.designsystem.Primary1
import ru.nativespeakers.core.designsystem.Primary6
import ru.nativespeakers.core.designsystem.Primary7
import ru.nativespeakers.core.ui.R
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetOption
import ru.nativespeakers.core.ui.bottomsheet.BottomSheetWithOptions
import ru.nativespeakers.core.ui.toUi
import java.util.Calendar

@Composable
private fun rememberDatePickerOptions(
    onAutoChooseClick: () -> Unit,
    onChooseByHandClick: () -> Unit,
): List<BottomSheetOption> {
    val autoChooseString = stringResource(R.string.core_ui_auto_choose)
    val chooseByHandString = stringResource(R.string.core_ui_choose_by_hand)
    return remember(onAutoChooseClick, onChooseByHandClick) {
        listOf(
            BottomSheetOption(
                name = autoChooseString,
                leadingIcon = Icons.Outlined.Settings,
                onClick = onAutoChooseClick,
            ),
            BottomSheetOption(
                name = chooseByHandString,
                leadingIcon = Icons.Outlined.EditCalendar,
                onClick = onChooseByHandClick,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateSection(
    selectedDate: LocalDateTime?,
    onAutoChooseClick: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    selectDateEnabled: Boolean = true,
) {
    var datePickerVisible by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var timePickerVisible by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    var selectedDateMs by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }

    var bottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(Base1)
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                tint = Base6,
                contentDescription = null,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        val string =
            selectedDate?.toUi() ?: stringResource(R.string.core_ui_auto)
        Text(
            text = string,
            overflow = TextOverflow.Ellipsis,
            color = Base6,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        ChangeButton(
            imageVector = Icons.Outlined.EditCalendar,
            text = stringResource(R.string.core_ui_change),
            onClick = { bottomSheetVisible = true },
            enabled = selectDateEnabled,
        )
    }

    if (datePickerVisible) {
        SelectDateDialog(
            datePickerState = datePickerState,
            onDismiss = { datePickerVisible = false },
            onDateSelected = { selectedDateMs = it },
        )
    }

    if (timePickerVisible) {
        SelectTimeDialog(
            timePickerState = timePickerState,
            onConfirm = {
                selectedMinute = timePickerState.minute
                selectedHour = timePickerState.hour
                timePickerVisible = false
            },
            onDismiss = { timePickerVisible = false }
        )
    }

    // Opens just after date is selected
    LaunchedEffect(selectedDateMs) {
        if (selectedDateMs != null) {
            timePickerVisible = true

            val currentTime = Calendar.getInstance()
            timePickerState.is24hour = true
            timePickerState.minute = currentTime.get(Calendar.MINUTE)
            timePickerState.hour = currentTime.get(Calendar.HOUR_OF_DAY)
        }
    }

    // Reset all the time if even one was not selected
    LaunchedEffect(selectedHour, selectedMinute) {
        if (selectedHour == null || selectedMinute == null) {
            selectedDateMs = null
            selectedHour = null
            selectedMinute = null

            onAutoChooseClick()
        }
    }

    // Date was selected
    LaunchedEffect(selectedDateMs, selectedMinute, selectedHour) {
        val dateMs = selectedDateMs
        val minutes = selectedMinute
        val hours = selectedHour

        if (dateMs != null && minutes != null && hours != null) {
            val date = createLocalDateTime(dateMs, hours, minutes)
            onDateSelected(date)
        }
    }

    val options = rememberDatePickerOptions(
        onAutoChooseClick = {
            onAutoChooseClick()
            bottomSheetVisible = false
        },
        onChooseByHandClick = {
            datePickerVisible = true
            bottomSheetVisible = false
        }
    )

    if (bottomSheetVisible) {
        BottomSheetWithOptions(
            options = options,
            onDismissRequest = {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        bottomSheetVisible = false
                    }
                }
            },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChangeButton(
    imageVector: ImageVector,
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary1,
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
            tint = Primary6,
            contentDescription = null,
        )

        if (text != null) {
            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                color = Primary7,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun createLocalDateTime(dateMs: Long, hours: Int, minutes: Int): LocalDateTime {
    val datePart = Instant.fromEpochMilliseconds(dateMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val timePart = LocalTime(hours, minutes, 0)
    return LocalDateTime(datePart, timePart)
}