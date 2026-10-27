package com.example.ui.rwsettings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.PengumumanEntity
import com.example.data.model.RwSettingsEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.StatusDanger

@Composable
fun RwSettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val settings by viewModel.rwSettings.collectAsState()
    val announcements by viewModel.allPengumumanList.collectAsState()

    var namaRw by remember(settings) { mutableStateOf(settings?.namaRw ?: "Kavling RW 17") }
    var namaKetuaRw by remember(settings) { mutableStateOf(settings?.namaKetuaRw ?: "Bpk. H. Bambang Sutrisno, S.T.") }
    var noHpKetuaRw by remember(settings) { mutableStateOf(settings?.noHpKetuaRw ?: "081289871717") }
    var alamatSekretariat by remember(settings) { mutableStateOf(settings?.alamatSekretariat ?: "Balai Warga Kavling RW 17, Jl. Melati Kav. A1") }

    // QRIS & Rekening
    var qrisMerchantName by remember(settings) { mutableStateOf(settings?.qrisMerchantName ?: "QRIS RW 17 KAVLING INDAH") }
    var rekeningBank by remember(settings) { mutableStateOf(settings?.rekeningBank ?: "Bank Central Asia (BCA)") }
    var noRekening by remember(settings) { mutableStateOf(settings?.noRekening ?: "8720192831") }
    var namaPemilikRekening by remember(settings) { mutableStateOf(settings?.namaPemilikRekening ?: "KAS KAVLING RW 17") }
    var defaultIuranNominalText by remember(settings) { mutableStateOf((settings?.defaultIuranNominal ?: 100000L).toString()) }

    // Dashboard Customization
    var bannerNotice by remember(settings) { mutableStateOf(settings?.bannerNotice ?: "") }
    var runningTextAnnouncement by remember(settings) { mutableStateOf(settings?.runningTextAnnouncement ?: "") }

    // Surat Template & Jenis Surat Customization (Super Admin)
    var kopSuratInstansi by remember(settings) { mutableStateOf(settings?.kopSuratInstansi ?: "PENGURUS RUKUN WARGA 17") }
    var kopSuratKelurahan by remember(settings) { mutableStateOf(settings?.kopSuratKelurahan ?: "KELURAHAN KAVLING ASRI - KECAMATAN SEJAHTERA") }
    var kopSuratKontak by remember(settings) { mutableStateOf(settings?.kopSuratKontak ?: "Sekretariat: Jl. Melati Kav. A1 RT 01/RW 17 • Telp: 081289871717") }
    var pejabatPenandatangan by remember(settings) { mutableStateOf(settings?.pejabatPenandatangan ?: settings?.namaKetuaRw ?: "Bpk. H. Bambang Sutrisno, S.T.") }
    var jabatanPenandatangan by remember(settings) { mutableStateOf(settings?.jabatanPenandatangan ?: "Ketua RW 17") }
    var catatanLegalitasFooter by remember(settings) { mutableStateOf(settings?.catatanLegalitasFooter ?: "Surat keterangan ini sah dan terotorisasi secara elektronik melalui Sistem Informasi Digital Terintegrasi RW 17.") }

    val initialJenisList = remember(settings) {
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
    var jenisSuratList by remember(settings) { mutableStateOf(initialJenisList) }
    var newJenisSuratInput by remember { mutableStateOf("") }

    var showAddAnnouncementDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Top Header
        Surface(
            color = CivicBluePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pengaturan Super Admin RW",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Konfigurasi identitas RW, QRIS pembayaran iuran, & tampilan dashboard",
                    fontSize = 12.sp,
                    color = Color(0xFF93C5FD)
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profil Pengurus RW
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CivicBluePrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CivicBluePrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Profil Pengurus RW & Ketua RW",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Avatar & Foto Ketua RW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E3A8A),
                            modifier = Modifier.size(60.dp),
                            shadowElevation = 3.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = namaKetuaRw.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(text = "Foto / Avatar Resmi Ketua RW", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(text = "Ditampilkan pada kop surat & dashboard warga", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = namaKetuaRw,
                        onValueChange = { namaKetuaRw = it },
                        label = { Text("Nama Lengkap Ketua RW") },
                        placeholder = { Text("Contoh: Bpk. H. Bambang Sutrisno, S.T.") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rw_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noHpKetuaRw,
                        onValueChange = { noHpKetuaRw = it },
                        label = { Text("Nomor HP / WhatsApp Resmi RW") },
                        placeholder = { Text("0812xxxxxxxx") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rw_phone_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = alamatSekretariat,
                        onValueChange = { alamatSekretariat = it },
                        label = { Text("Alamat Lengkap Sekretariat / Balai RW") },
                        placeholder = { Text("Jl. Kavling Asri Utama Blok A1") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rw_address_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // 2. Pengaturan QRIS Pembayaran Iuran
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626).copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Konfigurasi QRIS & Rekening Iuran",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = qrisMerchantName,
                        onValueChange = { qrisMerchantName = it },
                        label = { Text("Nama Merchant QRIS") },
                        placeholder = { Text("QRIS RW 17 KAVLING INDAH") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("qris_merchant_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rekeningBank,
                        onValueChange = { rekeningBank = it },
                        label = { Text("Nama Bank") },
                        placeholder = { Text("Bank Central Asia (BCA)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = noRekening,
                            onValueChange = { noRekening = it },
                            label = { Text("Nomor Rekening") },
                            placeholder = { Text("8720192831") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("qris_rek_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = defaultIuranNominalText,
                            onValueChange = { defaultIuranNominalText = it.filter { c -> c.isDigit() } },
                            label = { Text("Iuran (Rp)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = namaPemilikRekening,
                        onValueChange = { namaPemilikRekening = it },
                        label = { Text("Nama Pemilik Rekening") },
                        placeholder = { Text("KAS RW 17") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // 3. Kustomisasi Tampilan Dashboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CivicEmeraldSecondary.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = CivicEmeraldSecondary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Kustomisasi Tampilan Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = bannerNotice,
                        onValueChange = { bannerNotice = it },
                        label = { Text("Teks Banner Sambutan Dashboard") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("banner_text_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = runningTextAnnouncement,
                        onValueChange = { runningTextAnnouncement = it },
                        label = { Text("Running Text / Teks Himbauan Berjalan") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("running_text_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // 4. Pengaturan Template & Format Surat Online (Super Admin)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CivicBluePrimary.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CivicBluePrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Format Surat & Jenis Surat Online",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Konfigurasi kop resmi, logo pengurus & daftar jenis surat",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Kop Surat Resmi RW 17", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = CivicBluePrimary)
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = kopSuratInstansi,
                        onValueChange = { kopSuratInstansi = it },
                        label = { Text("Baris 1 Kop: Nama Lembaga / RW") },
                        placeholder = { Text("PENGURUS RUKUN WARGA 17") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = kopSuratKelurahan,
                        onValueChange = { kopSuratKelurahan = it },
                        label = { Text("Baris 2 Kop: Kelurahan & Kecamatan") },
                        placeholder = { Text("KELURAHAN KAVLING ASRI - KECAMATAN SEJAHTERA") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = kopSuratKontak,
                        onValueChange = { kopSuratKontak = it },
                        label = { Text("Baris 3 Kop: Alamat & Kontak Sekretariat") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Pejabat Penandatangan & Legalitas", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = CivicBluePrimary)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pejabatPenandatangan,
                            onValueChange = { pejabatPenandatangan = it },
                            label = { Text("Nama Penandatangan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = jabatanPenandatangan,
                            onValueChange = { jabatanPenandatangan = it },
                            label = { Text("Jabatan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = catatanLegalitasFooter,
                        onValueChange = { catatanLegalitasFooter = it },
                        label = { Text("Catatan Legalitas / Footer Surat") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Daftar Jenis Surat Online Warga", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = CivicBluePrimary)
                    Text("Jenis surat yang dapat diajukan oleh seluruh warga:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Chip list of letter types
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        jenisSuratList.forEachIndexed { index, j ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "${index + 1}. $j", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    IconButton(
                                        onClick = {
                                            if (jenisSuratList.size > 1) {
                                                jenisSuratList = jenisSuratList.toMutableList().also { it.removeAt(index) }
                                            } else {
                                                Toast.makeText(context, "Minimal harus ada 1 jenis surat", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = StatusDanger, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input to add a new letter type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newJenisSuratInput,
                            onValueChange = { newJenisSuratInput = it },
                            placeholder = { Text("Contoh: Surat Izin Keramaian") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = {
                                if (newJenisSuratInput.isNotBlank()) {
                                    val trimmed = newJenisSuratInput.trim()
                                    if (!jenisSuratList.contains(trimmed)) {
                                        jenisSuratList = jenisSuratList + trimmed
                                        newJenisSuratInput = ""
                                        Toast.makeText(context, "Jenis surat ditambahkan", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Jenis surat sudah terdaftar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivicEmeraldSecondary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Save Settings Button
            Button(
                onClick = {
                    val updated = (settings ?: RwSettingsEntity()).copy(
                        namaRw = namaRw,
                        namaKetuaRw = namaKetuaRw,
                        noHpKetuaRw = noHpKetuaRw,
                        alamatSekretariat = alamatSekretariat,
                        qrisMerchantName = qrisMerchantName,
                        rekeningBank = rekeningBank,
                        noRekening = noRekening,
                        namaPemilikRekening = namaPemilikRekening,
                        defaultIuranNominal = defaultIuranNominalText.toLongOrNull() ?: 100000L,
                        bannerNotice = bannerNotice,
                        runningTextAnnouncement = runningTextAnnouncement,
                        kopSuratInstansi = kopSuratInstansi,
                        kopSuratKelurahan = kopSuratKelurahan,
                        kopSuratKontak = kopSuratKontak,
                        pejabatPenandatangan = pejabatPenandatangan,
                        jabatanPenandatangan = jabatanPenandatangan,
                        catatanLegalitasFooter = catatanLegalitasFooter,
                        daftarJenisSurat = jenisSuratList.joinToString(",")
                    )
                    viewModel.updateRwSettings(updated)
                    Toast.makeText(context, "Pengaturan identitas RW, QRIS & template surat berhasil disimpan!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_rw_settings_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Semua Perubahan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            // 4. Kelola Pengumuman Lingkungan RW
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = CivicBluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kelola Pengumuman RW 17",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = { showAddAnnouncementDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    announcements.forEach { ann ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = ann.judul, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                    Text(text = "${ann.kategori} • ${ann.tanggal}", fontSize = 10.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.deletePengumuman(ann) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = StatusDanger, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddAnnouncementDialog) {
        AddPengumumanDialog(
            onDismiss = { showAddAnnouncementDialog = false },
            onSubmit = { judul, kategori, isi, isPinned ->
                viewModel.addPengumuman(judul, kategori, isi, isPinned)
                showAddAnnouncementDialog = false
                Toast.makeText(context, "Pengumuman berhasil disiarkan!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AddPengumumanDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Boolean) -> Unit
) {
    var judul by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("Kerja Bakti") }
    var isi by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Siarkan Pengumuman RW Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul Pengumuman") },
                    placeholder = { Text("Mis: Jadwal Ronda Malam RT 01-06") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kategori,
                    onValueChange = { kategori = it },
                    label = { Text("Kategori") },
                    placeholder = { Text("Mis: Kerja Bakti / Posyandu / Ronda") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = isi,
                    onValueChange = { isi = it },
                    label = { Text("Isi Pengumuman Lengkap") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Text("Sematkan (Pin) di Halaman Depan", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (judul.isNotBlank() && isi.isNotBlank()) {
                        onSubmit(judul, kategori, isi, isPinned)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
            ) {
                Text("Siarkan Pengumuman")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
