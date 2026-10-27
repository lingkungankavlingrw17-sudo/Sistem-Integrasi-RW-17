package com.example.ui.keuangan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.Repository
import com.example.data.model.KasTransaksiEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary
import com.example.ui.theme.StatusDanger

@Composable
fun KeuanganScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.role == "SUPER_ADMIN"
    val isKetuaRt = currentUser?.role == "KETUA_RT"
    val canManageKas = isAdmin || isKetuaRt
    val settings by viewModel.rwSettings.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: QRIS KM RW 17, 1: Laporan Kas Transparan

    val kasTransactions by viewModel.kasTransactions.collectAsState()
    val totalPemasukan by viewModel.totalPemasukan.collectAsState()
    val totalPengeluaran by viewModel.totalPengeluaran.collectAsState()
    val saldoKas by viewModel.saldoKas.collectAsState()

    var showAddKasDialog by remember { mutableStateOf(false) }
    var showFullscreenQris by remember { mutableStateOf(false) }
    var transaksiToDelete by remember { mutableStateOf<KasTransaksiEntity?>(null) }

    val qrisTitle = "QRIS Kelompok Masyarakat RW 17"
    val merchantName = settings?.qrisMerchantName ?: "DANA KM RW 17"
    val nmidCode = "ID1026574923250"
    val bankName = settings?.rekeningBank ?: "Bank Central Asia (BCA)"
    val noRekening = settings?.noRekening ?: "8720192831"
    val pemilikRek = settings?.namaPemilikRekening ?: "KAS RW 17"
    val noHpPengurus = settings?.noHpKetuaRw ?: "081289871717"

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
                        text = "Keuangan & Kas Lingkungan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "QRIS Kelompok Masyarakat RW 17 & Laporan Kas Transparan",
                        fontSize = 12.sp,
                        color = Color(0xFF93C5FD)
                    )
                }
            }

            // Tab Selector: Replaces Tagihan Warga with QRIS Kelompok Masyarakat RW 17
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "QRIS KM RW 17",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_qris_km")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Laporan Kas Transparan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_kas_transparan")
                )
            }

            if (selectedTab == 0) {
                // --- TAB 1: QRIS KELOMPOK MASYARAKAT RW 17 ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Card
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
                                    Surface(
                                        color = Color(0xFFDC2626),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "QRIS",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "GPN",
                                            color = Color(0xFFDC2626),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Standar Nasional",
                                        color = Color(0xFF15803D),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = qrisTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Merchant: $merchantName • NMID: $nmidCode",
                                fontSize = 12.sp,
                                color = CivicBluePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mendukung pembayaran dari seluruh m-Banking (BCA, Mandiri, BRI, BNI, dll) dan e-Wallet (DANA, GoPay, OVO, ShopeePay, LinkAja) secara praktis dan bebas repot.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QRIS Flyer Image Display Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFullscreenQris = true }
                            .testTag("qris_image_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.qris_km_rw17),
                                    contentDescription = "QRIS Kelompok Masyarakat RW 17",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.72f), // standard flyer aspect ratio
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = null,
                                    tint = CivicBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ketuk gambar untuk memperbesar / tampilan layar penuh",
                                    fontSize = 11.sp,
                                    color = CivicBluePrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Data & Rekening Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Informasi Rekening & Kode Pembayaran",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // NMID Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("NMID QRIS Resmi", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = nmidCode,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("Dicetak oleh DANA", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(nmidCode))
                                        Toast.makeText(context, "NMID berhasil disalin!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("copy_nmid_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salin", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Rekening Bank Alternatif
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Transfer Alternatif: $bankName", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = noRekening,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CivicBluePrimary
                                    )
                                    Text("a.n. $pemilikRek", fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(noRekening))
                                        Toast.makeText(context, "No Rekening berhasil disalin!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("copy_rekening_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salin", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Panduan Pembayaran
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = CivicBluePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Petunjuk Pembayaran Warga",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivicBluePrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Buka aplikasi m-Banking (BCA/Mandiri/BRI dll) atau e-Wallet (DANA/GoPay/OVO/ShopeePay).\n" +
                                        "2. Pilih menu Bayar / Scan QRIS dan arahkan ke barcode QRIS KM RW 17 di atas.\n" +
                                        "3. Pastikan nama merchant tujuan tertera: *DANA KM RW 17*.\n" +
                                        "4. Masukkan nominal pembayaran dan selesaikan transaksi.\n" +
                                        "5. Kirim konfirmasi atau foto bukti bayar ke WhatsApp Pengurus RW melalui tombol di bawah.",
                                fontSize = 11.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons for WhatsApp Sharing & Confirmation
                    Button(
                        onClick = {
                            val shareMessage = "📢 *QRIS RESMI KELOMPOK MASYARAKAT RW 17 KAVLING* 📢\n\n" +
                                    "Yth. Bapak/Ibu Warga Kavling RW 17,\n" +
                                    "Berikut informasi pembayaran resmi iuran & kas lingkungan RW 17:\n\n" +
                                    "🏷 *Nama QRIS*: $qrisTitle\n" +
                                    "🏪 *Merchant*: $merchantName\n" +
                                    "🔢 *NMID*: $nmidCode\n" +
                                    "🏦 *Alternatif Transfer*: $bankName No. $noRekening a.n. $pemilikRek\n\n" +
                                    "Dapat di-scan melalui seluruh aplikasi m-Banking dan e-Wallet (BCA, Mandiri, BRI, DANA, GoPay, OVO, ShopeePay).\n\n" +
                                    "Semua dana yang masuk dikelola secara transparan dan tercatat di buku kas digital RW 17. Terima kasih atas partisipasi dan kepedulian seluruh warga."
                            Repository.openWhatsAppChat(context, "", shareMessage)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_qris_wa_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan Info QRIS ke WhatsApp Warga", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val userNik = currentUser?.nik ?: "-"
                            val userNama = currentUser?.nama ?: "Warga RW 17"
                            val userAlamat = "${currentUser?.noRumah ?: "Kavling"}, RT ${currentUser?.rt ?: "01"}"
                            val confirmMessage = "Halo Pengurus RW 17,\n" +
                                    "Saya ingin mengonfirmasi pembayaran iuran/kas warga melalui *QRIS Kelompok Masyarakat RW 17* ($merchantName - NMID $nmidCode).\n\n" +
                                    "• Nama Warga: $userNama\n" +
                                    "• NIK: $userNik\n" +
                                    "• Alamat: $userAlamat\n\n" +
                                    "Terlampir bukti transfer dari m-Banking/e-Wallet saya. Mohon dicatat dalam buku kas lingkungan. Terima kasih."
                            Repository.openWhatsAppChat(context, noHpPengurus, confirmMessage)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_qris_wa_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF16A34A))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kirim Bukti Pembayaran ke WA Pengurus", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            } else {
                // --- TAB 2: LAPORAN KAS TRANSPARAN ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // Financial Balance Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Posisi Saldo Kas RW 17",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = Repository.formatRupiah(saldoKas),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CivicEmeraldSecondary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = CivicEmeraldSecondary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Total Pemasukan", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Text(
                                            text = Repository.formatRupiah(totalPemasukan),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CivicEmeraldSecondary
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = StatusDanger, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Total Pengeluaran", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Text(
                                            text = Repository.formatRupiah(totalPengeluaran),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusDanger
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        val message = "📊 *REKAP LAPORAN KAS RW 17 (TRANSPARAN)* 📊\n" +
                                                "Saldo Kas Saat Ini: ${Repository.formatRupiah(saldoKas)}\n" +
                                                "Total Pemasukan: ${Repository.formatRupiah(totalPemasukan)}\n" +
                                                "Total Pengeluaran: ${Repository.formatRupiah(totalPengeluaran)}\n\n" +
                                                "Dikelola secara transparan dan akuntabel untuk kenyamanan seluruh warga Kavling RW 17. Terima kasih atas dukungan seluruh warga."
                                        Repository.openWhatsAppChat(context, "", message)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bagikan Rekap ke WhatsApp Warga", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Buku Kas & Riwayat Transaksi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(kasTransactions, key = { it.id }) { k ->
                        KasItemCard(
                            transaksi = k,
                            canDelete = canManageKas,
                            onDelete = { transaksiToDelete = k }
                        )
                    }
                }
            }
        }

        // FAB to add manual cash transaction (Super Admin only in Tab 1)
        if (isAdmin && selectedTab == 1) {
            FloatingActionButton(
                onClick = { showAddKasDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 90.dp)
                    .testTag("add_kas_fab"),
                containerColor = CivicEmeraldSecondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Catat Transaksi Kas")
            }
        }
    }

    // Modal: Fullscreen QRIS Dialog for High-Res Scanning
    if (showFullscreenQris) {
        Dialog(
            onDismissRequest = { showFullscreenQris = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f)),
                color = Color.Black.copy(alpha = 0.95f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = qrisTitle,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "NMID: $nmidCode",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = { showFullscreenQris = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qris_km_rw17),
                            contentDescription = "QRIS Layar Penuh",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showFullscreenQris = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tutup Tampilan Penuh", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal: Delete Confirmation Dialog
    transaksiToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { transaksiToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDanger) },
            title = { Text("Hapus Transaksi Kas?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin menghapus transaksi ini dari Buku Kas?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${target.judul} (${if (target.tipe == "PEMASUKAN") "+ " else "- "}${Repository.formatRupiah(target.nominal)})",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tanggal: ${target.tanggal} • Kategori: ${target.kategori}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteKasTransaksi(target.id)
                        transaksiToDelete = null
                        Toast.makeText(context, "Transaksi kas berhasil dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { transaksiToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal: Add Cash Transaction Dialog (Super Admin)
    if (showAddKasDialog) {
        AddKasDialog(
            onDismiss = { showAddKasDialog = false },
            onSubmit = { tipe, kategori, judul, nominal, keterangan ->
                viewModel.addKasManual(tipe, kategori, judul, nominal, keterangan)
                showAddKasDialog = false
                Toast.makeText(context, "Transaksi kas berhasil dicatat", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun KasItemCard(
    transaksi: KasTransaksiEntity,
    canDelete: Boolean = false,
    onDelete: () -> Unit = {}
) {
    val isPemasukan = transaksi.tipe == "PEMASUKAN"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isPemasukan) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPemasukan) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isPemasukan) CivicEmeraldSecondary else StatusDanger,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaksi.judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = "${transaksi.tanggal} • ${transaksi.kategori}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Dicatat: ${transaksi.dicatatOleh}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isPemasukan) "+ " else "- ") + Repository.formatRupiah(transaksi.nominal),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = if (isPemasukan) CivicEmeraldSecondary else StatusDanger
                )
                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("delete_kas_${transaksi.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Transaksi",
                            tint = StatusDanger,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddKasDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Long, String) -> Unit
) {
    var tipe by remember { mutableStateOf("PEMASUKAN") }
    var kategori by remember { mutableStateOf("Iuran Warga") }
    var judul by remember { mutableStateOf("") }
    var nominalText by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Transaksi Kas RW 17", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            tipe = "PEMASUKAN"
                            kategori = "Iuran Warga"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipe == "PEMASUKAN") CivicEmeraldSecondary else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pemasukan (+)", fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            tipe = "PENGELUARAN"
                            kategori = "Keamanan"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipe == "PENGELUARAN") StatusDanger else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pengeluaran (-)", fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul Transaksi") },
                    placeholder = { Text("Contoh: Pembelian Lampu PJU RT 02") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nominalText,
                    onValueChange = { nominalText = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    placeholder = { Text("Contoh: 150000") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan Tambahan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nominal = nominalText.toLongOrNull() ?: 0L
                    if (judul.isNotBlank() && nominal > 0) {
                        onSubmit(tipe, kategori, judul, nominal, keterangan)
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
