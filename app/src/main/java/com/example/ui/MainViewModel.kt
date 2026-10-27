package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Repository
import com.example.data.model.KasTransaksiEntity
import com.example.data.model.PengumumanEntity
import com.example.data.model.RwSettingsEntity
import com.example.data.model.WargaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository.getInstance(db)
    }

    val currentUser = repository.currentUser

    // RW Settings
    val rwSettings = repository.getSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RwSettingsEntity())

    // Warga state & filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRtFilter = MutableStateFlow("SEMUA")
    val selectedRtFilter: StateFlow<String> = _selectedRtFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedRtFilter(rt: String) {
        _selectedRtFilter.value = rt
    }

    val wargaList = combine(
        repository.getAllWargaFlow(),
        _searchQuery,
        _selectedRtFilter
    ) { list, query, rtFilter ->
        list.filter { warga ->
            val matchesSearch = query.isBlank() ||
                    warga.nama.contains(query, ignoreCase = true) ||
                    warga.nik.contains(query, ignoreCase = true) ||
                    warga.blokNomor.contains(query, ignoreCase = true) ||
                    warga.noKk.contains(query, ignoreCase = true)
            val matchesRt = rtFilter == "SEMUA" || warga.rt == rtFilter
            matchesSearch && matchesRt
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWargaCount = repository.countTotalWargaFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalKkCount = repository.countKkFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Surat state
    val allSuratList = repository.getAllSuratFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingSuratCount = repository.getPendingSuratCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Iuran state
    val allIuranList = repository.getAllIuranFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val iuranLunasCount = repository.countLunasIuranFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val iuranNunggakCount = repository.countNunggakIuranFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Keuangan & Kas
    val kasTransactions = repository.getKasFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPemasukan = repository.getTotalPemasukanFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalPengeluaran = repository.getTotalPengeluaranFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val saldoKas = combine(totalPemasukan, totalPengeluaran) { masukan, keluaran ->
        masukan - keluaran
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Pengaduan
    val allPengaduanList = repository.getAllPengaduanFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pengaduanAktifCount = repository.countAktifPengaduanFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Pengumuman
    val allPengumumanList = repository.getAllPengumumanFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications = combine(currentUser, repository.getAllWargaFlow()) { user, _ ->
        user?.id ?: 0L
    }.combine(repository.getAllSuratFlow()) { userId, _ ->
        userId
    }.combine(repository.getAllPengaduanFlow()) { userId, _ ->
        userId
    }

    // Actions
    fun switchRole(role: String) {
        viewModelScope.launch {
            repository.switchAccount(role)
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.login(email, pass)
            res.fold(
                onSuccess = { onResult(true, "Login berhasil") },
                onFailure = { onResult(false, it.message ?: "Login gagal") }
            )
        }
    }

    fun registerWarga(
        email: String,
        pass: String,
        nama: String,
        nik: String,
        noKk: String,
        noHp: String,
        rt: String,
        noRumah: String,
        tempatTanggalLahir: String,
        alamatLengkap: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerWarga(
                email = email,
                pass = pass,
                nama = nama,
                nik = nik,
                noKk = noKk,
                noHp = noHp,
                rt = rt,
                noRumah = noRumah,
                tempatTanggalLahir = tempatTanggalLahir,
                alamatLengkap = alamatLengkap
            )
            res.fold(
                onSuccess = { onResult(true, "Pendaftaran berhasil") },
                onFailure = { onResult(false, it.message ?: "Pendaftaran gagal") }
            )
        }
    }

    fun logout() {
        repository.logout()
    }

    fun saveWarga(warga: WargaEntity, isNew: Boolean) {
        viewModelScope.launch {
            if (isNew) {
                repository.addWarga(warga)
            } else {
                repository.updateWarga(warga)
            }
        }
    }

    fun deleteWarga(warga: WargaEntity) {
        viewModelScope.launch {
            repository.deleteWarga(warga)
        }
    }

    fun updateWargaNama(id: Long, newNama: String) {
        viewModelScope.launch {
            repository.updateWargaNama(id, newNama)
        }
    }

    fun submitSurat(
        namaPemohon: String? = null,
        nikPemohon: String? = null,
        rtPemohon: String? = null,
        jenisSurat: String,
        keperluan: String,
        onComplete: () -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.submitSurat(
                userId = user.id,
                nama = if (!namaPemohon.isNullOrBlank()) namaPemohon else user.nama,
                nik = if (!nikPemohon.isNullOrBlank()) nikPemohon else user.nik,
                rt = if (!rtPemohon.isNullOrBlank()) rtPemohon else user.rt,
                jenisSurat = jenisSurat,
                keperluan = keperluan
            )
            onComplete()
        }
    }

    fun verifySurat(suratId: Long, status: String, catatan: String?, userId: Long, namaSurat: String, noSurat: String) {
        viewModelScope.launch {
            repository.updateSuratStatus(suratId, status, catatan, userId, namaSurat, noSurat)
        }
    }

    fun payIuran(tagihanId: Long, metode: String, refNumber: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.payIuran(tagihanId, metode, refNumber, user.nama)
        }
    }

    fun submitPengaduan(
        kategori: String,
        judul: String,
        deskripsi: String,
        lokasi: String,
        lat: Double,
        lng: Double,
        onComplete: () -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.submitPengaduan(
                userId = user.id,
                namaPelapor = user.nama,
                noHp = user.noHp,
                kategori = kategori,
                judul = judul,
                deskripsi = deskripsi,
                lokasiDeskripsi = lokasi,
                latitude = lat,
                longitude = lng
            )
            onComplete()
        }
    }

    fun updatePengaduanStatus(id: Long, status: String, tanggapan: String?, userId: Long, judul: String) {
        viewModelScope.launch {
            repository.updatePengaduanTindakLanjut(id, status, tanggapan, userId, judul)
        }
    }

    fun addKasManual(tipe: String, kategori: String, judul: String, nominal: Long, keterangan: String) {
        viewModelScope.launch {
            repository.addKasTransaksi(
                KasTransaksiEntity(
                    tanggal = Repository.getFormattedCurrentDate(),
                    tipe = tipe,
                    kategori = kategori,
                    judul = judul,
                    nominal = nominal,
                    keterangan = keterangan,
                    dicatatOleh = currentUser.value?.nama ?: "Pengurus RW"
                )
            )
        }
    }

    fun deleteKasTransaksi(id: Long) {
        viewModelScope.launch {
            repository.deleteKasTransaksi(id)
        }
    }

    fun addPengumuman(judul: String, kategori: String, isi: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.addPengumuman(
                PengumumanEntity(
                    judul = judul,
                    kategori = kategori,
                    isi = isi,
                    tanggal = Repository.getFormattedCurrentDate(),
                    penulis = currentUser.value?.nama ?: "Pengurus RW",
                    isPinned = isPinned
                )
            )
        }
    }

    fun deletePengumuman(p: PengumumanEntity) {
        viewModelScope.launch {
            repository.deletePengumuman(p)
        }
    }

    fun updateRwSettings(updated: RwSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(updated)
        }
    }
}
