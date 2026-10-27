package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.IuranTagihanEntity
import com.example.data.model.KasTransaksiEntity
import com.example.data.model.NotifikasiEntity
import com.example.data.model.PengaduanEntity
import com.example.data.model.PengumumanEntity
import com.example.data.model.RwSettingsEntity
import com.example.data.model.SuratEntity
import com.example.data.model.UserEntity
import com.example.data.model.WargaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Repository private constructor(private val database: AppDatabase) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            ensureDefaultAccounts()
            val admin = database.userDao().getByEmail("lingkungankavlingrw17@gmail.com")
            if (admin != null && _currentUser.value == null) {
                _currentUser.value = admin
            }
        }
    }

    private suspend fun ensureDefaultAccounts() {
        val admin = database.userDao().getByEmail("lingkungankavlingrw17@gmail.com")
        if (admin == null) {
            database.userDao().insert(
                UserEntity(
                    email = "lingkungankavlingrw17@gmail.com",
                    password = "admin123",
                    role = "SUPER_ADMIN",
                    nama = "Bpk. H. Bambang Sutrisno, S.T.",
                    nik = "3276010508750001",
                    noKk = "3276012201080001",
                    noHp = "081289871717",
                    rt = "01",
                    noRumah = "Kav. A-01",
                    tempatTanggalLahir = "Bandung, 5 Agustus 1975",
                    alamatLengkap = "Jl. Kavling Asri Utama Blok A1 No. 01, RT 01 / RW 17"
                )
            )
        }

        val rtAdmins = listOf(
            Triple("01", "Bpk. Ahmad Fauzi (Ketua RT 01)", "081211110001"),
            Triple("02", "Bpk. Hendra Gunawan (Ketua RT 02)", "081211110002"),
            Triple("03", "Bpk. Joko Susilo (Ketua RT 03)", "081211110003"),
            Triple("04", "Bpk. Mulyadi Pratama (Ketua RT 04)", "081211110004"),
            Triple("05", "Bpk. Agus Salim (Ketua RT 05)", "081211110005"),
            Triple("06", "Bpk. Rudi Hartono (Ketua RT 06)", "081211110006")
        )

        for ((rt, nama, hp) in rtAdmins) {
            val email = "ketua.rt$rt@rw17.id"
            if (database.userDao().getByEmail(email) == null) {
                database.userDao().insert(
                    UserEntity(
                        email = email,
                        password = "password123",
                        role = "KETUA_RT",
                        nama = nama,
                        nik = "32760101019000$rt",
                        noKk = "32760101010800$rt",
                        noHp = hp,
                        rt = rt,
                        noRumah = "Rumah Ketua RT $rt",
                        tempatTanggalLahir = "Jakarta, 10 Januari 1980",
                        alamatLengkap = "Lingkungan RT $rt / RW 17 Kavling Asri"
                    )
                )
            }
        }
    }

    suspend fun login(email: String, pass: String): Result<UserEntity> {
        val user = database.userDao().getByEmail(email.trim())
        return if (user != null && user.password == pass.trim()) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Email atau kata sandi tidak sesuai"))
        }
    }

    suspend fun switchAccount(role: String) {
        val user = if (role == "SUPER_ADMIN") {
            database.userDao().getByEmail("lingkungankavlingrw17@gmail.com")
        } else {
            database.userDao().getByEmail("budi.santoso@warga.id")
        }
        if (user != null) {
            _currentUser.value = user
        }
    }

    suspend fun registerWarga(
        email: String,
        pass: String,
        nama: String,
        nik: String,
        noKk: String,
        noHp: String,
        rt: String,
        noRumah: String,
        tempatTanggalLahir: String = "",
        alamatLengkap: String = ""
    ): Result<UserEntity> {
        val existing = database.userDao().getByEmail(email.trim())
        if (existing != null) {
            return Result.failure(Exception("Email sudah terdaftar. Silakan login."))
        }

        val newUser = UserEntity(
            email = email.trim(),
            password = pass.trim(),
            role = "WARGA",
            nama = nama.trim(),
            nik = nik.trim(),
            noKk = noKk.trim(),
            noHp = noHp.trim(),
            rt = rt.trim(),
            noRumah = noRumah.trim(),
            tempatTanggalLahir = tempatTanggalLahir.trim(),
            alamatLengkap = alamatLengkap.trim()
        )
        val id = database.userDao().insert(newUser)
        val created = newUser.copy(id = id)

        // Also add to Warga table
        val warga = WargaEntity(
            nik = nik.trim(),
            noKk = noKk.trim(),
            nama = nama.trim(),
            jenisKelamin = "Laki-laki",
            tempatTanggalLahir = if (tempatTanggalLahir.isNotBlank()) tempatTanggalLahir.trim() else "Jakarta, 1 Januari 1990",
            agama = "Islam",
            pekerjaan = "Wiraswasta",
            statusHubungan = "Kepala Keluarga",
            statusTinggal = "Tetap",
            noHp = noHp.trim(),
            rt = rt.trim(),
            blokNomor = noRumah.trim(),
            alamatLengkap = alamatLengkap.trim(),
            isKepalaKeluarga = true
        )
        database.wargaDao().insert(warga)

        // Create welcome notification
        val welcomeNotif = NotifikasiEntity(
            targetUserId = id,
            judul = "Selamat Datang di SIM RW 17",
            pesan = "Akun warga atas nama $nama berhasil diaktifkan. Anda kini dapat mengajukan surat online, membayar iuran via QRIS, dan melapor kendala lingkungan.",
            tipe = "PENGUMUMAN",
            tanggal = getFormattedCurrentDate(),
            isRead = false
        )
        database.notifikasiDao().insert(welcomeNotif)

        _currentUser.value = created
        return Result.success(created)
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Warga & Keluarga ---
    fun getAllWargaFlow(): Flow<List<WargaEntity>> = database.wargaDao().getAllFlow()
    fun searchWargaFlow(q: String): Flow<List<WargaEntity>> = database.wargaDao().searchWargaFlow(q)
    fun getWargaByRtFlow(rt: String): Flow<List<WargaEntity>> = database.wargaDao().getByRtFlow(rt)
    fun getKeluargaFlow(noKk: String): Flow<List<WargaEntity>> = database.wargaDao().getByKkFlow(noKk)
    fun countTotalWargaFlow(): Flow<Int> = database.wargaDao().countTotalFlow()
    fun countKkFlow(): Flow<Int> = database.wargaDao().countKkFlow()

    suspend fun addWarga(w: WargaEntity) = database.wargaDao().insert(w)
    suspend fun updateWarga(w: WargaEntity) = database.wargaDao().update(w)
    suspend fun updateWargaNama(id: Long, newNama: String) = database.wargaDao().updateNama(id, newNama)
    suspend fun deleteWarga(w: WargaEntity) = database.wargaDao().delete(w)

    // --- Surat Online ---
    fun getAllSuratFlow(): Flow<List<SuratEntity>> = database.suratDao().getAllFlow()
    fun getSuratByUserIdFlow(userId: Long): Flow<List<SuratEntity>> = database.suratDao().getByUserIdFlow(userId)
    fun getPendingSuratCountFlow(): Flow<Int> = database.suratDao().getPendingCountFlow()

    suspend fun submitSurat(
        userId: Long,
        nama: String,
        nik: String,
        rt: String,
        jenisSurat: String,
        keperluan: String
    ): Long {
        val total = database.suratDao().countSync()
        val nomorSurat = String.format(Locale.getDefault(), "470/%03d/RW-17/X/2026", total + 1)
        val surat = SuratEntity(
            nomorSurat = nomorSurat,
            userId = userId,
            namaPemohon = nama,
            nik = nik,
            rt = rt,
            jenisSurat = jenisSurat,
            keperluan = keperluan,
            status = "MENUNGGU",
            tanggalPengajuan = getFormattedCurrentDate()
        )
        val id = database.suratDao().insert(surat)

        // Notify user
        database.notifikasiDao().insert(
            NotifikasiEntity(
                targetUserId = userId,
                judul = "Pengajuan Surat Berhasil Dikirim",
                pesan = "Pengajuan $jenisSurat ($nomorSurat) berhasil masuk ke sistem pengurus RW 17 untuk verifikasi.",
                tipe = "SURAT",
                tanggal = getFormattedCurrentDate()
            )
        )
        return id
    }

    suspend fun updateSuratStatus(id: Long, status: String, catatan: String?, userId: Long, namaSurat: String, noSurat: String) {
        val tglSelesai = if (status == "DISETUJUI" || status == "DITOLAK") getFormattedCurrentDate() else null
        database.suratDao().updateStatus(id, status, catatan, tglSelesai)

        // Push real-time notification to citizen
        val pesanStatus = if (status == "DISETUJUI") {
            "Selamat! Pengajuan $namaSurat ($noSurat) Anda telah DISETUJUI oleh Ketua RW. Dokumen pengantar resmi telah diterbitkan."
        } else if (status == "DITOLAK") {
            "Pengajuan $namaSurat Anda DITOLAK. Alasan: ${catatan ?: "Persyaratan belum lengkap"}."
        } else {
            "Pengajuan $namaSurat Anda sedang DIPROSES oleh Pengurus RW."
        }

        database.notifikasiDao().insert(
            NotifikasiEntity(
                targetUserId = userId,
                judul = "Status Surat: $status",
                pesan = pesanStatus,
                tipe = "SURAT",
                tanggal = getFormattedCurrentDate()
            )
        )
    }

    // --- Iuran & Keuangan ---
    fun getAllIuranFlow(): Flow<List<IuranTagihanEntity>> = database.iuranDao().getAllFlow()
    fun getIuranByNikFlow(nik: String): Flow<List<IuranTagihanEntity>> = database.iuranDao().getByNikFlow(nik)
    fun countLunasIuranFlow(): Flow<Int> = database.iuranDao().countLunasFlow()
    fun countNunggakIuranFlow(): Flow<Int> = database.iuranDao().countNunggakFlow()

    suspend fun payIuran(tagihanId: Long, metode: String, refNumber: String, namaWarga: String) {
        database.iuranDao().updatePayment(
            id = tagihanId,
            status = "LUNAS",
            tanggalBayar = getFormattedCurrentDate(),
            metode = metode,
            ref = refNumber
        )

        // Auto record to Kas Transaksi
        database.kasDao().insert(
            KasTransaksiEntity(
                tanggal = getFormattedCurrentDate(),
                tipe = "PEMASUKAN",
                kategori = "Iuran Warga",
                judul = "Penerimaan Iuran Warga - $namaWarga",
                nominal = 100000L,
                keterangan = "Pembayaran via $metode (Ref: $refNumber)",
                dicatatOleh = "Sistem QRIS RW 17"
            )
        )

        // Notification
        _currentUser.value?.id?.let { uid ->
            database.notifikasiDao().insert(
                NotifikasiEntity(
                    targetUserId = uid,
                    judul = "Pembayaran Iuran Berhasil",
                    pesan = "Iuran bulanan Anda telah LUNAS terverifikasi melalui $metode. Terima kasih atas partisipasi Anda membangun RW 17.",
                    tipe = "IURAN",
                    tanggal = getFormattedCurrentDate()
                )
            )
        }
    }

    // --- Kas & Keuangan ---
    fun getKasFlow(): Flow<List<KasTransaksiEntity>> = database.kasDao().getAllFlow()
    fun getTotalPemasukanFlow(): Flow<Long> = database.kasDao().getTotalPemasukanFlow()
    fun getTotalPengeluaranFlow(): Flow<Long> = database.kasDao().getTotalPengeluaranFlow()

    suspend fun addKasTransaksi(transaksi: KasTransaksiEntity) = database.kasDao().insert(transaksi)
    suspend fun deleteKasTransaksi(id: Long) = database.kasDao().deleteById(id)

    // --- Pengaduan & GPS ---
    fun getAllPengaduanFlow(): Flow<List<PengaduanEntity>> = database.pengaduanDao().getAllFlow()
    fun getPengaduanByUserIdFlow(userId: Long): Flow<List<PengaduanEntity>> = database.pengaduanDao().getByUserIdFlow(userId)
    fun countAktifPengaduanFlow(): Flow<Int> = database.pengaduanDao().countAktifFlow()

    suspend fun submitPengaduan(
        userId: Long,
        namaPelapor: String,
        noHp: String,
        kategori: String,
        judul: String,
        deskripsi: String,
        lokasiDeskripsi: String,
        latitude: Double,
        longitude: Double
    ): Long {
        val pengaduan = PengaduanEntity(
            userId = userId,
            namaPelapor = namaPelapor,
            noHp = noHp,
            kategori = kategori,
            judul = judul,
            deskripsi = deskripsi,
            lokasiDeskripsi = lokasiDeskripsi,
            latitude = latitude,
            longitude = longitude,
            status = "DIAJUKAN",
            tanggalLapor = getFormattedCurrentDate()
        )
        val id = database.pengaduanDao().insert(pengaduan)

        database.notifikasiDao().insert(
            NotifikasiEntity(
                targetUserId = userId,
                judul = "Laporan Pengaduan Diterima",
                pesan = "Laporan Anda mengenai '$judul' dengan tagging GPS telah diterima oleh Pengurus RW 17 dan segera ditinjau.",
                tipe = "PENGADUAN",
                tanggal = getFormattedCurrentDate()
            )
        )
        return id
    }

    suspend fun updatePengaduanTindakLanjut(id: Long, status: String, tanggapan: String?, userId: Long, judul: String) {
        val tglSelesai = if (status == "SELESAI") getFormattedCurrentDate() else null
        database.pengaduanDao().updateTindakLanjut(id, status, tanggapan, tglSelesai)

        database.notifikasiDao().insert(
            NotifikasiEntity(
                targetUserId = userId,
                judul = "Update Pengaduan: $status",
                pesan = "Laporan '$judul' Anda diperbarui ke status '$status'. Catatan: ${tanggapan ?: "Dalam penanganan petugas RW."}",
                tipe = "PENGADUAN",
                tanggal = getFormattedCurrentDate()
            )
        )
    }

    // --- Pengumuman ---
    fun getAllPengumumanFlow(): Flow<List<PengumumanEntity>> = database.pengumumanDao().getAllFlow()
    suspend fun addPengumuman(p: PengumumanEntity) {
        database.pengumumanDao().insert(p)
        // Broadcast push notification to all citizens
        database.notifikasiDao().insert(
            NotifikasiEntity(
                targetUserId = null,
                judul = "Pengumuman Baru RW 17",
                pesan = p.judul,
                tipe = "PENGUMUMAN",
                tanggal = getFormattedCurrentDate()
            )
        )
    }
    suspend fun deletePengumuman(p: PengumumanEntity) = database.pengumumanDao().delete(p)

    // --- RW Settings ---
    fun getSettingsFlow(): Flow<RwSettingsEntity?> = database.rwSettingsDao().getSettingsFlow()
    suspend fun updateSettings(settings: RwSettingsEntity) = database.rwSettingsDao().upsert(settings)

    // --- Notifikasi ---
    fun getNotificationsFlow(userId: Long): Flow<List<NotifikasiEntity>> = database.notifikasiDao().getNotificationsFlow(userId)
    fun getUnreadCountFlow(userId: Long): Flow<Int> = database.notifikasiDao().getUnreadCountFlow(userId)
    suspend fun markAllNotifRead(userId: Long) = database.notifikasiDao().markAllAsRead(userId)

    // --- Helper Formatters & WhatsApp Integration ---
    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(database: AppDatabase): Repository {
            return instance ?: synchronized(this) {
                instance ?: Repository(database).also { instance = it }
            }
        }

        fun getFormattedCurrentDate(): String {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            return sdf.format(Date())
        }

        fun formatRupiah(amount: Long): String {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.maximumFractionDigits = 0
            return format.format(amount)
        }

        fun openWhatsAppChat(context: Context, phone: String, message: String) {
            try {
                // Normalize indonesian phone: 08xx -> 628xx
                var cleanPhone = phone.replace(Regex("[^0-9]"), "")
                if (cleanPhone.startsWith("0")) {
                    cleanPhone = "62" + cleanPhone.substring(1)
                }
                val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback share text
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Kirim via WhatsApp"))
                } catch (ex: Exception) {
                    Toast.makeText(context, "Tidak dapat membuka WhatsApp", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
