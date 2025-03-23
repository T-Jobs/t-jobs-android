package ru.nativespeakers.core.ui.role

import androidx.compose.runtime.Composable
import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.core.ui.LocalAppRoles

@Composable
fun isHr() = AppRole.HR in LocalAppRoles.current

@Composable
fun isInterviewer() = AppRole.INTERVIEWER in LocalAppRoles.current

@Composable
fun isTeamLead() = AppRole.TEAM_LEAD in LocalAppRoles.current