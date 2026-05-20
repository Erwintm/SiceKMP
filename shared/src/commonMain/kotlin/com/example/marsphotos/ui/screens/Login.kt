package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import sicekmp.shared.generated.resources.Res
import sicekmp.shared.generated.resources.contrasena
import sicekmp.shared.generated.resources.entrar
import sicekmp.shared.generated.resources.login
import sicekmp.shared.generated.resources.usuario

@Composable
fun LoginPantalla(
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel // Quitamos la inicialización por defecto con Factory vieja
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Res.string.login), fontSize = 26.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.usuario,
            onValueChange = { viewModel.usuario = it },
            label = { Text(stringResource(Res.string.usuario)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text(stringResource(Res.string.contrasena)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(onLoginSuccess = { matricula ->
                    onLoginSuccess(matricula)
                })
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(4.dp))
            } else {
                Text(stringResource(Res.string.entrar))
            }
        }

        viewModel.mensajeError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}