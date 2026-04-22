package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.presentation.theme.PexelsAppTheme
import com.example.pexelsapp.utils.ThemedPreview

@Composable
fun AccountSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val user by viewModel.user.collectAsState()

    AccountSettingsScreenContent(
        user = user,
        onBack = onBack,
        onLogout = { viewModel.logout() },
        onChangePhoto = { /* Stub */ }
    )
}

@Composable
private fun AccountSettingsScreenContent(
    user: User?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onChangePhoto: () -> Unit
) {
    SettingsSubScreen(title = stringResource(id = R.string.account), onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Large Profile Photo
            Box(contentAlignment = Alignment.BottomEnd) {
                if (!user?.photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = user?.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                
                // Change Photo Button Stub
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onChangePhoto,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.change_photo))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile info
            InfoRow(label = stringResource(id = R.string.profile_name), value = user?.name ?: stringResource(R.string.profile_guest))
            HorizontalDivider(thickness = 0.5.dp)
            InfoRow(label = stringResource(id = R.string.profile_email), value = user?.email ?: "")
            
            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.logout))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@ThemedPreview
@Composable
private fun AccountSettingsScreenPreview() {
    PexelsAppTheme {
        AccountSettingsScreenContent(
            user = User("1", "test@test.com", "John Doe", ""),
            onBack = {},
            onLogout = {},
            onChangePhoto = {}
        )
    }
}

private fun Modifier.width(dp: Int) = this.padding(horizontal = dp.dp) // dummy for Spacer modifier if needed, but Spacer(modifier = Modifier.width(8.dp)) is correct
