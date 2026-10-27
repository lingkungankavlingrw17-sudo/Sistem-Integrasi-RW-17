package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.IuranDao
import com.example.data.dao.KasDao
import com.example.data.dao.NotifikasiDao
import com.example.data.dao.PengaduanDao
import com.example.data.dao.PengumumanDao
import com.example.data.dao.RwSettingsDao
import com.example.data.dao.SuratDao
import com.example.data.dao.UserDao
import com.example.data.dao.WargaDao
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
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        WargaEntity::class,
        SuratEntity::class,
        IuranTagihanEntity::class,
        KasTransaksiEntity::class,
        PengaduanEntity::class,
        PengumumanEntity::class,
        RwSettingsEntity::class,
        NotifikasiEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun wargaDao(): WargaDao
    abstract fun suratDao(): SuratDao
    abstract fun iuranDao(): IuranDao
    abstract fun kasDao(): KasDao
    abstract fun pengaduanDao(): PengaduanDao
    abstract fun pengumumanDao(): PengumumanDao
    abstract fun rwSettingsDao(): RwSettingsDao
    abstract fun notifikasiDao(): NotifikasiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sim_rtrw_rw17.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            // 1. Initial Users (Super Admin and Demo Warga)
            val adminUser = UserEntity(
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
            val adminId = db.userDao().insert(adminUser)

            val wargaUser = UserEntity(
                email = "budi.santoso@warga.id",
                password = "warga123",
                role = "WARGA",
                nama = "Budi Santoso",
                nik = "3276011203850001",
                noKk = "3276012004120005",
                noHp = "081382901928",
                rt = "02",
                noRumah = "Blok B2 No. 14",
                tempatTanggalLahir = "Jakarta, 12 Maret 1985",
                alamatLengkap = "Jl. Melati Raya Blok B2 No. 14, RT 02 / RW 17, Kavling Asri"
            )
            val wargaId = db.userDao().insert(wargaUser)

            val wargaUser2 = UserEntity(
                email = "siti.rahma@warga.id",
                password = "warga123",
                role = "WARGA",
                nama = "Siti Rahmawati",
                nik = "3276014407900003",
                noKk = "3276011809150009",
                noHp = "081909876543",
                rt = "03",
                noRumah = "Blok C1 No. 05"
            )
            db.userDao().insert(wargaUser2)

            // 2. Initial Warga & Keluarga Master Data
            val initialWarga = listOf(
                WargaEntity(
                    nik = "3276010508750001",
                    noKk = "3276012201080001",
                    nama = "H. Bambang Sutrisno, S.T.",
                    jenisKelamin = "Laki-laki",
                    tempatTanggalLahir = "Bandung, 5 Agustus 1975",
                    agama = "Islam",
                    pekerjaan = "Wiraswasta / Ketua RW",
                    statusHubungan = "Kepala Keluarga",
                    statusTinggal = "Tetap",
                    noHp = "081289871717",
                    rt = "01",
                    blokNomor = "Kav. A-01",
                    isKepalaKeluarga = true
                ),
                WargaEntity(
                    nik = "3276014509780002",
                    noKk = "3276012201080001",
                    nama = "Hj. Endang Suryani",
                    jenisKelamin = "Perempuan",
                    tempatTanggalLahir = "Semarang, 12 September 1978",
                    agama = "Islam",
                    pekerjaan = "Ibu Rumah Tangga",
                    statusHubungan = "Istri",
                    statusTinggal = "Tetap",
                    noHp = "081289871718",
                    rt = "01",
                    blokNomor = "Kav. A-01",
                    isKepalaKeluarga = false
                ),
                WargaEntity(
                    nik = "3276011203850001",
                    noKk = "3276012004120005",
                    nama = "Budi Santoso",
                    jenisKelamin = "Laki-laki",
                    tempatTanggalLahir = "Jakarta, 12 Maret 1985",
                    agama = "Islam",
                    pekerjaan = "Karyawan Swasta",
                    statusHubungan = "Kepala Keluarga",
                    statusTinggal = "Tetap",
                    noHp = "081382901928",
                    rt = "02",
                    blokNomor = "Blok B2 No. 14",
                    isKepalaKeluarga = true
                ),
                WargaEntity(
                    nik = "3276015506890002",
                    noKk = "3276012004120005",
                    nama = "Ratna Yunita",
                    jenisKelamin = "Perempuan",
                    tempatTanggalLahir = "Surabaya, 15 Juni 1989",
                    agama = "Islam",
                    pekerjaan = "Guru SMA",
                    statusHubungan = "Istri",
                    statusTinggal = "Tetap",
                    noHp = "081382901929",
                    rt = "02",
                    blokNomor = "Blok B2 No. 14",
                    isKepalaKeluarga = false
                ),
                WargaEntity(
                    nik = "3276012204120003",
                    noKk = "3276012004120005",
                    nama = "Rizky Santoso",
                    jenisKelamin = "Laki-laki",
                    tempatTanggalLahir = "Bogor, 22 April 2012",
                    agama = "Islam",
                    pekerjaan = "Pelajar",
                    statusHubungan = "Anak",
                    statusTinggal = "Tetap",
                    noHp = "-",
                    rt = "02",
                    blokNomor = "Blok B2 No. 14",
                    isKepalaKeluarga = false
                ),
                WargaEntity(
                    nik = "3276014407900003",
                    noKk = "3276011809150009",
                    nama = "Siti Rahmawati",
                    jenisKelamin = "Perempuan",
                    tempatTanggalLahir = "Yogyakarta, 4 Juli 1990",
                    agama = "Islam",
                    pekerjaan = "Apoteker",
                    statusHubungan = "Kepala Keluarga",
                    statusTinggal = "Tetap",
                    noHp = "081909876543",
                    rt = "03",
                    blokNomor = "Blok C1 No. 05",
                    isKepalaKeluarga = true
                ),
                WargaEntity(
                    nik = "3276011802880004",
                    noKk = "3276010504140012",
                    nama = "Ahmad Fauzi",
                    jenisKelamin = "Laki-laki",
                    tempatTanggalLahir = "Solo, 18 Februari 1988",
                    agama = "Islam",
                    pekerjaan = "Wirausaha Kuliner",
                    statusHubungan = "Kepala Keluarga",
                    statusTinggal = "Tetap",
                    noHp = "085612347890",
                    rt = "04",
                    blokNomor = "Blok D3 No. 08",
                    isKepalaKeluarga = true
                ),
                WargaEntity(
                    nik = "3276012501930005",
                    noKk = "3276011902200021",
                    nama = "Agus Pratama",
                    jenisKelamin = "Laki-laki",
                    tempatTanggalLahir = "Medan, 25 Januari 1993",
                    agama = "Kristen Protestan",
                    pekerjaan = "Software Engineer",
                    statusHubungan = "Kepala Keluarga",
                    statusTinggal = "Kontrak/Kos",
                    noHp = "087890123456",
                    rt = "05",
                    blokNomor = "Blok E2 No. 03",
                    isKepalaKeluarga = true
                ),
                WargaEntity(
                    nik = "3276016010950006",
                    noKk = "3276012011220033",
                    nama = "Dewi Lestari",
                    jenisKelamin = "Perempuan",
                    tempatTanggalLahir = "Denpasar, 20 Oktober 1995",
                    agama = "Hindu",
                    pekerjaan = "Desainer Grafis",
                    statusHubungan = "Kepala Keluarga",
                    statusTinggal = "Tetap",
                    noHp = "082134567812",
                    rt = "06",
                    blokNomor = "Blok F1 No. 11",
                    isKepalaKeluarga = true
                )
            )
            for (w in initialWarga) {
                db.wargaDao().insert(w)
            }

            // 3. Initial Surat Pengajuan
            val initialSurat = listOf(
                SuratEntity(
                    nomorSurat = "470/012/RW-17/X/2026",
                    userId = wargaId,
                    namaPemohon = "Budi Santoso",
                    nik = "3276011203850001",
                    rt = "02",
                    jenisSurat = "Surat Pengantar SKCK",
                    keperluan = "Persyaratan berkas perpanjangan kontrak kerja BUMN",
                    status = "DISETUJUI",
                    tanggalPengajuan = "20 Oktober 2026",
                    tanggalSelesai = "21 Oktober 2026",
                    catatanPengurus = "Dokumen lengkap dan diverifikasi langsung oleh Ketua RW 17."
                ),
                SuratEntity(
                    nomorSurat = "474/015/RW-17/X/2026",
                    userId = wargaId,
                    namaPemohon = "Budi Santoso",
                    nik = "3276011203850001",
                    rt = "02",
                    jenisSurat = "Surat Keterangan Domisili",
                    keperluan = "Pengajuan pinjaman renovasi rumah Bank BTN",
                    status = "DIPROSES",
                    tanggalPengajuan = "22 Oktober 2026",
                    catatanPengurus = "Sedang dalam tahap verifikasi ketua RT 02."
                ),
                SuratEntity(
                    nomorSurat = "472/016/RW-17/X/2026",
                    userId = 3L,
                    namaPemohon = "Siti Rahmawati",
                    nik = "3276014407900003",
                    rt = "03",
                    jenisSurat = "Surat Keterangan Usaha",
                    keperluan = "Izin pembukaan gerai apotek mini di lingkungan",
                    status = "MENUNGGU",
                    tanggalPengajuan = "22 Oktober 2026",
                    catatanPengurus = null
                )
            )
            for (s in initialSurat) {
                db.suratDao().insert(s)
            }

            // 4. Initial Iuran Tagihan
            val initialTagihan = listOf(
                IuranTagihanEntity(
                    wargaNik = "3276011203850001",
                    namaWarga = "Budi Santoso",
                    rt = "02",
                    blokNomor = "Blok B2 No. 14",
                    periodeBulan = "Oktober",
                    periodeTahun = 2026,
                    nominal = 100000L,
                    status = "LUNAS",
                    tanggalBayar = "05 Oktober 2026",
                    metodeBayar = "QRIS RW 17",
                    buktiBayarRef = "TRX-QRIS-89301928"
                ),
                IuranTagihanEntity(
                    wargaNik = "3276011203850001",
                    namaWarga = "Budi Santoso",
                    rt = "02",
                    blokNomor = "Blok B2 No. 14",
                    periodeBulan = "November",
                    periodeTahun = 2026,
                    nominal = 100000L,
                    status = "BELUM_BAYAR"
                ),
                IuranTagihanEntity(
                    wargaNik = "3276014407900003",
                    namaWarga = "Siti Rahmawati",
                    rt = "03",
                    blokNomor = "Blok C1 No. 05",
                    periodeBulan = "Oktober",
                    periodeTahun = 2026,
                    nominal = 100000L,
                    status = "LUNAS",
                    tanggalBayar = "08 Oktober 2026",
                    metodeBayar = "Transfer BCA",
                    buktiBayarRef = "BCA-REF-771829"
                ),
                IuranTagihanEntity(
                    wargaNik = "3276011802880004",
                    namaWarga = "Ahmad Fauzi",
                    rt = "04",
                    blokNomor = "Blok D3 No. 08",
                    periodeBulan = "Oktober",
                    periodeTahun = 2026,
                    nominal = 100000L,
                    status = "BELUM_BAYAR"
                ),
                IuranTagihanEntity(
                    wargaNik = "3276012501930005",
                    namaWarga = "Agus Pratama",
                    rt = "05",
                    blokNomor = "Blok E2 No. 03",
                    periodeBulan = "Oktober",
                    periodeTahun = 2026,
                    nominal = 100000L,
                    status = "BELUM_BAYAR"
                ),
                IuranTagihanEntity(
                    wargaNik = "3276016010950006",
                    namaWarga = "Dewi Lestari",
                    rt = "06",
                    blokNomor = "Blok F1 No. 11",
                    periodeBulan = "Oktober",
                    periodeTahun = 2026,
                    nominal = 100000L,
                    status = "LUNAS",
                    tanggalBayar = "10 Oktober 2026",
                    metodeBayar = "QRIS RW 17",
                    buktiBayarRef = "TRX-QRIS-992010"
                )
            )
            for (t in initialTagihan) {
                db.iuranDao().insert(t)
            }

            // 5. Initial Kas Transaksi
            val initialKas = listOf(
                KasTransaksiEntity(
                    tanggal = "01 Oktober 2026",
                    tipe = "PEMASUKAN",
                    kategori = "Iuran Warga",
                    judul = "Penerimaan Iuran Warga Kolektif RT 01 - RT 06",
                    nominal = 8400000L,
                    keterangan = "Setoran iuran kebersihan & keamanan warga 84 KK",
                    dicatatOleh = "Bendahara RW 17"
                ),
                KasTransaksiEntity(
                    tanggal = "04 Oktober 2026",
                    tipe = "PEMASUKAN",
                    kategori = "Dana Swadaya",
                    judul = "Sponsorship & Donasi Warga untuk Penerangan Jalan",
                    nominal = 1500000L,
                    keterangan = "Donasi sukarela pengadaan 5 titik lampu sorot LED",
                    dicatatOleh = "Bendahara RW 17"
                ),
                KasTransaksiEntity(
                    tanggal = "05 Oktober 2026",
                    tipe = "PENGELUARAN",
                    kategori = "Keamanan",
                    judul = "Gaji Petugas Keamanan Pos 1 & Pos 2 (4 Orang)",
                    nominal = 4000000L,
                    keterangan = "Honorarium bulanan satpam lingkungan kavling",
                    dicatatOleh = "Bendahara RW 17"
                ),
                KasTransaksiEntity(
                    tanggal = "07 Oktober 2026",
                    tipe = "PENGELUARAN",
                    kategori = "Kebersihan",
                    judul = "Retribusi Pengangkutan Sampah DLHK & Honor Kru",
                    nominal = 1800000L,
                    keterangan = "Operasional armada truk sampah 3x seminggu",
                    dicatatOleh = "Bendahara RW 17"
                ),
                KasTransaksiEntity(
                    tanggal = "12 Oktober 2026",
                    tipe = "PENGELUARAN",
                    kategori = "Infrastruktur",
                    judul = "Penggantian 3 Bohlam PJU & MCB Listrik Blok B",
                    nominal = 450000L,
                    keterangan = "Respon laporan warga terkait lampu mati di RT 02",
                    dicatatOleh = "Sie. Pembangunan RW"
                ),
                KasTransaksiEntity(
                    tanggal = "18 Oktober 2026",
                    tipe = "PENGELUARAN",
                    kategori = "Operasional RW",
                    judul = "Perawatan 8 Titik CCTV & Bandwidth Internet Balai",
                    nominal = 350000L,
                    keterangan = "Maintenance bulanan server CCTV monitoring",
                    dicatatOleh = "Sie. Keamanan RW"
                )
            )
            for (k in initialKas) {
                db.kasDao().insert(k)
            }

            // 6. Initial Pengaduan Lingkungan with GPS
            val initialPengaduan = listOf(
                PengaduanEntity(
                    userId = wargaId,
                    namaPelapor = "Budi Santoso",
                    noHp = "081382901928",
                    kategori = "Lampu PJU / Kelistrikan",
                    judul = "Lampu PJU Padam di Pertigaan Blok B2",
                    deskripsi = "Lampu jalan penerangan umum sudah 2 hari padam, lokasi sangat gelap di malam hari dan membahayakan pengendara motor.",
                    lokasiDeskripsi = "Depan rumah Blok B2 No. 12 dekat pos kamling RT 02",
                    latitude = -6.291543,
                    longitude = 106.812398,
                    status = "PENANGANAN",
                    tanggalLapor = "20 Oktober 2026",
                    tanggapanPengurus = "Pengurus RW telah membeli sparepart LED dan dijadwalkan dipasang oleh teknisi sore ini.",
                    tanggalSelesai = null
                ),
                PengaduanEntity(
                    userId = 3L,
                    namaPelapor = "Siti Rahmawati",
                    noHp = "081909876543",
                    kategori = "Saluran Air & Banjir",
                    judul = "Sedimen Lumpur Tebal di Gorong-gorong Blok C1",
                    deskripsi = "Saluran air got tersumbat pasir dan sampah daun kering, jika hujan deras air meluap ke badan jalan.",
                    lokasiDeskripsi = "Saluran drainase utama Blok C1 samping taman bermain",
                    latitude = -6.292100,
                    longitude = 106.813050,
                    status = "DITINJAU",
                    tanggalLapor = "21 Oktober 2026",
                    tanggapanPengurus = "Ditinjau oleh Sie Lingkungan Hidup. Akan dimasukkan ke agenda kerja bakti Minggu.",
                    tanggalSelesai = null
                ),
                PengaduanEntity(
                    userId = wargaId,
                    namaPelapor = "Budi Santoso",
                    noHp = "081382901928",
                    kategori = "Sampah & Kebersihan",
                    judul = "Sampah Liar di Lahan Kosong Ujung Blok F",
                    deskripsi = "Ada pembuangan kantong sampah liar dari oknum luar kavling di samping pagar pembatas.",
                    lokasiDeskripsi = "Lahan kavling kosong Blok F dekat gapura belakang",
                    latitude = -6.293021,
                    longitude = 106.814210,
                    status = "SELESAI",
                    tanggalLapor = "15 Oktober 2026",
                    tanggapanPengurus = "Sudah diangkut oleh tim kebersihan dan dipasang plang larangan buang sampah bertanda tangan Ketua RW.",
                    tanggalSelesai = "16 Oktober 2026"
                ),
                PengaduanEntity(
                    userId = 4L,
                    namaPelapor = "Ahmad Fauzi",
                    noHp = "085612347890",
                    kategori = "Keamanan & Ketertiban",
                    judul = "Kucing Liar & Knalpot Brong Tengah Malam",
                    deskripsi = "Sering terjadi kendaraan bermotor melaju kencang dengan knalpot bising di atas jam 23.00 WIB.",
                    lokasiDeskripsi = "Jalan Utama Kavling depan Blok D",
                    latitude = -6.291880,
                    longitude = 106.811900,
                    status = "DIAJUKAN",
                    tanggalLapor = "22 Oktober 2026",
                    tanggapanPengurus = null,
                    tanggalSelesai = null
                )
            )
            for (p in initialPengaduan) {
                db.pengaduanDao().insert(p)
            }

            // 7. Initial Pengumuman
            val initialPengumuman = listOf(
                PengumumanEntity(
                    judul = "Kerja Bakti Massal RW 17: Pembersihan Saluran Air & Fogging",
                    kategori = "Kerja Bakti",
                    isi = "Diberitahukan kepada seluruh warga RT 01 s/d RT 06 Kavling RW 17, sehubungan memasuki musim penghujan akan diadakan kerja bakti serentak pada hari Minggu, 26 Oktober 2026 pukul 07.00 WIB berkumpul di Balai RW. Mohon membawa peralatan kerja bakti masing-masing. Konsumsi disediakan oleh ibu-ibu PKK.",
                    tanggal = "22 Oktober 2026",
                    penulis = "Ketua RW 17 - Bpk. H. Bambang Sutrisno",
                    isPinned = true
                ),
                PengumumanEntity(
                    judul = "Layanan Pembayaran Iuran Warga Digital Melalui QRIS Resmi RW 17",
                    kategori = "Keuangan",
                    isi = "Mulai bulan Oktober 2026, warga dapat membayar tagihan iuran bulanan kebersihan & keamanan (Rp 100.000) langsung via scan QRIS RW 17 dari berbagai aplikasi e-wallet (GoPay, OVO, Dana, ShopeePay) maupun Mobile Banking.",
                    tanggal = "18 Oktober 2026",
                    penulis = "Bendahara RW 17",
                    isPinned = true
                ),
                PengumumanEntity(
                    judul = "Jadwal Posyandu Balita & Posbindu Lansia Mawar RW 17",
                    kategori = "Posyandu",
                    isi = "Pelayanan penimbangan balita, imunisasi tambahan, serta cek tensi & gula darah gratis untuk lansia dilaksanakan pada hari Sabtu, 25 Oktober 2026 pukul 08.30 WIB di Balai Pertemuan RW 17.",
                    tanggal = "19 Oktober 2026",
                    penulis = "Kader Posyandu RW 17",
                    isPinned = false
                )
            )
            for (peng in initialPengumuman) {
                db.pengumumanDao().insert(peng)
            }

            // 8. Initial RW Settings
            val defaultSettings = RwSettingsEntity(
                id = 1,
                namaRw = "Kavling RW 17",
                namaKetuaRw = "Bpk. H. Bambang Sutrisno, S.T.",
                noHpKetuaRw = "081289871717",
                alamatSekretariat = "Balai Pertemuan Warga Kavling RW 17, Jl. Kavling Asri Utama Blok A1",
                rekeningBank = "Bank Central Asia (BCA)",
                noRekening = "8720192831",
                namaPemilikRekening = "KAS KAVLING RW 17",
                qrisMerchantName = "QRIS RW 17 KAVLING INDAH",
                defaultIuranNominal = 100000L,
                bannerNotice = "Selamat Datang di SIM RW 17 - Lingkungan Bersih, Aman & Transparan",
                runningTextAnnouncement = "Himbauan Pengurus RW: Kerja bakti massal Minggu pukul 07:00 WIB di seluruh wilayah RT 01 s/d RT 06. Pastikan iuran bulanan telah diselesaikan tepat waktu. Terima kasih."
            )
            db.rwSettingsDao().upsert(defaultSettings)

            // 9. Initial Notifikasi
            val initialNotif = listOf(
                NotifikasiEntity(
                    targetUserId = wargaId,
                    judul = "Surat Pengantar SKCK Disetujui",
                    pesan = "Surat Pengantar SKCK Anda (No: 470/012/RW-17/X/2026) telah diverifikasi dan disetujui oleh Ketua RW 17.",
                    tipe = "SURAT",
                    tanggal = "21 Oktober 2026, 14:30 WIB",
                    isRead = false
                ),
                NotifikasiEntity(
                    targetUserId = null,
                    judul = "Tagihan Iuran RW 17 Bulan Oktober 2026",
                    pesan = "Tagihan iuran kebersihan & keamanan bulan Oktober sebesar Rp 100.000 telah terbit. Pembayaran dapat dilakukan via QRIS atau transfer.",
                    tipe = "IURAN",
                    tanggal = "01 Oktober 2026, 08:00 WIB",
                    isRead = true
                ),
                NotifikasiEntity(
                    targetUserId = wargaId,
                    judul = "Status Pengaduan PJU Diperbarui",
                    pesan = "Aduan Anda mengenai Lampu PJU Blok B2 kini berstatus: DALAM PENANGANAN. Teknisi akan mengganti bohlam sore ini.",
                    tipe = "PENGADUAN",
                    tanggal = "22 Oktober 2026, 09:15 WIB",
                    isRead = false
                )
            )
            for (notif in initialNotif) {
                db.notifikasiDao().insert(notif)
            }
        }
    }
}
