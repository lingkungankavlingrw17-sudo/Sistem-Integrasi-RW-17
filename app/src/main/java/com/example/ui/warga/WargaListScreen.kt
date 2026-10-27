package com.example.ui.warga

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.WargaEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.StatusDanger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WargaListScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role == "SUPER_ADMIN"
    val isKetuaRt = currentUser?.role == "KETUA_RT"
    val isWarga = currentUser?.role == "WARGA"
    val canEdit = isAdmin || isKetuaRt || isWarga
    val canDelete = isAdmin // Warga cannot delete data in the application

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRt by viewModel.selectedRtFilter.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val totalWarga by viewModel.totalWargaCount.collectAsState()
    val totalKk by viewModel.totalKkCount.collectAsState()

    val displayedWargaList = if (isWarga) {
        val userKk = currentUser?.noKk ?: ""
        wargaList.filter { it.noKk == userKk }
    } else {
        wargaList
    }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingWarga by remember { mutableStateOf<WargaEntity?>(null) }
    var wargaToDelete by remember { mutableStateOf<WargaEntity?>(null) }
    var selectedKkForFamilyView by remember { mutableStateOf<String?>(null) }

    val rtList = listOf("SEMUA", "01", "02", "03", "04", "05", "06")

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
                        text = if (isWarga) "Data Anggota Keluarga (KK)" else "Data Warga & Keluarga",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isWarga) "No. KK: ${currentUser?.noKk ?: "-"} • RT ${currentUser?.rt ?: "-"}" else "Master data kependudukan Kavling RW 17",
                        fontSize = 12.sp,
                        color = Color(0xFF93C5FD)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats summary chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isWarga) "${displayedWargaList.size} Anggota KK Terdaftar" else "Total: $totalWarga Jiwa",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (!isWarga) {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$totalKk Kepala Keluarga",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(if (isWarga) "Cari anggota keluarga..." else "Cari nama warga, NIK, No. KK, atau blok...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_warga_input")
                )
            }

            // Filter RT horizontal pills (only for admins/ketua RT)
            if (!isWarga) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rtList.forEach { rt ->
                        val isSelected = selectedRt == rt
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedRtFilter(rt) },
                            label = {
                                Text(
                                    text = if (rt == "SEMUA") "Semua RT" else "RT $rt",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // Warga List
            if (displayedWargaList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isWarga) "Belum ada anggota keluarga terdaftar pada KK ini.\nSilakan klik tombol di bawah untuk menambahkan." else "Tidak ada data warga ditemukan",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedWargaList, key = { it.id }) { w ->
                        WargaItemCard(
                            warga = w,
                            canEdit = canEdit,
                            canDelete = canDelete,
                            onEdit = {
                                editingWarga = w
                                showAddEditDialog = true
                            },
                            onDelete = {
                                wargaToDelete = w
                            },
                            onViewFamily = {
                                selectedKkForFamilyView = w.noKk
                            }
                        )
                    }
                }
            }
        }

        // FAB for adding family member (Warga) or citizen (Super Admin / Ketua RT)
        if (canEdit) {
            FloatingActionButton(
                onClick = {
                    editingWarga = null
                    showAddEditDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 90.dp)
                    .testTag("add_warga_fab"),
                containerColor = CivicEmeraldSecondary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isWarga) "Tambah Anggota KK" else "Tambah Warga",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Add / Edit Citizen Modal Dialog
    if (showAddEditDialog) {
        AddEditWargaDialog(
            initialWarga = editingWarga,
            isWarga = isWarga,
            defaultKk = currentUser?.noKk ?: "",
            defaultRt = currentUser?.rt ?: "01",
            defaultAlamat = currentUser?.alamatLengkap ?: "",
            onDismiss = { showAddEditDialog = false },
            onSave = { savedWarga ->
                viewModel.saveWarga(savedWarga, isNew = (editingWarga == null))
                showAddEditDialog = false
                Toast.makeText(context, "Data anggota warga berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    wargaToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { wargaToDelete = null },
            title = { Text("Hapus Data Warga?") },
            text = { Text("Apakah Anda yakin ingin menghapus data '${target.nama}' (NIK: ${target.nik})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWarga(target)
                        wargaToDelete = null
                        Toast.makeText(context, "Data warga dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { wargaToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Family Member (Kartu Keluarga) Detail Modal
    selectedKkForFamilyView?.let { kkNumber ->
        val familyMembers = wargaList.filter { it.noKk == kkNumber }
        AlertDialog(
            onDismissRequest = { selectedKkForFamilyView = null },
            title = {
                Column {
                    Text("Anggota Kartu Keluarga (KK)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("No. KK: $kkNumber", fontSize = 12.sp, color = Color.Gray)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    familyMembers.forEach { member ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = member.nama, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(
                                        color = if (member.isKepalaKeluarga) CivicBluePrimary else Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = member.statusHubungan,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(text = "NIK: ${member.nik}", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "${member.jenisKelamin} • ${member.pekerjaan}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedKkForFamilyView = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun WargaItemCard(
    warga: WargaEntity,
    canEdit: Boolean,
    canDelete: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewFamily: () -> Unit
) {
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
                        color = if (warga.isKepalaKeluarga) CivicBluePrimary else CivicEmeraldSecondary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (warga.isKepalaKeluarga) Icons.Default.Home else Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = warga.nama,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "NIK: ${warga.nik}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (warga.statusTinggal == "Tetap") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = warga.statusTinggal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (warga.statusTinggal == "Tetap") Color(0xFF166534) else Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RT ${warga.rt} • ${warga.blokNomor}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CivicBluePrimary
                )
                Text(
                    text = warga.statusHubungan,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "TTL: ${warga.tempatTanggalLahir}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (warga.alamatLengkap.isNotBlank()) {
                Text(
                    text = "Alamat: ${warga.alamatLengkap}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Text(
                text = "Pekerjaan: ${warga.pekerjaan} • HP: ${warga.noHp}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewFamily,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lihat KK", fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (canEdit) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("edit_warga_${warga.id}")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Nama Warga",
                                tint = CivicBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (canDelete) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_warga_${warga.id}")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = StatusDanger,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWargaDialog(
    initialWarga: WargaEntity?,
    isWarga: Boolean = false,
    defaultKk: String = "",
    defaultRt: String = "",
    defaultAlamat: String = "",
    onDismiss: () -> Unit,
    onSave: (WargaEntity) -> Unit
) {
    var nama by remember { mutableStateOf(initialWarga?.nama ?: "") }
    var nik by remember { mutableStateOf(initialWarga?.nik ?: "") }
    var noKk by remember { mutableStateOf(initialWarga?.noKk ?: if (isWarga) defaultKk else "") }
    var rt by remember { mutableStateOf(initialWarga?.rt ?: if (isWarga) defaultRt else "01") }
    var blokNomor by remember { mutableStateOf(initialWarga?.blokNomor ?: "") }
    var tempatTanggalLahir by remember { mutableStateOf(initialWarga?.tempatTanggalLahir ?: "") }
    var alamatLengkap by remember { mutableStateOf(initialWarga?.alamatLengkap ?: if (isWarga) defaultAlamat else "") }
    var noHp by remember { mutableStateOf(initialWarga?.noHp ?: "") }
    var pekerjaan by remember { mutableStateOf(initialWarga?.pekerjaan ?: "Karyawan Swasta") }
    var statusHubungan by remember { mutableStateOf(initialWarga?.statusHubungan ?: if (isWarga) "Anggota Keluarga" else "Kepala Keluarga") }
    var statusTinggal by remember { mutableStateOf(initialWarga?.statusTinggal ?: "Tetap") }
    var jenisKelamin by remember { mutableStateOf(initialWarga?.jenisKelamin ?: "Laki-laki") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialWarga == null) {
                    if (isWarga) "Tambah Anggota Keluarga (KK)" else "Tambah Warga Baru"
                } else {
                    if (isWarga) "Edit Data Anggota Keluarga" else "Edit Data Warga"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Lengkap Warga") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nik,
                    onValueChange = { nik = it },
                    label = { Text("NIK (16 Digit)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = noKk,
                    onValueChange = { if (!isWarga) noKk = it },
                    label = { Text(if (isWarga) "Nomor KK (Sesuai Akun Keluarga)" else "Nomor KK (16 Digit)") },
                    readOnly = isWarga,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tempatTanggalLahir,
                    onValueChange = { tempatTanggalLahir = it },
                    label = { Text("Tempat, Tanggal Lahir") },
                    placeholder = { Text("Mis: Jakarta, 12-03-1985") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = alamatLengkap,
                    onValueChange = { alamatLengkap = it },
                    label = { Text("Alamat Lengkap Rumah") },
                    placeholder = { Text("Jl. Melati Blok B2 No. 14, RT 02 / RW 17") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rt,
                        onValueChange = { if (!isWarga) rt = it },
                        readOnly = isWarga,
                        label = { Text("RT") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = blokNomor,
                        onValueChange = { blokNomor = it },
                        label = { Text("Blok & No.") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = noHp,
                    onValueChange = { noHp = it },
                    label = { Text("Nomor HP / WhatsApp") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pekerjaan,
                    onValueChange = { pekerjaan = it },
                    label = { Text("Pekerjaan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = statusHubungan,
                        onValueChange = { statusHubungan = it },
                        label = { Text("Hubungan KK") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = statusTinggal,
                        onValueChange = { statusTinggal = it },
                        label = { Text("Status Tinggal") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isNotBlank() && nik.isNotBlank()) {
                        val saved = (initialWarga ?: WargaEntity(
                            nik = nik,
                            noKk = noKk,
                            nama = nama,
                            jenisKelamin = jenisKelamin,
                            tempatTanggalLahir = tempatTanggalLahir.ifBlank { "Jakarta, 01-01-1990" },
                            agama = "Islam",
                            pekerjaan = pekerjaan,
                            statusHubungan = statusHubungan,
                            statusTinggal = statusTinggal,
                            noHp = noHp,
                            rt = rt,
                            blokNomor = blokNomor,
                            alamatLengkap = alamatLengkap,
                            isKepalaKeluarga = (statusHubungan == "Kepala Keluarga")
                        )).copy(
                            nama = nama,
                            nik = nik,
                            noKk = noKk,
                            rt = rt,
                            blokNomor = blokNomor,
                            tempatTanggalLahir = tempatTanggalLahir,
                            alamatLengkap = alamatLengkap,
                            noHp = noHp,
                            pekerjaan = pekerjaan,
                            statusHubungan = statusHubungan,
                            statusTinggal = statusTinggal,
                            isKepalaKeluarga = (statusHubungan == "Kepala Keluarga")
                        )
                        onSave(saved)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
