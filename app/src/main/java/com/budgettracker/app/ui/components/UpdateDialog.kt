package com.budgettracker.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.budgettracker.app.utils.UpdateInfo
import com.budgettracker.app.utils.UpdateManager
import kotlinx.coroutines.launch
import java.io.File

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_000_000 -> "%.1f MB".format(bytes / 1_000_000.0)
    bytes >= 1_000 -> "%.0f KB".format(bytes / 1_000.0)
    else -> "$bytes B"
}

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var downloadProgress by remember { mutableStateOf(0) }
    var isDownloading by remember { mutableStateOf(false) }
    var isInstallReady by remember { mutableStateOf(false) }
    var downloadedFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Pulsating animation for the update icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Dialog(
        onDismissRequest = { if (!isDownloading) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = !isDownloading, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF00E676).copy(alpha = pulseAlpha * 0.3f),
                                    Color(0xFF00BCD4).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚀", fontSize = 36.sp)
                }

                Text(
                    text = "Update verfügbar!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                // Version Badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF2A2A2A),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Aktuell",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text("→", color = Color.Gray)
                    Surface(
                        color = Color(0xFF00E676).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "v${updateInfo.versionName}  ✨",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Changelog
                if (updateInfo.changelog.isNotBlank()) {
                    Surface(
                        color = Color(0xFF2A2A2A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Was ist neu",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = updateInfo.changelog,
                                color = Color(0xFFE0E0E0),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // APK Size
                Text(
                    text = "📦 ${formatBytes(updateInfo.apkSizeBytes)}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                // Download Progress
                if (isDownloading || isInstallReady) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF00E676),
                            trackColor = Color(0xFF2A2A2A)
                        )
                        Text(
                            text = if (isInstallReady) "✅ Download abgeschlossen!" else "⬇ Lade herunter… $downloadProgress%",
                            color = if (isInstallReady) Color(0xFF00E676) else Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                // Error
                errorMessage?.let { msg ->
                    Text(
                        text = "❌ $msg",
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Abbrechen / Später
                    if (!isDownloading) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A3A3A))
                        ) {
                            Text("Später", fontSize = 14.sp)
                        }
                    }

                    // Haupt-Button
                    Button(
                        onClick = {
                            when {
                                isInstallReady && downloadedFile != null -> {
                                    // Installieren
                                    UpdateManager.installApk(context, downloadedFile!!)
                                }
                                !isDownloading -> {
                                    // Download starten
                                    isDownloading = true
                                    errorMessage = null
                                    scope.launch {
                                        val file = UpdateManager.downloadApk(
                                            context = context,
                                            downloadUrl = updateInfo.downloadUrl,
                                            onProgress = { downloadProgress = it }
                                        )
                                        isDownloading = false
                                        if (file != null) {
                                            downloadedFile = file
                                            isInstallReady = true
                                        } else {
                                            errorMessage = "Download fehlgeschlagen. Bitte erneut versuchen."
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInstallReady) Color(0xFF00BCD4) else Color(0xFF00E676),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isDownloading
                    ) {
                        Text(
                            text = when {
                                isDownloading -> "Lädt…"
                                isInstallReady -> "Jetzt installieren"
                                else -> "Updaten ↓"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
