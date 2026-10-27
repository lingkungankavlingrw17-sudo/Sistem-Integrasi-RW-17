package com.example.ui.surat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RwSettingsEntity
import com.example.data.model.SuratEntity
import com.example.data.model.UserEntity
import com.example.data.model.WargaEntity
import com.example.ui.MainViewModel
import com.example.ui.components.StatusPill
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.StatusDanger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isSuperAdmin = currentUser?.role == "SUPER_ADMIN"
    val isKetuaRt = currentUser?.role == "KETUA_RT"
    val canAuthorize = isSuperAdmin || isKetuaRt
    val userRt = currentUser?.rt ?: ""

    val allSurat by viewModel.allSuratList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val settings by viewModel.rwSettings.collectAsState()

    var statusFilter by remember { mutableStateOf("SEMUA") }
    var rtFilter by remember { mutableStateOf(if (isKetuaRt) userRt else "SEMUA") }
    var verifyingSurat by remember { mutableStateOf<SuratEntity?>(null) }
    var previewSurat by remember { mutableStateOf<SuratEntity?>(null) }
    var showAjukanSuratDialog by remember { mutableStateOf(false) }

    // Filter displayed letters:
    // If Ketua RT, they can view either all or their RT, default to their RT
    // If Warga, view only their own letters
    // If Super Admin, view all
    val displayedSurat = allSurat.filter { s ->
        val matchesUser = if (canAuthorize) true else s.userId == (currentUser?.id ?: -1L)
        val matchesStatus = when (statusFilter) {
            "SEMUA" -> true
            "MENUNGGU" -> s.status == "MENUNGGU" || s.status == "DIPROSES"
            else -> s.status == statusFilter
        }
        val matchesRt = if (canAuthorize) {
            if (rtFilter == "SEMUA") true else s.rt == rtFilter
        } else true
        matchesUser && matchesStatus && matchesRt
    }

    val pendingCount = if (canAuthorize) {
        allSurat.count { (it.status == "MENUNGGU" || it.status == "DIPROSES") && (if (isKetuaRt) it.rt == userRt else true) }
    } else {
        displayedSurat.count { it.status == "MENUNGGU" || it.status == "DIPROSES" }
    }
    val approvedCount = if (canAuthorize) {
        allSurat.count { it.status == "DISETUJUI" && (if (isKetuaRt) it.rt == userRt else true) }
    } else {
        displayedSurat.count { it.status == "DISETUJUI" }
    }

    val filterOptions = listOf("SEMUA", "MENUNGGU", "DISETUJUI", "DITOLAK")

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (canAuthorize) "Daftar Otorisasi Surat Online" else "Permohonan Surat Online",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = when {
                                    isSuperAdmin -> "Otorisasi & persetujuan surat permohonan warga se-RW 17"
                                    isKetuaRt -> "Otorisasi & persetujuan surat permohonan warga RT $userRt"
                                    else -> "Ajukan surat pengantar resmi & pantau hasil otorisasi RT/RW 17"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF93C5FD)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (canAuthorize) Color(0xFFDCFCE7) else Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = if (canAuthorize) "Mode Otorisator" else "Warga Pemohon",
                                color = if (canAuthorize) Color(0xFF166534) else CivicBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Summary Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFFDE68A), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (canAuthorize) "$pendingCount Perlu Otorisasi" else "$pendingCount Menunggu Otorisasi",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (canAuthorize) "$approvedCount Terotorisasi" else "$approvedCount Telah Disetujui",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterOptions.forEach { status ->
                    val isSelected = statusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { statusFilter = status },
                        label = {
                            Text(
                                text = when (status) {
                                    "SEMUA" -> "Semua Status"
                                    "MENUNGGU" -> "Menunggu Otorisasi"
                                    "DISETUJUI" -> "Telah Diotorisasi"
                                    else -> status
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                // RT Filter chips for admins/super admin
                if (isSuperAdmin) {
                    val rts = listOf("SEMUA", "01", "02", "03", "04", "05", "06")
                    rts.forEach { r ->
                        val isSelected = rtFilter == r
                        FilterChip(
                            selected = isSelected,
                            onClick = { rtFilter = r },
                            label = {
                                Text(
                                    text = if (r == "SEMUA") "Semua RT" else "RT $r",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            if (displayedSurat.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tidak ada permohonan surat dalam daftar otorisasi ini",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
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
                    items(displayedSurat, key = { it.id }) { s ->
                        OtorisasiSuratCard(
                            surat = s,
                            canAuthorize = canAuthorize,
                            onAuthorize = { verifyingSurat = s },
                            onPreview = { previewSurat = s }
                        )
                    }
                }
            }
        }

        // FAB for Warga to submit a new letter request
        if (!canAuthorize) {
            FloatingActionButton(
                onClick = { showAjukanSuratDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 90.dp)
                    .testTag("ajukan_surat_fab"),
                containerColor = CivicBluePrimary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ajukan Surat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    // Modal: Form Pengajuan Surat Baru oleh Warga
    if (showAjukanSuratDialog) {
        val availableTypes = remember(settings) {
            settings?.daftarJenisSurat?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                ?: listOf(
                    "Surat Pengantar KTP / KK",
                    "Surat Keterangan Domisili",
                    "Surat Pengantar SKCK",
                    "Surat Keterangan Usaha",
                    "Surat Keterangan Belum Menikah",
                    "Surat Keterangan Kematian",
                    "Surat Pengantar Pindah Domisili",
                    "Surat Keterangan Tidak Mampu"
                )
        }
        val myFamilyMembers = remember(wargaList, currentUser) {
            wargaList.filter { it.noKk == (currentUser?.noKk ?: "") }
        }

        AjukanSuratDialog(
            currentUser = currentUser,
            familyMembers = myFamilyMembers,
            availableJenisSurat = availableTypes,
            onDismiss = { showAjukanSuratDialog = false },
            onSubmit = { nama, nik, rt, jenis, keperluan ->
                viewModel.submitSurat(
                    namaPemohon = nama,
                    nikPemohon = nik,
                    rtPemohon = rt,
                    jenisSurat = jenis,
                    keperluan = keperluan
                ) {
                    showAjukanSuratDialog = false
                    Toast.makeText(context, "Permohonan $jenis berhasil dikirim!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Modal: Authorization & Verification Dialog
    verifyingSurat?.let { s ->
        AuthorizeSuratDialog(
            surat = s,
            currentUserRole = currentUser?.role ?: "KETUA_RT",
            currentUserName = currentUser?.nama ?: "Pengurus",
            onDismiss = { verifyingSurat = null },
            onUpdateStatus = { newStatus, catatan ->
                viewModel.verifySurat(s.id, newStatus, catatan, s.userId, s.jenisSurat, s.nomorSurat)
                verifyingSurat = null
                Toast.makeText(context, "Surat berhasil diotorisasi: $newStatus", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Modal: Official Letter View & Print Preview
    previewSurat?.let { s ->
        OfficialSuratPreviewDialog(
            surat = s,
            settings = settings,
            onDismiss = { previewSurat = null }
        )
    }
}

@Composable
fun OtorisasiSuratCard(
    surat: SuratEntity,
    canAuthorize: Boolean,
    onAuthorize: () -> Unit,
    onPreview: () -> Unit
) {
    val isPending = surat.status == "MENUNGGU" || surat.status == "DIPROSES"
    val isApproved = surat.status == "DISETUJUI"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPending) Color(0xFFFFFBEB) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = surat.jenisSurat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "No. Registrasi: ${surat.nomorSurat}",
                        fontSize = 11.sp,
                        color = CivicBluePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusPill(status = surat.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pemohon info box
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pemohon: ${surat.namaPemohon}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            color = CivicBluePrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "RT ${surat.rt}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = CivicBluePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "NIK: ${surat.nik}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keperluan: \"${surat.keperluan}\"",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            surat.catatanPengurus?.let { catatan ->
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Catatan Otorisator: $catatan",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
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
                    text = "Diajukan: ${surat.tanggalPengajuan}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isApproved) {
                        Button(
                            onClick = onPreview,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivicEmeraldSecondary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lihat Dokumen Otorisasi", fontSize = 11.sp)
                        }
                    }

                    if (canAuthorize) {
                        Button(
                            onClick = onAuthorize,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPending) CivicBluePrimary else Color(0xFF64748B)
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("authorize_surat_button_${surat.id}")
                        ) {
                            Icon(
                                imageVector = if (isPending) Icons.Default.Check else Icons.Default.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPending) "Otorisasi Surat" else "Edit Otorisasi", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthorizeSuratDialog(
    surat: SuratEntity,
    currentUserRole: String,
    currentUserName: String,
    onDismiss: () -> Unit,
    onUpdateStatus: (String, String?) -> Unit
) {
    var catatan by remember {
        mutableStateOf(surat.catatanPengurus ?: "Telah diverifikasi dan diotorisasi oleh $currentUserName ($currentUserRole)")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Otorisasi Permohonan Surat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("No: ${surat.nomorSurat} • RT ${surat.rt}", fontSize = 12.sp, color = CivicBluePrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "Pemohon: ${surat.namaPemohon}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "NIK: ${surat.nik}", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "Jenis: ${surat.jenisSurat}", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "Keperluan: ${surat.keperluan}", fontSize = 11.sp, color = Color(0xFF334155))
                    }
                }

                OutlinedTextField(
                    value = catatan,
                    onValueChange = { catatan = it },
                    label = { Text("Catatan / Keterangan Otorisasi") },
                    placeholder = { Text("Contoh: Persyaratan lengkap dan sah.") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = "Pilih tindakan otorisasi di bawah ini:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onUpdateStatus("DISETUJUI", catatan) },
                        colors = ButtonDefaults.buttonColors(containerColor = CivicEmeraldSecondary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("approve_surat_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setujui", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onUpdateStatus("DITOLAK", catatan) },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reject_surat_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun OfficialSuratPreviewDialog(
    surat: SuratEntity,
    settings: RwSettingsEntity?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val kopInstansi = settings?.kopSuratInstansi ?: "PENGURUS RUKUN WARGA 17"
    val kopKelurahan = settings?.kopSuratKelurahan ?: "KELURAHAN KAVLING ASRI - KECAMATAN SEJAHTERA"
    val kopKontak = settings?.kopSuratKontak ?: settings?.alamatSekretariat ?: "Sekretariat: Jl. Melati Kav. A1 RT 01/RW 17"
    val pejabatNama = settings?.pejabatPenandatangan ?: settings?.namaKetuaRw ?: "Bpk. H. Bambang Sutrisno, S.T."
    val pejabatJabatan = settings?.jabatanPenandatangan ?: "Ketua RW 17"
    val legalitasFooter = settings?.catatanLegalitasFooter ?: "Surat keterangan ini sah dan terotorisasi secara elektronik melalui Sistem Informasi Digital Terintegrasi RW 17."

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dokumen Otorisasi Resmi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "TEROTORISASI",
                        color = Color(0xFF166534),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                // Kop Surat with Official Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_rw17),
                        contentDescription = "Logo RW 17",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = kopInstansi,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = kopKelurahan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = kopKontak,
                            fontSize = 8.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .height(2.dp)
                        .background(Color(0xFF0F172A))
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = surat.jenisSurat.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Nomor: ${surat.nomorSurat}",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Yang bertanda tangan di bawah ini Pengurus RT/RW 17, menerangkan dengan sebenarnya bahwa:",
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Nama Lengkap: ${surat.namaPemohon}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Nomor NIK: ${surat.nik}", fontSize = 10.sp)
                Text(text = "Wilayah: RT ${surat.rt} / RW 17", fontSize = 10.sp)

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Adalah benar-benar warga yang berdomisili di lingkungan kami dan surat pengantar ini telah diotorisasi untuk keperluan:\n\"${surat.keperluan}\"",
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )

                surat.catatanPengurus?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Catatan Otorisasi: $it", fontSize = 9.sp, color = Color(0xFF334155))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Signature & Digital QR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR Verifikasi Keaslian",
                                    tint = CivicBluePrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Text(text = "QR Keabsahan", fontSize = 8.sp, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Ditetapkan di: Kavling RW 17", fontSize = 9.sp)
                        Text(text = "Pada tanggal: ${surat.tanggalSelesai ?: surat.tanggalPengajuan}", fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(text = pejabatNama, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text(text = pejabatJabatan, fontSize = 9.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = legalitasFooter,
                    fontSize = 8.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Dokumen resmi siap dicetak / dibagikan!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cetak / Simpan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjukanSuratDialog(
    currentUser: UserEntity?,
    familyMembers: List<WargaEntity>,
    availableJenisSurat: List<String>,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    val applicantList = remember(familyMembers, currentUser) {
        if (familyMembers.isNotEmpty()) {
            familyMembers.map { Triple(it.nama, it.nik, it.rt) }
        } else {
            listOf(Triple(currentUser?.nama ?: "Warga", currentUser?.nik ?: "", currentUser?.rt ?: "01"))
        }
    }

    var selectedApplicantIndex by remember { mutableIntStateOf(0) }
    var expandedApplicantMenu by remember { mutableStateOf(false) }

    var selectedJenis by remember { mutableStateOf(availableJenisSurat.firstOrNull() ?: "Surat Pengantar KTP / KK") }
    var expandedJenisMenu by remember { mutableStateOf(false) }

    var keperluan by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Ajukan Permohonan Surat Online", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Kavling RW 17 • RT ${currentUser?.rt ?: "01"}", fontSize = 12.sp, color = CivicBluePrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pilih Anggota Pemohon dari KK
                Text("Nama Pemohon (Keluarga)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CivicBluePrimary)
                ExposedDropdownMenuBox(
                    expanded = expandedApplicantMenu,
                    onExpandedChange = { expandedApplicantMenu = !expandedApplicantMenu }
                ) {
                    val current = applicantList.getOrNull(selectedApplicantIndex)
                    OutlinedTextField(
                        value = "${current?.first ?: ""} (NIK: ${current?.second ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedApplicantMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedApplicantMenu,
                        onDismissRequest = { expandedApplicantMenu = false }
                    ) {
                        applicantList.forEachIndexed { idx, (nama, nik, _) ->
                            DropdownMenuItem(
                                text = { Text("$nama (NIK: $nik)") },
                                onClick = {
                                    selectedApplicantIndex = idx
                                    expandedApplicantMenu = false
                                }
                            )
                        }
                    }
                }

                // Pilih Jenis Surat
                Text("Jenis Surat Pengantar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CivicBluePrimary)
                ExposedDropdownMenuBox(
                    expanded = expandedJenisMenu,
                    onExpandedChange = { expandedJenisMenu = !expandedJenisMenu }
                ) {
                    OutlinedTextField(
                        value = selectedJenis,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJenisMenu) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedJenisMenu,
                        onDismissRequest = { expandedJenisMenu = false }
                    ) {
                        availableJenisSurat.forEach { jenis ->
                            DropdownMenuItem(
                                text = { Text(jenis) },
                                onClick = {
                                    selectedJenis = jenis
                                    expandedJenisMenu = false
                                }
                            )
                        }
                    }
                }

                // Input Keperluan
                OutlinedTextField(
                    value = keperluan,
                    onValueChange = { keperluan = it },
                    label = { Text("Keperluan / Maksud Pengajuan") },
                    placeholder = { Text("Contoh: Pembuatan e-KTP baru di Dukcapil") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ℹ️ Surat yang Anda ajukan akan otomatis masuk ke daftar otorisasi Ketua RT ${currentUser?.rt ?: ""} dan pengurus RW 17 untuk diverifikasi.",
                        fontSize = 10.sp,
                        color = CivicBluePrimary,
                        modifier = Modifier.padding(8.dp),
                        lineHeight = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (keperluan.isNotBlank()) {
                        val current = applicantList.getOrNull(selectedApplicantIndex)
                        val nama = current?.first ?: currentUser?.nama ?: ""
                        val nik = current?.second ?: currentUser?.nik ?: ""
                        val rt = current?.third ?: currentUser?.rt ?: "01"
                        onSubmit(nama, nik, rt, selectedJenis, keperluan)
                    }
                },
                enabled = keperluan.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
            ) {
                Text("Kirim Permohonan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
