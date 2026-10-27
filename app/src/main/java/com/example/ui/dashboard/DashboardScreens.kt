package com.example.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Repository
import com.example.ui.MainViewModel
import com.example.ui.components.AnalyticsBarChart
import com.example.ui.components.CivicStatCard
import com.example.ui.components.RunningNoticeBanner
import com.example.ui.components.StatusPill
import com.example.ui.theme.CivicAmberTertiary
import com.example.ui.theme.CivicBlueContainer
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusInfo

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onNavigateToWarga: () -> Unit,
    onNavigateToSurat: () -> Unit,
    onNavigateToKeuangan: () -> Unit,
    onNavigateToPengaduan: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val settings by viewModel.rwSettings.collectAsState()
    val totalWarga by viewModel.totalWargaCount.collectAsState()
    val totalKk by viewModel.totalKkCount.collectAsState()
    val pendingSurat by viewModel.pendingSuratCount.collectAsState()
    val iuranLunas by viewModel.iuranLunasCount.collectAsState()
    val iuranNunggak by viewModel.iuranNunggakCount.collectAsState()
    val saldoKas by viewModel.saldoKas.collectAsState()
    val pengaduanAktif by viewModel.pengaduanAktifCount.collectAsState()
    val aduanList by viewModel.allPengaduanList.collectAsState()
    val suratList by viewModel.allSuratList.collectAsState()

    // Aggregate category counts for visual chart
    val complaintCategories = listOf(
        "Lampu PJU / Listrik" to aduanList.count { it.kategori.contains("Lampu", ignoreCase = true) },
        "Sampah & Kebersihan" to aduanList.count { it.kategori.contains("Sampah", ignoreCase = true) },
        "Saluran Air / Got" to aduanList.count { it.kategori.contains("Saluran", ignoreCase = true) },
        "Keamanan & Ketertiban" to aduanList.count { it.kategori.contains("Keamanan", ignoreCase = true) },
        "Fasilitas Umum" to aduanList.count { it.kategori.contains("Fasilitas", ignoreCase = true) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Hero Admin Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(CivicBluePrimary, Color(0xFF1E293B))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_rw17),
                                contentDescription = "Logo Resmi RW 17",
                                modifier = Modifier.clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "SUPER ADMIN PENGURUS RW",
                                    color = Color(0xFF92400E),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "RW 17 KAVLING",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Ketua RW: ${settings?.namaKetuaRw ?: "Bpk. H. Bambang Sutrisno, S.T."}",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier
                            .size(46.dp)
                            .clickable { onNavigateToSettings() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan RW",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = settings?.bannerNotice ?: "",
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }

        // Running announcement notice
        settings?.runningTextAnnouncement?.let { text ->
            RunningNoticeBanner(text = text)
        }

        // Key Metrics Summary
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Ringkasan Eksekutif Lingkungan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2x3 Grid of Stat Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CivicStatCard(
                    title = "Total Warga",
                    value = "$totalWarga Jiwa",
                    subtitle = "$totalKk Kepala Keluarga",
                    icon = Icons.Default.Group,
                    containerColor = Color(0xFFEFF6FF),
                    contentColor = CivicBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWarga
                )
                CivicStatCard(
                    title = "Surat Pending",
                    value = "$pendingSurat Surat",
                    subtitle = "Perlu verifikasi",
                    icon = Icons.Default.Description,
                    containerColor = Color(0xFFFEF3C7),
                    contentColor = Color(0xFFB45309),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSurat
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CivicStatCard(
                    title = "Saldo Kas RW",
                    value = Repository.formatRupiah(saldoKas),
                    subtitle = "Transparan & real-time",
                    icon = Icons.Default.AccountBalance,
                    containerColor = Color(0xFFECFDF5),
                    contentColor = CivicEmeraldSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToKeuangan
                )
                CivicStatCard(
                    title = "QRIS KM RW 17",
                    value = "DANA KM RW 17",
                    subtitle = "NMID: ID1026574923250",
                    icon = Icons.Default.QrCodeScanner,
                    containerColor = Color(0xFFF0FDF4),
                    contentColor = Color(0xFF15803D),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToKeuangan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CivicStatCard(
                    title = "Aduan Aktif",
                    value = "$pengaduanAktif Masalah",
                    subtitle = "Dengan tagging GPS",
                    icon = Icons.Default.ReportProblem,
                    containerColor = Color(0xFFFEF2F2),
                    contentColor = StatusDanger,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPengaduan
                )
                CivicStatCard(
                    title = "QRIS Kas RW",
                    value = "Aktif",
                    subtitle = "Siap terima iuran",
                    icon = Icons.Default.QrCodeScanner,
                    containerColor = Color(0xFFFAF5FF),
                    contentColor = Color(0xFF7E22CE),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSettings
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Visual Analytics Bar Chart for Complaint Trends
            AnalyticsBarChart(items = complaintCategories)

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Access / Pending Actions
            Text(
                text = "Surat Menunggu Verifikasi Segera",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            val waitingSurat = suratList.filter { it.status == "MENUNGGU" }
            if (waitingSurat.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Semua surat pengajuan warga telah diverifikasi.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                waitingSurat.take(3).forEach { s ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.jenisSurat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Pemohon: ${s.namaPemohon} (RT ${s.rt})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Keperluan: ${s.keperluan}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                            Button(
                                onClick = onNavigateToSurat,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
                            ) {
                                Text("Tinjau", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WargaHomeScreen(
    viewModel: MainViewModel,
    onNavigateToSurat: () -> Unit,
    onNavigateToKeuangan: () -> Unit,
    onNavigateToPengaduan: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.rwSettings.collectAsState()
    val userSuratList by viewModel.allSuratList.collectAsState()
    val userAduanList by viewModel.allPengaduanList.collectAsState()
    val iuranList by viewModel.allIuranList.collectAsState()
    val announcements by viewModel.allPengumumanList.collectAsState()

    val mySurat = userSuratList.filter { it.userId == (currentUser?.id ?: -1L) }
    val myAduan = userAduanList.filter { it.userId == (currentUser?.id ?: -1L) }
    val myIuran = iuranList.firstOrNull { it.wargaNik == currentUser?.nik }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Warga Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(CivicBluePrimary, Color(0xFF1E3A8A).copy(alpha = 0.85f))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_rw17),
                            contentDescription = "Logo RW 17",
                            modifier = Modifier.clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Halo, ${currentUser?.nama ?: "Warga RW 17"}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "RT ${currentUser?.rt ?: "01"} • ${currentUser?.noRumah ?: "Kavling"}",
                            fontSize = 12.sp,
                            color = Color(0xFF93C5FD)
                        )
                        Text(
                            text = "NIK: ${currentUser?.nik ?: "-"}",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // Running Banner
        settings?.runningTextAnnouncement?.let { text ->
            RunningNoticeBanner(text = text)
        }

        // Official QRIS Kelompok Masyarakat RW 17 Card
        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDC2626)
                            ) {
                                Text(
                                    text = "QRIS",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "QRIS Kelompok Masyarakat RW 17",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "Resmi Aktif",
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onNavigateToKeuangan() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.qris_km_rw17),
                                contentDescription = "QRIS Kelompok Masyarakat RW 17",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DANA KM RW 17",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CivicBluePrimary
                            )
                            Text(
                                text = "NMID: ID1026574923250",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "BCA: 8720192831 a.n. Kas RW 17",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Scan via m-Banking BCA/Mandiri/BRI atau GoPay/DANA/OVO",
                                fontSize = 10.sp,
                                color = CivicEmeraldSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToKeuangan,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pay_dues_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buka QRIS Resmi", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val message = "Halo Pengurus RW 17, saya ${currentUser?.nama} (${currentUser?.noRumah}, RT ${currentUser?.rt}) ingin konfirmasi pembayaran via *QRIS Kelompok Masyarakat RW 17* (DANA KM RW 17 - NMID ID1026574923250). Terlampir bukti transfer. Terima kasih."
                                Repository.openWhatsAppChat(context, settings?.noHpKetuaRw ?: "081289871717", message)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_whatsapp_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF16A34A))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kirim Bukti WA", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Main Civic Action Shortcuts
            Text(
                text = "Layanan Lingkungan Warga",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CivicShortcutCard(
                    title = "Ajukan Surat",
                    subtitle = "SKCK, Domisili, dll",
                    icon = Icons.Default.Description,
                    iconColor = CivicBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSurat
                )
                CivicShortcutCard(
                    title = "Iuran & Kas",
                    subtitle = "Laporan Kas Transparan",
                    icon = Icons.Default.AccountBalance,
                    iconColor = CivicEmeraldSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToKeuangan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CivicShortcutCard(
                    title = "Lapor GPS",
                    subtitle = "Aduan Cepat Lingkungan",
                    icon = Icons.Default.LocationOn,
                    iconColor = StatusDanger,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPengaduan
                )
                CivicShortcutCard(
                    title = "Sekretariat RW",
                    subtitle = "Info Pengurus & Posko",
                    icon = Icons.Default.Home,
                    iconColor = CivicAmberTertiary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val message = "Halo Pengurus RW 17, saya ${currentUser?.nama} warga RT ${currentUser?.rt} membutuhkan informasi sekretariat RW."
                        Repository.openWhatsAppChat(context, settings?.noHpKetuaRw ?: "081289871717", message)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Active Requests / Tracking
            Text(
                text = "Tracking Surat & Aduan Saya",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (mySurat.isEmpty() && myAduan.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Belum ada pengajuan surat atau aduan aktif. Gunakan menu di atas untuk mengajukan layanan.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                mySurat.take(2).forEach { s ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = s.jenisSurat, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "No: ${s.nomorSurat}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusPill(status = s.status)
                        }
                    }
                }
                myAduan.take(2).forEach { a ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = a.judul, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Kategori: ${a.kategori}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusPill(status = a.status)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Community Announcements
            Text(
                text = "Pengumuman & Agenda RW 17",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            announcements.take(3).forEach { ann ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = CivicBlueContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ann.kategori,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicBluePrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(text = ann.tanggal, fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = ann.judul, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ann.isi,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CivicShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
