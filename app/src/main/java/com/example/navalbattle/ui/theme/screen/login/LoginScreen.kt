package com.example.navalbattle.ui.theme.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navalbattle.R
import com.naval.battle.ui.component.CustomButton
import com.naval.battle.ui.component.CustomTextField
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    isLightTheme: Boolean,
    viewModel: LoginViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    // State for password visibility and dialogs
    var passwordVisible by remember { mutableStateOf(false) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    // Adjust sizes and spacing based on orientation
    val padding = if (isLandscape) 12.dp else 24.dp
    val imageSize = if (isLandscape) 60.dp else 128.dp
    val spacerHeight = if (isLandscape) 6.dp else 16.dp
    val buttonSpacerHeight = if (isLandscape) 10.dp else 24.dp
    val bottomSpacerHeight = if (isLandscape) 10.dp else 100.dp
    val textFieldWidthFraction = if (isLandscape) 0.8f else 0.9f
    val textStyle = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    val errorTextSize = if (isLandscape) 12.sp else 14.sp
    val titleSize = if (isLandscape) 24.sp else 32.sp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isLightTheme) listOf(Color.White, Color(0xFFE0E0E0))
                    else listOf(Color(0xFF003087), Color(0xFF4FC3F7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(padding)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo
            Image(
                painter = painterResource(id = R.drawable.intro),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(imageSize)
                    .padding(bottom = if (isLandscape) 4.dp else 16.dp)
            )

            // App name
            Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isLightTheme) Color(0xFF747B81) else Color.White,
                modifier = Modifier.padding(bottom = if (isLandscape) 8.dp else 24.dp)
            )

            CustomTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.value = it },
                label = stringResource(id = R.string.login_email_placeholder),
                isLightTheme = isLightTheme,
                isError = viewModel.emailError.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(textFieldWidthFraction),
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            // Password field with visibility toggle
            PasswordTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = stringResource(id = R.string.login_password_placeholder),
                isLightTheme = isLightTheme,
                isError = viewModel.passwordError.value,
                passwordVisible = passwordVisible,
                onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                modifier = Modifier.fillMaxWidth(textFieldWidthFraction),
            )

            // Forgot Password link
            Box(
                modifier = Modifier
                    .fillMaxWidth(textFieldWidthFraction)
                    .padding(top = 4.dp, bottom = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = if (isLightTheme) MaterialTheme.colorScheme.primary else Color(0xFF90CAF9),
                    style = textStyle,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { showPasswordResetDialog = true }
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(buttonSpacerHeight))

            CustomButton(
                text = stringResource(id = R.string.login),
                onClick = {
                    viewModel.signIn {
                        navController.navigate("menu") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                isLightTheme = isLightTheme,
                modifier = Modifier.fillMaxWidth(textFieldWidthFraction),
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            CustomButton(
                text = stringResource(id = R.string.register),
                onClick = {
                    viewModel.registerWithEmailVerification { success ->
                        if (success) {}
                    }
                },
                isLightTheme = isLightTheme,
                modifier = Modifier.fillMaxWidth(textFieldWidthFraction),
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            // Feedback message area
            Spacer(modifier = Modifier.height(spacerHeight))

            viewModel.errorMessage.value?.let {
                val isSuccess = it.contains("successfully") || it.contains("verification")
                val backgroundColor = if (isSuccess) {
                    if (isLightTheme) Color(0xFFE8F5E9) else Color(0xFF1B5E20).copy(alpha = 0.7f)
                } else {
                    if (isLightTheme) Color(0xFFFFEBEE) else Color(0xFFB71C1C).copy(alpha = 0.7f)
                }
                val textColor = if (isSuccess) {
                    if (isLightTheme) Color(0xFF2E7D32) else Color(0xFFA5D6A7)
                } else {
                    if (isLightTheme) Color(0xFFB71C1C) else Color(0xFFEF9A9A)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(textFieldWidthFraction)
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = it,
                        color = textColor,
                        style = textStyle.copy(fontSize = errorTextSize),
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Clear error message after 4 seconds
            LaunchedEffect(viewModel.errorMessage.value) {
                if (viewModel.errorMessage.value != null) {
                    delay(4000)
                    viewModel.clearUserData()
                }
            }

            Spacer(modifier = Modifier.height(bottomSpacerHeight))
        }

        // Password Reset Dialog
        if (showPasswordResetDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordResetDialog = false },
                title = { Text(text = stringResource(id = R.string.reset_password)) },
                text = {
                    Column {
                        Text(text = stringResource(id = R.string.reset_password_instruction))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text(text = stringResource(id = R.string.reset_password_instruction)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetPassword(resetEmail) { success ->
                                if (success) {
                                    showPasswordResetDialog = false
                                }
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.send_reset_link))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPasswordResetDialog = false }
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isLightTheme: Boolean,
    isError: Boolean = false,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isLightTheme = isLightTheme,
            isError = isError,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        IconButton(
            onClick = onTogglePasswordVisibility,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            Icon(
                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                tint = if (isLightTheme) Color.DarkGray else Color.LightGray
            )
        }
    }
}