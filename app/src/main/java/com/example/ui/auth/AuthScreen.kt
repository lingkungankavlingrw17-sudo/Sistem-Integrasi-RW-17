package com.example.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.theme.CivicBluePrimary
import com.example.ui.theme.CivicEmeraldSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Daftar Warga

    // Login Fields (no prefill per production requirements)
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Register Fields
    var regNama by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPass by remember { mutableStateOf("") }
    var regNik by remember { mutableStateOf("") }
    var regNoKk by remember { mutableStateOf("") }
    var regTempatLahir by remember { mutableStateOf("") }
    var regTanggalLahir by remember { mutableStateOf("") }
    var regAlamatLengkap by remember { mutableStateOf("") }
    var regNoHp by remember { mutableStateOf("") }
    var regRt by remember { mutableStateOf("01") }
    var regNoRumah by remember { mutableStateOf("") }
    var rtExpanded by remember { mutableStateOf(false) }

    val rtOptions = listOf("01", "02", "03", "04", "05", "06")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CivicBluePrimary,
                        Color(0xFF1E293B),
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f,
                    endY = 750f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Official Logo RW 17 Image
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.size(92.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_rw17),
                        contentDescription = "Logo Resmi RW 17",
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "SISTEM INFORMASI TERINTEGRASI",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "RW 17 KAVLING",
                color = Color(0xFFFCD34D),
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Rukun • Damai • Sejahtera",
                color = Color(0xFF93C5FD),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Masuk", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Daftar Warga", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedTab == 0) {
                        // --- LOGIN FORM ---
                        Text(
                            text = "Login Akun",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Silakan masukkan email dan kata sandi Anda",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email") },
                            placeholder = { Text("nama@email.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Kata Sandi") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                    Toast.makeText(context, "Mohon lengkapi email dan kata sandi", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.login(loginEmail, loginPassword) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivicBluePrimary)
                        ) {
                            Text("Masuk ke Sistem", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }



                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        // --- REGISTER WARGA FORM ---
                        Text(
                            text = "Pendaftaran Akun Warga",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Lengkapi data kependudukan untuk akses layanan digital RW 17",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = regNama,
                            onValueChange = { regNama = it },
                            label = { Text("Nama Lengkap Sesuai KTP") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Warga") },
                            placeholder = { Text("warga@email.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPass,
                            onValueChange = { regPass = it },
                            label = { Text("Kata Sandi Akun") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regNik,
                                onValueChange = { if (it.length <= 16) regNik = it },
                                label = { Text("NIK (16 Digit)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("register_nik_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = regNoKk,
                                onValueChange = { if (it.length <= 16) regNoKk = it },
                                label = { Text("No. KK") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("register_kk_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Added: Tempat & Tanggal Lahir
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regTempatLahir,
                                onValueChange = { regTempatLahir = it },
                                label = { Text("Tempat Lahir") },
                                placeholder = { Text("Mis: Jakarta") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("register_tempat_lahir_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = regTanggalLahir,
                                onValueChange = { regTanggalLahir = it },
                                label = { Text("Tanggal Lahir") },
                                placeholder = { Text("Mis: 15-08-1990") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("register_tanggal_lahir_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Added: Alamat Lengkap Rumah
                        OutlinedTextField(
                            value = regAlamatLengkap,
                            onValueChange = { regAlamatLengkap = it },
                            label = { Text("Alamat Lengkap Rumah") },
                            placeholder = { Text("Contoh: Jl. Kavling Asri Utama Blok B2 No. 14, RT 02 / RW 17") },
                            minLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_alamat_lengkap_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = rtExpanded,
                                onExpandedChange = { rtExpanded = !rtExpanded },
                                modifier = Modifier.weight(0.45f)
                            ) {
                                OutlinedTextField(
                                    value = "RT $regRt",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("RT") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rtExpanded) },
                                    modifier = Modifier.menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = rtExpanded,
                                    onDismissRequest = { rtExpanded = false }
                                ) {
                                    rtOptions.forEach { rt ->
                                        DropdownMenuItem(
                                            text = { Text("RT $rt") },
                                            onClick = {
                                                regRt = rt
                                                rtExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = regNoRumah,
                                onValueChange = { regNoRumah = it },
                                label = { Text("Blok & No. Rumah") },
                                placeholder = { Text("Mis: Blok B2 No. 14") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.55f)
                                    .testTag("register_rumah_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regNoHp,
                            onValueChange = { regNoHp = it },
                            label = { Text("Nomor HP / WhatsApp Aktif") },
                            placeholder = { Text("0812xxxxxxxx") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_phone_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (regNama.isBlank() || regEmail.isBlank() || regPass.isBlank() || regNik.isBlank()) {
                                    Toast.makeText(context, "Harap lengkapi nama, email, sandi, dan NIK", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val ttl = if (regTempatLahir.isNotBlank() || regTanggalLahir.isNotBlank()) {
                                    "${regTempatLahir.trim()}, ${regTanggalLahir.trim()}"
                                } else ""

                                viewModel.registerWarga(
                                    email = regEmail,
                                    pass = regPass,
                                    nama = regNama,
                                    nik = regNik,
                                    noKk = regNoKk,
                                    noHp = regNoHp,
                                    rt = regRt,
                                    noRumah = regNoRumah,
                                    tempatTanggalLahir = ttl,
                                    alamatLengkap = regAlamatLengkap
                                ) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("register_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivicEmeraldSecondary)
                        ) {
                            Text("Daftar Sekarang", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
