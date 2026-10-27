package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String,
    val role: String, // "SUPER_ADMIN" or "WARGA"
    val nama: String,
    val nik: String,
    val noKk: String,
    val noHp: String,
    val rt: String,
    val noRumah: String,
    val tempatTanggalLahir: String = "",
    val alamatLengkap: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "warga",
    indices = [Index(value = ["nik"], unique = true)]
)
data class WargaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nik: String,
    val noKk: String,
    val nama: String,
    val jenisKelamin: String, // "Laki-laki" or "Perempuan"
    val tempatTanggalLahir: String,
    val agama: String,
    val pekerjaan: String,
    val statusHubungan: String, // "Kepala Keluarga", "Istri", "Anak", dll
    val statusTinggal: String, // "Tetap" or "Kontrak/Kos"
    val noHp: String,
    val rt: String,
    val blokNomor: String,
    val alamatLengkap: String = "",
    val isKepalaKeluarga: Boolean = false
)

@Entity(tableName = "surat_pengajuan")
data class SuratEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nomorSurat: String,
    val userId: Long,
    val namaPemohon: String,
    val nik: String,
    val rt: String,
    val jenisSurat: String,
    val keperluan: String,
    val status: String, // "MENUNGGU", "DIPROSES", "DISETUJUI", "DITOLAK"
    val tanggalPengajuan: String,
    val tanggalSelesai: String? = null,
    val catatanPengurus: String? = null
)

@Entity(tableName = "iuran_tagihan")
data class IuranTagihanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wargaNik: String,
    val namaWarga: String,
    val rt: String,
    val blokNomor: String,
    val periodeBulan: String,
    val periodeTahun: Int,
    val nominal: Long,
    val status: String, // "BELUM_BAYAR", "MENUNGGU_KONFIRMASI", "LUNAS"
    val tanggalBayar: String? = null,
    val metodeBayar: String? = null,
    val buktiBayarRef: String? = null
)

@Entity(tableName = "kas_transaksi")
data class KasTransaksiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tanggal: String,
    val tipe: String, // "PEMASUKAN" or "PENGELUARAN"
    val kategori: String,
    val judul: String,
    val nominal: Long,
    val keterangan: String,
    val dicatatOleh: String
)

@Entity(tableName = "pengaduan_lingkungan")
data class PengaduanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val namaPelapor: String,
    val noHp: String,
    val kategori: String,
    val judul: String,
    val deskripsi: String,
    val lokasiDeskripsi: String,
    val latitude: Double,
    val longitude: Double,
    val status: String, // "DIAJUKAN", "DITINJAU", "PENANGANAN", "SELESAI"
    val tanggalLapor: String,
    val tanggapanPengurus: String? = null,
    val tanggalSelesai: String? = null
)

@Entity(tableName = "pengumuman")
data class PengumumanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val judul: String,
    val kategori: String,
    val isi: String,
    val tanggal: String,
    val penulis: String,
    val isPinned: Boolean = false
)

@Entity(tableName = "rw_settings")
data class RwSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val namaRw: String = "Kavling RW 17",
    val namaKetuaRw: String = "Bpk. H. Bambang Sutrisno, S.T.",
    val noHpKetuaRw: String = "081289871717",
    val alamatSekretariat: String = "Balai Pertemuan Warga Kavling RW 17, Jl. Melati Kav. A1",
    val rekeningBank: String = "Bank Central Asia (BCA)",
    val noRekening: String = "8720192831",
    val namaPemilikRekening: String = "KAS KAVLING RW 17",
    val qrisMerchantName: String = "QRIS RW 17 KAVLING INDAH",
    val defaultIuranNominal: Long = 100000L,
    val bannerNotice: String = "Selamat Datang di SIM RW 17 - Lingkungan Mandiri, Transparan & Terintegrasi",
    val runningTextAnnouncement: String = "Himbauan Pengurus RW: Kerja bakti massal Minggu pukul 07:00 WIB di seluruh wilayah RT 01 s/d RT 06. Mari jaga keamanan & kebersihan lingkungan kita.",
    val accentTheme: String = "Navy Civic",
    val kopSuratInstansi: String = "PENGURUS RUKUN WARGA 17",
    val kopSuratKelurahan: String = "KELURAHAN KAVLING ASRI - KECAMATAN SEJAHTERA",
    val kopSuratKontak: String = "Sekretariat: Jl. Melati Kav. A1 RT 01/RW 17 • Telp: 081289871717",
    val daftarJenisSurat: String = "Surat Pengantar KTP / KK,Surat Keterangan Domisili,Surat Pengantar SKCK,Surat Keterangan Usaha,Surat Keterangan Belum Menikah,Surat Keterangan Kematian,Surat Pengantar Pindah Domisili,Surat Keterangan Tidak Mampu",
    val formatNomorSurat: String = "470/[NOMOR]/RW-17/X/2026",
    val pejabatPenandatangan: String = "Bpk. H. Bambang Sutrisno, S.T.",
    val jabatanPenandatangan: String = "Ketua RW 17",
    val catatanLegalitasFooter: String = "Surat keterangan ini sah dan terotorisasi secara elektronik melalui Sistem Informasi Digital Terintegrasi RW 17."
)

@Entity(tableName = "notifikasi")
data class NotifikasiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetUserId: Long? = null, // null for broadcast
    val judul: String,
    val pesan: String,
    val tipe: String, // "SURAT", "IURAN", "PENGADUAN", "PENGUMUMAN"
    val tanggal: String,
    val isRead: Boolean = false
)
