package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.IuranTagihanEntity
import com.example.data.model.KasTransaksiEntity
import com.example.data.model.NotifikasiEntity
import com.example.data.model.PengaduanEntity
import com.example.data.model.PengumumanEntity
import com.example.data.model.RwSettingsEntity
import com.example.data.model.SuratEntity
import com.example.data.model.UserEntity
import com.example.data.model.WargaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface WargaDao {
    @Query("SELECT * FROM warga ORDER BY rt ASC, blokNomor ASC, nama ASC")
    fun getAllFlow(): Flow<List<WargaEntity>>

    @Query("SELECT * FROM warga WHERE nama LIKE '%' || :query || '%' OR nik LIKE '%' || :query || '%' OR blokNomor LIKE '%' || :query || '%' OR noKk LIKE '%' || :query || '%' ORDER BY nama ASC")
    fun searchWargaFlow(query: String): Flow<List<WargaEntity>>

    @Query("SELECT * FROM warga WHERE rt = :rt ORDER BY blokNomor ASC")
    fun getByRtFlow(rt: String): Flow<List<WargaEntity>>

    @Query("SELECT * FROM warga WHERE noKk = :noKk ORDER BY isKepalaKeluarga DESC, nama ASC")
    fun getByKkFlow(noKk: String): Flow<List<WargaEntity>>

    @Query("SELECT COUNT(*) FROM warga")
    fun countTotalFlow(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT noKk) FROM warga")
    fun countKkFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(warga: WargaEntity): Long

    @Update
    suspend fun update(warga: WargaEntity)

    @Query("UPDATE warga SET nama = :nama WHERE id = :id")
    suspend fun updateNama(id: Long, nama: String)

    @Delete
    suspend fun delete(warga: WargaEntity)

    @Query("SELECT COUNT(*) FROM warga")
    suspend fun countSync(): Int
}

@Dao
interface SuratDao {
    @Query("SELECT * FROM surat_pengajuan ORDER BY id DESC")
    fun getAllFlow(): Flow<List<SuratEntity>>

    @Query("SELECT * FROM surat_pengajuan WHERE userId = :userId ORDER BY id DESC")
    fun getByUserIdFlow(userId: Long): Flow<List<SuratEntity>>

    @Query("SELECT COUNT(*) FROM surat_pengajuan WHERE status = 'MENUNGGU'")
    fun getPendingCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(surat: SuratEntity): Long

    @Query("UPDATE surat_pengajuan SET status = :status, catatanPengurus = :catatan, tanggalSelesai = :tanggalSelesai WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, catatan: String?, tanggalSelesai: String?)

    @Query("SELECT COUNT(*) FROM surat_pengajuan")
    suspend fun countSync(): Int
}

@Dao
interface IuranDao {
    @Query("SELECT * FROM iuran_tagihan ORDER BY periodeTahun DESC, id DESC")
    fun getAllFlow(): Flow<List<IuranTagihanEntity>>

    @Query("SELECT * FROM iuran_tagihan WHERE wargaNik = :nik ORDER BY periodeTahun DESC, id DESC")
    fun getByNikFlow(nik: String): Flow<List<IuranTagihanEntity>>

    @Query("SELECT COUNT(*) FROM iuran_tagihan WHERE status = 'LUNAS'")
    fun countLunasFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM iuran_tagihan WHERE status != 'LUNAS'")
    fun countNunggakFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tagihan: IuranTagihanEntity): Long

    @Query("UPDATE iuran_tagihan SET status = :status, tanggalBayar = :tanggalBayar, metodeBayar = :metode, buktiBayarRef = :ref WHERE id = :id")
    suspend fun updatePayment(id: Long, status: String, tanggalBayar: String?, metode: String?, ref: String?)

    @Query("SELECT COUNT(*) FROM iuran_tagihan")
    suspend fun countSync(): Int
}

@Dao
interface KasDao {
    @Query("SELECT * FROM kas_transaksi ORDER BY id DESC")
    fun getAllFlow(): Flow<List<KasTransaksiEntity>>

    @Query("SELECT COALESCE(SUM(nominal), 0) FROM kas_transaksi WHERE tipe = 'PEMASUKAN'")
    fun getTotalPemasukanFlow(): Flow<Long>

    @Query("SELECT COALESCE(SUM(nominal), 0) FROM kas_transaksi WHERE tipe = 'PENGELUARAN'")
    fun getTotalPengeluaranFlow(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaksi: KasTransaksiEntity): Long

    @Query("DELETE FROM kas_transaksi WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(transaksi: KasTransaksiEntity)

    @Query("SELECT COUNT(*) FROM kas_transaksi")
    suspend fun countSync(): Int
}

@Dao
interface PengaduanDao {
    @Query("SELECT * FROM pengaduan_lingkungan ORDER BY id DESC")
    fun getAllFlow(): Flow<List<PengaduanEntity>>

    @Query("SELECT * FROM pengaduan_lingkungan WHERE userId = :userId ORDER BY id DESC")
    fun getByUserIdFlow(userId: Long): Flow<List<PengaduanEntity>>

    @Query("SELECT COUNT(*) FROM pengaduan_lingkungan WHERE status != 'SELESAI'")
    fun countAktifFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aduan: PengaduanEntity): Long

    @Query("UPDATE pengaduan_lingkungan SET status = :status, tanggapanPengurus = :tanggapan, tanggalSelesai = :tanggalSelesai WHERE id = :id")
    suspend fun updateTindakLanjut(id: Long, status: String, tanggapan: String?, tanggalSelesai: String?)

    @Query("SELECT COUNT(*) FROM pengaduan_lingkungan")
    suspend fun countSync(): Int
}

@Dao
interface PengumumanDao {
    @Query("SELECT * FROM pengumuman ORDER BY isPinned DESC, id DESC")
    fun getAllFlow(): Flow<List<PengumumanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pengumuman: PengumumanEntity): Long

    @Delete
    suspend fun delete(pengumuman: PengumumanEntity)

    @Query("SELECT COUNT(*) FROM pengumuman")
    suspend fun countSync(): Int
}

@Dao
interface RwSettingsDao {
    @Query("SELECT * FROM rw_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<RwSettingsEntity?>

    @Query("SELECT * FROM rw_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): RwSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: RwSettingsEntity)
}

@Dao
interface NotifikasiDao {
    @Query("SELECT * FROM notifikasi WHERE targetUserId IS NULL OR targetUserId = :userId ORDER BY id DESC")
    fun getNotificationsFlow(userId: Long): Flow<List<NotifikasiEntity>>

    @Query("SELECT COUNT(*) FROM notifikasi WHERE (targetUserId IS NULL OR targetUserId = :userId) AND isRead = 0")
    fun getUnreadCountFlow(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notifikasi: NotifikasiEntity): Long

    @Query("UPDATE notifikasi SET isRead = 1 WHERE targetUserId IS NULL OR targetUserId = :userId")
    suspend fun markAllAsRead(userId: Long)

    @Query("SELECT COUNT(*) FROM notifikasi")
    suspend fun countSync(): Int
}
