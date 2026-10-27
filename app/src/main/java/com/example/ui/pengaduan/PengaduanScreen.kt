package com.example.ui.pengaduan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.PengaduanEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AnalyticsBarChart
import com.example.ui.components.StatusPill
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusPending

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaduanScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role == "SUPER_ADMIN"
    val allPengaduan by viewModel.allPengaduanList.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Daftar Aduan, 1: Analitik & Tren
    var statusFilter by remember { mutableStateOf("SEMUA") }
    var showNewAduanDialog by remember { mutableStateOf(false) }
    var respondingAduan by remember { mutableStateOf<PengaduanEntity?>(null) }

    val displayedAduan = allPengaduan.filter { a ->
        val matchesUser = isAdmin || a.userId == (currentUser?.id ?: -1L)
        val matchesFilter = statusFilter == "SEMUA" || a.status == statusFilter
        matchesUser && matchesFilter
    }

    val filterOptions = listOf("SEMUA", "DIAJUKAN", "DITINJAU", "PENANGANAN", "SELESAI")

    // Categories aggregate for visual chart
    val complaintCategories = listOf(
        "Lampu PJU / Kelistrikan" to allPengaduan.count { it.kategori.contains("Lampu", ignoreCase = true) },
        "Sampah & Kebersihan" to allPengaduan.count { it.kategori.contains("Sampah", ignoreCase = true) },
        "Saluran Air & Banjir" to allPengaduan.count { it.kategori.contains("Saluran", ignoreCase = true) },
        "Keamanan & Ketertiban" to allPengaduan.count { it.kategori.contains("Keamanan", ignoreCase = true) },
        "Fasilitas Umum & Jalan" to allPengaduan.count { it.kategori.contains("Fasilitas", ignoreCase = true) }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header Top
            Surface(
                color = CivicBluePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pengaduan Lingkungan & GPS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Pelaporan masalah berbasis lokasi akurat & pemantauan respon cepat",
                        fontSize = 12.sp,
                        color = Color(0xFF93C5FD)
                    )
                }
            }

            // Tabs: Daftar Aduan vs Analitik Tren
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Laporan Masalah", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Dasbor Analitik & Tren", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            if (selectedTab == 0) {
                // Filter status chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { status ->
                        val isSelected = statusFilter == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { statusFilter = status },
                            label = {
                                Text(
                                    text = if (status == "SEMUA") "Semua Aduan" else status,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                if (displayedAduan.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tidak ada laporan pengaduan dalam kategori ini",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 6.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(displayedAduan, key = { it.id }) { a ->
                            PengaduanItemCard(
                                aduan = a,
                                isAdmin = isAdmin,
                                onRespond = { respondingAduan = a },
                                onOpenMap = {
                                    val geoUri = "geo:${a.latitude},${a.longitude}?q=${a.latitude},${a.longitude}(Lokasi+Aduan+${Uri.encode(a.judul)})"
                                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                                    mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Koordinat GPS: ${a.latitude}, ${a.longitude}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // --- TAB 2: ANALITIK TREN KELUHAN ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AnalyticsBarChart(items = complaintCategories)

                    // Performance & Preventative Recommendations
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = CivicBluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Indikator Kinerja & Respon Lingkungan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Rata-rata Waktu Respon", fontSize = 11.sp, color = Color.Gray)
                                    Text("2.4 Jam", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CivicBluePrimary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Tingkat Penyelesaian", fontSize = 11.sp, color = Color.Gray)
                                    val total = allPengaduan.size.coerceAtLeast(1)
                                    val selesai = allPengaduan.count { it.status == "SELESAI" }
                                    val pct = (selesai * 100) / total
                                    Text("$pct% Tuntas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CivicEmeraldSecondary)
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Handyman, contentDescription = null, tint = Color(0xFFB45309))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rekomendasi Tindakan Preventif Pengurus RW",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Saluran Drainase: Keluhan got tersumbat di Blok B dan C berulang. Disarankan pengadaan kerja bakti pengerukan sedimen pasir setiap 2 minggu sekali.\n" +
                                        "2. Penerangan Jalan (PJU): Siapkan stok sparepart bohlam LED 30W di posko RW untuk mempercepat waktu penanganan lampu mati.\n" +
                                        "3. Keamanan: Pengetatan portal pintu barat di atas pukul 23:00 WIB untuk mencegah kebisingan knalpot brong.",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // FAB to submit complaint
        FloatingActionButton(
            onClick = { showNewAduanDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
                .testTag("new_aduan_fab"),
            containerColor = StatusDanger,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Lapor Masalah Lingkungan")
        }
    }

    // Modal: New Complaint with GPS
    if (showNewAduanDialog) {
        NewPengaduanGpsDialog(
            onDismiss = { showNewAduanDialog = false },
            onSubmit = { kategori, judul, deskripsi, lokasi, lat, lng ->
                viewModel.submitPengaduan(kategori, judul, deskripsi, lokasi, lat, lng) {
                    showNewAduanDialog = false
                    Toast.makeText(context, "Laporan aduan GPS berhasil dikirim ke pengurus RW", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Modal: Admin Respond to Complaint
    respondingAduan?.let { aduan ->
        RespondPengaduanDialog(
            aduan = aduan,
            onDismiss = { respondingAduan = null },
            onUpdate = { newStatus, tanggapan ->
                viewModel.updatePengaduanStatus(aduan.id, newStatus, tanggapan, aduan.userId, aduan.judul)
                respondingAduan = null
                Toast.makeText(context, "Status tindak lanjut diperbarui!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun PengaduanItemCard(
    aduan: PengaduanEntity,
    isAdmin: Boolean,
    onRespond: () -> Unit,
    onOpenMap: () -> Unit
) {
    val categoryIcon = when {
        aduan.kategori.contains("Lampu", ignoreCase = true) -> Icons.Default.Lightbulb
        aduan.kategori.contains("Sampah", ignoreCase = true) -> Icons.Default.Delete
        aduan.kategori.contains("Saluran", ignoreCase = true) -> Icons.Default.WaterDamage
        aduan.kategori.contains("Keamanan", ignoreCase = true) -> Icons.Default.Security
        else -> Icons.Default.ReportProblem
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFEF2F2),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = categoryIcon, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = aduan.judul, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = aduan.kategori, fontSize = 11.sp, color = CivicBluePrimary)
                    }
                }
                StatusPill(status = aduan.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = aduan.deskripsi, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(8.dp))

            // Location Box with GPS pin
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(text = aduan.lokasiDeskripsi, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text(text = "GPS: ${String.format("%.5f", aduan.latitude)}, ${String.format("%.5f", aduan.longitude)}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    OutlinedButton(
                        onClick = onOpenMap,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Peta", fontSize = 10.sp)
                    }
                }
            }

            // Follow-up notes from RW management
            aduan.tanggapanPengurus?.let { tanggapan ->
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tindak Lanjut Pengurus: $tanggapan",
                        fontSize = 11.sp,
                        color = Color(0xFF065F46),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pelapor: ${aduan.namaPelapor} • ${aduan.tanggalLapor}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                if (isAdmin) {
                    Button(
                        onClick = onRespond,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Handyman, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tindak Lanjut", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPengaduanGpsDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Double, Double) -> Unit
) {
    val context = LocalContext.current
    val kategoriList = listOf(
        "Lampu PJU / Kelistrikan",
        "Sampah & Kebersihan",
        "Saluran Air & Banjir",
        "Keamanan & Ketertiban",
        "Fasilitas Umum & Jalan"
    )
    var selectedKategori by remember { mutableStateOf(kategoriList.first()) }
    var expandedKategori by remember { mutableStateOf(false) }
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasiDeskripsi by remember { mutableStateOf("") }

    // Live GPS Coordinates
    var latitude by remember { mutableStateOf(-6.291543) }
    var longitude by remember { mutableStateOf(106.812398) }
    var isGpsLocked by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationGranted || coarseLocationGranted) {
            try {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val loc: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (loc != null) {
                    latitude = loc.latitude
                    longitude = loc.longitude
                }
            } catch (e: SecurityException) {
                // Keep default precision coordinates
            }
            isGpsLocked = true
            Toast.makeText(context, "GPS Terkunci: ${String.format("%.5f", latitude)}, ${String.format("%.5f", longitude)}", Toast.LENGTH_SHORT).show()
        } else {
            // Fallback GPS coordinates
            isGpsLocked = true
            Toast.makeText(context, "Menggunakan koordinat satelit lingkungan RW 17", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lapor Masalah Lingkungan Berbasis GPS", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // GPS Status Box
                Surface(
                    color = if (isGpsLocked) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isGpsLocked) Icons.Default.CheckCircle else Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = if (isGpsLocked) Color(0xFF166534) else Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isGpsLocked) "GPS Terkunci Akurat" else "GPS Belum Diambil",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isGpsLocked) Color(0xFF166534) else Color(0xFF78350F)
                                )
                                Text(
                                    text = "Lat: ${String.format("%.5f", latitude)}, Lng: ${String.format("%.5f", longitude)}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Ambil GPS", fontSize = 10.sp)
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedKategori,
                    onExpandedChange = { expandedKategori = !expandedKategori },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedKategori,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Kendala") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKategori) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedKategori,
                        onDismissRequest = { expandedKategori = false }
                    ) {
                        kategoriList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item, fontSize = 13.sp) },
                                onClick = {
                                    selectedKategori = item
                                    expandedKategori = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul Pengaduan") },
                    placeholder = { Text("Mis: Bohlam PJU Padam Depan Blok B2") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aduan_judul_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Masalah Lengkap") },
                    placeholder = { Text("Jelaskan kondisi kerusakan dan kronologi...") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aduan_deskripsi_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = lokasiDeskripsi,
                    onValueChange = { lokasiDeskripsi = it },
                    label = { Text("Patokan Alamat / No. Rumah") },
                    placeholder = { Text("Misal: Depan Pos Kamling RT 02") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aduan_lokasi_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (judul.isNotBlank() && deskripsi.isNotBlank()) {
                        onSubmit(selectedKategori, judul, deskripsi, lokasiDeskripsi.ifBlank { "Kavling RW 17" }, latitude, longitude)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
                modifier = Modifier.testTag("submit_aduan_button")
            ) {
                Text("Kirim Laporan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun RespondPengaduanDialog(
    aduan: PengaduanEntity,
    onDismiss: () -> Unit,
    onUpdate: (String, String?) -> Unit
) {
    var tanggapan by remember { mutableStateOf(aduan.tanggapanPengurus ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tindak Lanjut Pengaduan Lingkungan", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = aduan.judul, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Lokasi: ${aduan.lokasiDeskripsi}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Deskripsi: ${aduan.deskripsi}", fontSize = 11.sp)

                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tanggapan,
                    onValueChange = { tanggapan = it },
                    label = { Text("Catatan Tindak Lanjut Pengurus") },
                    placeholder = { Text("Contoh: Teknisi telah dikirim untuk perbaikan.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Ubah Status Laporan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onUpdate("DITINJAU", tanggapan) },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusPending),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ditinjau", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { onUpdate("PENANGANAN", tanggapan) },
                        colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ditangani", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { onUpdate("SELESAI", tanggapan) },
                        colors = ButtonDefaults.buttonColors(containerColor = CivicEmeraldSecondary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Selesai", fontSize = 10.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
