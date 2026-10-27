package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.components.NotifikasiDialog
import com.example.ui.dashboard.AdminDashboardScreen
import com.example.ui.dashboard.WargaHomeScreen
import com.example.ui.keuangan.KeuanganScreen
import com.example.ui.pengaduan.PengaduanScreen
import com.example.ui.rwsettings.RwSettingsScreen
import com.example.ui.surat.SuratScreen
import com.example.ui.theme.CivicBlueContainer
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.warga.WargaListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: MainViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.rwSettings.collectAsState()

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        val user = currentUser!!
        val isAdmin = user.role == "SUPER_ADMIN"
        val isKetuaRt = user.role == "KETUA_RT"
        val isOfficial = isAdmin || isKetuaRt

        // Navigation state: 0: Dashboard/Home, 1: Warga, 2: Surat, 3: Keuangan, 4: Pengaduan, 5: Settings (Admin)
        var currentNavIndex by remember { mutableIntStateOf(0) }
        var showNotifModal by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_rw17),
                                    contentDescription = "Logo RW 17",
                                    modifier = Modifier.clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RW 17 KAVLING",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = when {
                                        isAdmin -> "Super Admin • ${settings?.namaKetuaRw ?: "Ketua RW"}"
                                        isKetuaRt -> "Ketua RT ${user.rt} • ${user.nama}"
                                        else -> "${user.nama} • RT ${user.rt}"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFF93C5FD)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CivicBluePrimary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        // Official Role Badge (Non-clickable, static status)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isAdmin -> Color(0xFFFEF3C7)
                                isKetuaRt -> Color(0xFFE0E7FF)
                                else -> Color(0xFFDCFCE7)
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOfficial) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = when {
                                        isAdmin -> Color(0xFF92400E)
                                        isKetuaRt -> Color(0xFF3730A3)
                                        else -> Color(0xFF166534)
                                    },
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when {
                                        isAdmin -> "Super Admin"
                                        isKetuaRt -> "Ketua RT ${user.rt}"
                                        else -> "Warga"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isAdmin -> Color(0xFF92400E)
                                        isKetuaRt -> Color(0xFF3730A3)
                                        else -> Color(0xFF166534)
                                    }
                                )
                            }
                        }

                        // Notification Bell
                        IconButton(
                            onClick = { showNotifModal = true },
                            modifier = Modifier.testTag("notification_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = Color(0xFFEA580C)) {
                                        Text("3", fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = Color.White
                                )
                            }
                        }

                        // Logout Icon
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Keluar",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentNavIndex == 0,
                        onClick = { currentNavIndex = 0 },
                        icon = {
                            Icon(
                                imageVector = if (isOfficial) Icons.Default.Dashboard else Icons.Default.Home,
                                contentDescription = "Beranda"
                            )
                        },
                        label = { Text("Beranda", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CivicBluePrimary,
                            selectedTextColor = CivicBluePrimary,
                            indicatorColor = CivicBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentNavIndex == 1,
                        onClick = { currentNavIndex = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Warga"
                            )
                        },
                        label = { Text("Warga", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CivicBluePrimary,
                            selectedTextColor = CivicBluePrimary,
                            indicatorColor = CivicBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_warga")
                    )

                    NavigationBarItem(
                        selected = currentNavIndex == 2,
                        onClick = { currentNavIndex = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Surat"
                            )
                        },
                        label = { Text("Surat", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CivicBluePrimary,
                            selectedTextColor = CivicBluePrimary,
                            indicatorColor = CivicBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_surat")
                    )

                    NavigationBarItem(
                        selected = currentNavIndex == 3,
                        onClick = { currentNavIndex = 3 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "QRIS & Kas"
                            )
                        },
                        label = { Text("QRIS & Kas", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CivicBluePrimary,
                            selectedTextColor = CivicBluePrimary,
                            indicatorColor = CivicBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_keuangan")
                    )

                    NavigationBarItem(
                        selected = currentNavIndex == 4,
                        onClick = { currentNavIndex = 4 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = "Pengaduan"
                            )
                        },
                        label = { Text("Pengaduan", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CivicBluePrimary,
                            selectedTextColor = CivicBluePrimary,
                            indicatorColor = CivicBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_pengaduan")
                    )

                    if (isAdmin) {
                        NavigationBarItem(
                            selected = currentNavIndex == 5,
                            onClick = { currentNavIndex = 5 },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Pengaturan"
                                )
                            },
                            label = { Text("Profil RW", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CivicBluePrimary,
                                selectedTextColor = CivicBluePrimary,
                                indicatorColor = CivicBlueContainer
                            ),
                            modifier = Modifier.testTag("nav_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentNavIndex) {
                    0 -> {
                        if (isOfficial) {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToWarga = { currentNavIndex = 1 },
                                onNavigateToSurat = { currentNavIndex = 2 },
                                onNavigateToKeuangan = { currentNavIndex = 3 },
                                onNavigateToPengaduan = { currentNavIndex = 4 },
                                onNavigateToSettings = { if (isAdmin) currentNavIndex = 5 else currentNavIndex = 0 }
                            )
                        } else {
                            WargaHomeScreen(
                                viewModel = viewModel,
                                onNavigateToSurat = { currentNavIndex = 2 },
                                onNavigateToKeuangan = { currentNavIndex = 3 },
                                onNavigateToPengaduan = { currentNavIndex = 4 }
                            )
                        }
                    }
                    1 -> WargaListScreen(viewModel = viewModel)
                    2 -> SuratScreen(viewModel = viewModel)
                    3 -> KeuanganScreen(viewModel = viewModel)
                    4 -> PengaduanScreen(viewModel = viewModel)
                    5 -> {
                        if (isAdmin) {
                            RwSettingsScreen(viewModel = viewModel)
                        } else {
                            WargaHomeScreen(
                                viewModel = viewModel,
                                onNavigateToSurat = { currentNavIndex = 2 },
                                onNavigateToKeuangan = { currentNavIndex = 3 },
                                onNavigateToPengaduan = { currentNavIndex = 4 }
                            )
                        }
                    }
                }
            }
        }

        if (showNotifModal) {
            NotifikasiDialog(
                viewModel = viewModel,
                onDismiss = { showNotifModal = false }
            )
        }
    }
}
