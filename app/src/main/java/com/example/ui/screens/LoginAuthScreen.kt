package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.BarberLabViewModel
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor

@Composable
fun LoginAuthDialog(
    viewModel: BarberLabViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    // Form mode: 0 = Login, 1 = Criar Conta (Firebase), 2 = Esqueci a Senha
    var authTabMode by remember { mutableIntStateOf(0) }

    // Inputs
    var emailOrUsername by remember { mutableStateOf("admin@barberlab.com") }
    var password by remember { mutableStateOf("123456") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Barbeiro Senior") }
    var passwordVisible by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val rolesList = listOf("Proprietário", "Barbeiro Master", "Barbeiro Senior", "Atendente")

    val demoAccounts = listOf(
        Triple("admin@barberlab.com", "123456", "Mestre Luan (Proprietário)"),
        Triple("barbeiro1@barberlab.com", "123456", "Lucas Fade (Barbeiro Senior)"),
        Triple("vintage@barberlab.com", "123456", "Pedro Ramos (Vintage Club)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentUser == null) "Firebase Auth - BarberLab" else "Perfil do Barbeiro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = primaryColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "FIREBASE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryColor,
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
                    .padding(vertical = 4.dp)
            ) {
                if (currentUser != null) {
                    // Logged in View
                    val user = currentUser!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (user.avatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = user.fullName,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(primaryColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.fullName.take(1).ifBlank { "B" },
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 22.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text("Cargo: ${user.role}", fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.SemiBold)
                                Text("ID/Usuário: @${user.username}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "Sessão encerrada no Firebase Auth", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.85f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_logout")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sair da Conta (Firebase Logout)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Tab Selector: 0 = Entrar | 1 = Criar Conta | 2 = Redefinir
                    TabRow(
                        selectedTabIndex = authTabMode,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = primaryColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = authTabMode == 0,
                            onClick = {
                                authTabMode = 0
                                statusMessage = null
                            },
                            text = { Text("Entrar", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = authTabMode == 1,
                            onClick = {
                                authTabMode = 1
                                statusMessage = null
                            },
                            text = { Text("Criar Conta", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = authTabMode == 2,
                            onClick = {
                                authTabMode = 2
                                statusMessage = null
                            },
                            text = { Text("Redefinir", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    when (authTabMode) {
                        0 -> {
                            // FORM 0: LOGIN
                            Text(
                                text = "Acesse com seu e-mail cadastrado no Firebase Auth.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                label = { Text("E-mail do Barbeiro") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = primaryColor) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_email_login"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Senha") },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryColor) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_password_login"),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { authTabMode = 2 },
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("Esqueceu a senha?", fontSize = 11.sp, color = BarberAiCyan)
                                }
                            }
                        }

                        1 -> {
                            // FORM 1: CRIAR CONTA (REGISTER)
                            Text(
                                text = "Cadastre um novo barbeiro com e-mail e senha no Firebase.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Nome Completo do Barbeiro *") },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = primaryColor) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_fullname_signup"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                label = { Text("E-mail Profissional *") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = primaryColor) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_email_signup"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Senha (Mínimo 6 caracteres) *") },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryColor) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_password_signup"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Cargo na Barbearia:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rolesList.take(2).forEach { r ->
                                    FilterChip(
                                        selected = selectedRole == r,
                                        onClick = { selectedRole = r },
                                        label = { Text(r, fontSize = 10.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        2 -> {
                            // FORM 2: REDEFINIR SENHA
                            Text(
                                text = "Digite seu e-mail cadastrado. Enviaremos um link do Firebase Auth para redefinir sua senha de acesso.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                label = { Text("Seu E-mail Cadastrado") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = primaryColor) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_email_reset"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )
                        }
                    }

                    // Mensagem de Erro ou Sucesso
                    loginError?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = err,
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    statusMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BarberAiCyan.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = BarberAiCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contas de Demonstração (Atalho)
                    if (authTabMode == 0) {
                        Text(
                            text = "Acesso Rápido de Demonstração (Toque para preencher):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BarberAiCyan
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        demoAccounts.forEach { (email, pwd, label) ->
                            Surface(
                                onClick = {
                                    emailOrUsername = email
                                    password = pwd
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text(email.substringBefore("@"), fontSize = 10.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (currentUser == null) {
                Button(
                    onClick = {
                        statusMessage = null
                        when (authTabMode) {
                            0 -> {
                                viewModel.loginWithFirebase(emailOrUsername, password) { success, err ->
                                    if (success) {
                                        Toast.makeText(context, "Login realizado via Firebase!", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                }
                            }
                            1 -> {
                                viewModel.signUpWithFirebase(
                                    emailInput = emailOrUsername,
                                    passwordInput = password,
                                    fullNameInput = fullName,
                                    roleInput = selectedRole
                                ) { success, err ->
                                    if (success) {
                                        Toast.makeText(context, "Conta criada com sucesso no Firebase!", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                }
                            }
                            2 -> {
                                viewModel.sendPasswordResetEmail(emailOrUsername) { success, msg ->
                                    statusMessage = msg
                                }
                            }
                        }
                    },
                    enabled = !isAuthLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                    modifier = Modifier.testTag("btn_perform_auth")
                ) {
                    if (isAuthLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = when (authTabMode) {
                            0 -> "Entrar com Firebase"
                            1 -> "Criar Conta Firebase"
                            else -> "Enviar E-mail"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black)
                ) {
                    Text("Concluído", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

