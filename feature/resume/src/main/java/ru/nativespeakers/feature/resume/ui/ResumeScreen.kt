package ru.nativespeakers.feature.resume.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.nativespeakers.core.designsystem.Base10
import ru.nativespeakers.core.designsystem.Base8
import ru.nativespeakers.core.ui.photo.toPersonAndPhotoUiState
import ru.nativespeakers.core.ui.screen.ErrorScreen
import ru.nativespeakers.core.ui.screen.LoadingScreen
import ru.nativespeakers.core.ui.track.TrackMemberSection
import ru.nativespeakers.core.ui.vacancy.SalaryBlock
import ru.nativespeakers.feature.resume.R
import ru.nativespeakers.feature.vacancy.details.ui.TagsSection

@Composable
internal fun ResumeScreen(
    resumeId: Long,
    navigateBack: () -> Unit,
) {
    val viewModel = hiltViewModel(
        creationCallback = { factory: ResumeViewModel.Factory ->
            factory.create(resumeId)
        },
    )

    when {
        viewModel.isLoading && !viewModel.isLoaded -> LoadingScreen()
        viewModel.isError && !viewModel.isLoaded -> ErrorScreen(viewModel::loadData)
        viewModel.isLoaded -> ResumeScreenContent(
            viewModel = viewModel,
            navigateBack = navigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResumeScreenContent(
    viewModel: ResumeViewModel,
    navigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val resume = viewModel.resume.value!!
    val candidate = viewModel.candidate.value!!

    Scaffold(
        topBar = {
            Header(
                onBackPressed = navigateBack,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ResumeNameAndSalary(
                name = resume.name,
                salaryLowerBound = resume.salaryMin,
                modifier = Modifier.fillMaxWidth()
            )

            TrackMemberSection(
                isStaff = false,
                state = candidate.toPersonAndPhotoUiState(),
            )

            TagsSection(
                tags = resume.tags.groupBy { it.category },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.feature_resume_description),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = resume.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ResumeNameAndSalary(
    name: String,
    salaryLowerBound: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.displayMedium,
            fontSize = 27.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )

        SalaryBlock(
            salaryLowerBoundRub = salaryLowerBound,
            salaryHigherBoundRub = null,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    onBackPressed: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.feature_resume_title),
                style = MaterialTheme.typography.titleLarge,
                color = Base10,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            IconButton(
                onClick = onBackPressed,
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Base8
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(elevation = 4.dp)
    )
}