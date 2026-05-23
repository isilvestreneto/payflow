package com.payflow.app.ui.screens.login

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.payflow.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.usuario) {
        if (state.usuario != null) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "PayFlow",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(
                        imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha"
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.loginComEmail(email, senha) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && email.isNotBlank() && senha.isNotBlank()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Entrar")
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("  ou  ", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.loginComGoogle(context) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Entrar com Google")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { viewModel.mostrarCadastro() }) {
            Text("Não tem conta? Criar conta")
        }

        state.erro?.let { erro ->
            Spacer(Modifier.height(16.dp))
            Text(
                erro,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    // Modal de cadastro
    if (state.mostrarCadastro) {
        CadastroBottomSheet(
            isLoading = state.isLoading,
            erro = state.erro,
            onDismiss = { viewModel.fecharCadastro() },
            onCadastrar = { nome, emailCad, senhaCad ->
                viewModel.cadastrar(nome, emailCad, senhaCad)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CadastroBottomSheet(
    isLoading: Boolean,
    erro: String?,
    onDismiss: () -> Unit,
    onCadastrar: (nome: String, email: String, senha: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmarSenha by rememberSaveable { mutableStateOf("") }
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }
    var confirmarSenhaVisivel by rememberSaveable { mutableStateOf(false) }

    val emailValido by remember(email) {
        derivedStateOf { Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    }
    val senhaValida by remember(senha) {
        derivedStateOf { senha.length >= 6 && senha.all { it.isDigit() } }
    }
    val senhasConferem by remember(senha, confirmarSenha) {
        derivedStateOf { senha == confirmarSenha && confirmarSenha.isNotEmpty() }
    }
    val formularioValido by remember(nome, emailValido, senhaValida, senhasConferem) {
        derivedStateOf { nome.isNotBlank() && emailValido && senhaValida && senhasConferem }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Criar Conta",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = email.isNotEmpty() && !emailValido,
                supportingText = if (email.isNotEmpty() && !emailValido) {
                    { Text("Email inválido") }
                } else null
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it.filter { c -> c.isDigit() } },
                label = { Text("Senha (6+ dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                isError = senha.isNotEmpty() && !senhaValida,
                supportingText = if (senha.isNotEmpty() && !senhaValida) {
                    { Text("Mínimo 6 dígitos numéricos") }
                } else null,
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (senhaVisivel) "Ocultar" else "Mostrar"
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it.filter { c -> c.isDigit() } },
                label = { Text("Confirmar senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                isError = confirmarSenha.isNotEmpty() && !senhasConferem,
                supportingText = if (confirmarSenha.isNotEmpty() && !senhasConferem) {
                    { Text("Senhas não conferem") }
                } else null,
                trailingIcon = {
                    IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                        Icon(
                            imageVector = if (confirmarSenhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmarSenhaVisivel) "Ocultar" else "Mostrar"
                        )
                    }
                }
            )

            erro?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onCadastrar(nome, email, senha) },
                modifier = Modifier.fillMaxWidth(),
                enabled = formularioValido && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Criar conta")
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text("Já tenho conta")
            }
        }
    }
}