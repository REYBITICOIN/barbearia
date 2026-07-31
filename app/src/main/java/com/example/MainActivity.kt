package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BarberLabViewModel
import com.example.ui.screens.*
import com.example.ui.components.BarberAppSplashScreen
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberGold
import com.example.ui.theme.BarberLabTheme
import com.example.ui.theme.parseHexColor

class MainActivity : ComponentActivity() {

    private val viewModel: BarberLabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val activeShop by viewModel.activeBarbershop.collectAsState()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            BarberLabTheme(
                darkTheme = isDarkMode,
                primaryHexOverride = activeShop?.primaryColorHex
            ) {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: BarberLabViewModel) {
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSplashScreen by remember { mutableStateOf(true) }

    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    // Fullscreen 10s Intro Splash Video with "Pular" button (Corner Top-Right)
    if (showSplashScreen) {
        BarberAppSplashScreen(
            shopName = activeShop?.name ?: "Barbearia do João - Corte Moderno e Clássico",
            onDismiss = { showSplashScreen = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Barbearia Logo",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "BARBEARIA DO JOÃO",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Corte Moderno e Clássico",
                                fontSize = 11.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Replay Intro Video Splash Screen
                    IconButton(
                        onClick = { showSplashScreen = true },
                        modifier = Modifier.testTag("btn_replay_splash")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Ver Vídeo Splash Barbearia",
                            tint = BarberGold
                        )
                    }

                    // Theme Selector Toggle Button (Claro / Escuro)
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier
                            .testTag("btn_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Alternar para Modo Claro" else "Alternar para Modo Escuro",
                            tint = if (isDarkMode) BarberGold else BarberAiCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Login / Profile Button
                    Surface(
                        onClick = { showLoginDialog = true },
                        color = if (currentUser != null) primaryColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_top_login")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (currentUser != null) Icons.Default.AccountCircle else Icons.Default.LockOpen,
                                contentDescription = "Login",
                                tint = if (currentUser != null) primaryColor else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Entrar",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = BarberAiCyan.copy(alpha = 0.2f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(BarberAiCyan, androidx.compose.foundation.shape.CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Multi-Tenant", color = BarberAiCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Default.Dashboard else Icons.Outlined.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(if (selectedTab == 1) Icons.Default.PhotoCamera else Icons.Outlined.PhotoCamera, contentDescription = "Câmera") },
                    label = { Text("Câmera", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_camera")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(if (selectedTab == 2) Icons.Default.Palette else Icons.Outlined.Palette, contentDescription = "Marca") },
                    label = { Text("Marca", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_branding")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(if (selectedTab == 3) Icons.Default.SmartToy else Icons.Outlined.SmartToy, contentDescription = "Agente IA") },
                    label = { Text("Agente IA", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_agent")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(if (selectedTab == 4) Icons.Default.Campaign else Icons.Outlined.Campaign, contentDescription = "Marketing") },
                    label = { Text("Marketing", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_marketing")
                )

                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(if (selectedTab == 5) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth, contentDescription = "Agenda") },
                    label = { Text("Agenda", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_appointments")
                )

                NavigationBarItem(
                    selected = selectedTab == 6,
                    onClick = { selectedTab = 6 },
                    icon = { Icon(if (selectedTab == 6) Icons.Default.Terminal else Icons.Outlined.Terminal, contentDescription = "Deploy") },
                    label = { Text("Deploy/SQL", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = primaryColor
                    ),
                    modifier = Modifier.testTag("nav_deploy")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TenantDashboardScreen(viewModel = viewModel, onNavigateToTab = { selectedTab = it })
                1 -> BarberCameraGalleryScreen(viewModel = viewModel)
                2 -> BrandingCustomizerScreen(viewModel = viewModel)
                3 -> AutonomousAgentScreen(viewModel = viewModel)
                4 -> MarketingAgentsScreen(viewModel = viewModel)
                5 -> AppointmentsServicesScreen(viewModel = viewModel)
                6 -> DeployGuideScreen(viewModel = viewModel)
            }
        }

        if (showLoginDialog) {
            LoginAuthDialog(
                viewModel = viewModel,
                onDismiss = { showLoginDialog = false }
            )
        }
    }
}
