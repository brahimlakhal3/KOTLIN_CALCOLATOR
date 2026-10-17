package com.brahim.calculator.component

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brahim.calculator.BuildConfig
import com.brahim.calculator.R
import com.brahim.calculator.element.ContactButton
import com.brahim.calculator.ui.theme.Motion
import com.brahim.calculator.viewModel.AppConfigVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShell(configViewModel: AppConfigVM) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.fromRoute(backStackEntry?.destination?.route)
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var reusedExpression by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler(enabled = drawerState.isOpen) { scope.launch { drawerState.close() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                current = currentScreen,
                onSelect = { screen ->
                    scope.launch { drawerState.close() }
                    if (screen != currentScreen) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        },
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(tween(Motion.SHORT_2)) togetherWith fadeOut(tween(Motion.SHORT_1))
                            },
                            label = "title",
                        ) { screen ->
                            Text(
                                text = stringResource(screen.titleRes),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = stringResource(R.string.menu),
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        ),
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Calculator.route,
                enterTransition = {
                    fadeIn(Motion.enter(Motion.MEDIUM_1)) +
                            slideInHorizontally(
                                animationSpec = Motion.enter<IntOffset>(Motion.MEDIUM_2),
                                initialOffsetX = { it / 10 },
                            )
                },
                exitTransition = {
                    fadeOut(Motion.exit()) +
                            slideOutHorizontally(
                                animationSpec = Motion.exit<IntOffset>(),
                                targetOffsetX = { -it / 10 },
                            )
                },
                popEnterTransition = {
                    fadeIn(Motion.enter(Motion.MEDIUM_1)) +
                            slideInHorizontally(
                                animationSpec = Motion.enter<IntOffset>(Motion.MEDIUM_2),
                                initialOffsetX = { -it / 10 },
                            )
                },
                popExitTransition = {
                    fadeOut(Motion.exit()) +
                            slideOutHorizontally(
                                animationSpec = Motion.exit<IntOffset>(),
                                targetOffsetX = { it / 10 },
                            )
                },
            ) {
                composable(Screen.Calculator.route) {
                    CalculatorScreen(
                        onOpenHistory = { navController.navigate(Screen.History.route) },
                        reusedExpression = reusedExpression,
                        onReusedExpressionHandled = { reusedExpression = null },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
                composable(Screen.History.route) {
                    HistoryScreen(
                        onReuse = { expression ->
                            reusedExpression = expression
                            navController.navigate(Screen.Calculator.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        configViewModel = configViewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun AppDrawer(current: Screen, onSelect: (Screen) -> Unit) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        DrawerHeader()

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
        Spacer(Modifier.height(12.dp))

        Screen.entries.forEach { screen ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = stringResource(screen.titleRes),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                selected = screen == current,
                onClick = { onSelect(screen) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }

        Spacer(Modifier.weight(1f))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))

        Text(
            text = stringResource(R.string.contact_us),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 28.dp, top = 20.dp, bottom = 12.dp),
        )

        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ContactButton(
                description = "Facebook",
                onClick = { openLink(context, "https://www.facebook.com") },
            ) {
                Icon(
                    Icons.Default.Facebook,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
            ContactButton(
                description = "WhatsApp",
                onClick = { openLink(context, "https://wa.me/212706495519") },
            ) {
                Icon(
                    Icons.Default.Whatsapp,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
            ContactButton(
                description = "LinkedIn",
                onClick = { openLink(context, "https://www.linkedin.com") },
            ) {
                Icon(
                    painter = painterResource(R.drawable.linkedin),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                )
            }
            ContactButton(
                description = "Instagram",
                onClick = { openLink(context, "https://www.instagram.com") },
            ) {
                Icon(
                    painter = painterResource(R.drawable.instagram),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Text(
            text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 28.dp, top = 20.dp, bottom = 24.dp),
        )
    }
}

@Composable
private fun DrawerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, top = 32.dp, end = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Calculate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun openLink(context: Context, url: String) {
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
}
