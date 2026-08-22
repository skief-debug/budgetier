package com.budgettracker.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

// ────────────────────────────────────────────────────
//  Dein GitHub-Repo hier eintragen:
//  Format: "DEIN_USERNAME/DEIN_REPO_NAME"
// ────────────────────────────────────────────────────
const val GITHUB_REPO = "ansga/budgetier"   // ← anpassen!

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val changelog: String,
    val downloadUrl: String,
    val apkSizeBytes: Long
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val info: UpdateInfo) : UpdateCheckResult()
    object UpToDate : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

object UpdateManager {

    private const val GITHUB_API = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

    /**
     * Prüft GitHub Releases auf neue Version.
     * Vergleicht tag-Name (z.B. "v2") mit aktuellem versionCode.
     */
    suspend fun checkForUpdate(currentVersionCode: Int): UpdateCheckResult =
        withContext(Dispatchers.IO) {
            try {
                val conn = URL(GITHUB_API).openConnection() as HttpURLConnection
                conn.apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "BudgeTier-App")
                    connectTimeout = 8_000
                    readTimeout = 8_000
                }

                if (conn.responseCode != 200) {
                    return@withContext UpdateCheckResult.Error("HTTP ${conn.responseCode}")
                }

                val body = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val json = JSONObject(body)

                // Tag format: "v2" → versionCode = 2
                val tagName = json.getString("tag_name").removePrefix("v")
                val remoteVersionCode = tagName.split(".").firstOrNull()?.toIntOrNull()
                    ?: return@withContext UpdateCheckResult.Error("Ungültiger Tag: $tagName")

                if (remoteVersionCode <= currentVersionCode) {
                    return@withContext UpdateCheckResult.UpToDate
                }

                val versionName = json.optString("name", tagName)
                val changelog = json.optString("body", "Neue Version verfügbar.").take(400)

                // APK-Asset aus Release-Assets heraussuchen
                val assets = json.getJSONArray("assets")
                var downloadUrl = ""
                var apkSize = 0L
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.getString("name")
                    if (name.endsWith(".apk")) {
                        downloadUrl = asset.getString("browser_download_url")
                        apkSize = asset.getLong("size")
                        break
                    }
                }

                if (downloadUrl.isEmpty()) {
                    return@withContext UpdateCheckResult.Error("Kein APK-Asset im Release gefunden.")
                }

                UpdateCheckResult.UpdateAvailable(
                    UpdateInfo(
                        versionCode = remoteVersionCode,
                        versionName = versionName,
                        changelog = changelog,
                        downloadUrl = downloadUrl,
                        apkSizeBytes = apkSize
                    )
                )
            } catch (e: Exception) {
                UpdateCheckResult.Error(e.message ?: "Unbekannter Fehler")
            }
        }

    /**
     * Lädt APK herunter und gibt Fortschritt via onProgress(0..100) zurück.
     * Gibt den lokalen File zurück wenn fertig.
     */
    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Int) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val destFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "BudgeTier_update.apk"
            )

            val conn = URL(downloadUrl).openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 60_000
                // GitHub redirects (301) → follow automatically
                instanceFollowRedirects = true
            }
            conn.connect()

            val totalBytes = conn.contentLengthLong
            var downloadedBytes = 0L

            conn.inputStream.use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                            withContext(Dispatchers.Main) { onProgress(progress) }
                        }
                    }
                }
            }
            conn.disconnect()
            withContext(Dispatchers.Main) { onProgress(100) }
            destFile
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Startet den System-Installer für die heruntergeladene APK.
     */
    fun installApk(context: Context, apkFile: File) {
        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }
}
