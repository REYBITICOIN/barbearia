package com.example.ui.components

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberGold
import kotlinx.coroutines.delay

const val DEFAULT_BARBER_VIDEO_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

/**
 * Fullscreen 10-second Splash Screen with video logo "Barbearia do João - Corte Moderno e Clássico"
 * and a "Pular" button in the top right corner.
 */
@Composable
fun BarberAppSplashScreen(
    videoUrl: String = DEFAULT_BARBER_VIDEO_URL,
    shopName: String = "Barbearia do João - Corte Moderno e Clássico",
    durationMillis: Long = 10000L,
    onDismiss: () -> Unit
) {
    // Auto-dismiss splash screen after durationMillis (10 seconds)
    LaunchedEffect(Unit) {
        delay(durationMillis)
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("fullscreen_splash_screen")
        ) {
            // Fullscreen Video Player
            SplashVideoPlayer(
                videoUrl = videoUrl,
                modifier = Modifier.fillMaxSize()
            )

            // Top Bar with Brand Title & "Pular" Button (Top Right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Barber Shop Brand Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BarberGold)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCut,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = shopName.ifBlank { "BARBEARIA DO JOÃO" },
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "CORTE MODERNO E CLÁSSICO",
                            color = BarberGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                // BOTÃO DE PULAR (CORNER TOP RIGHT)
                Surface(
                    onClick = onDismiss,
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BarberGold),
                    modifier = Modifier
                        .testTag("btn_skip_splash")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Pular",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Pular Vídeo Splash",
                            tint = BarberGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Brand Overlay Info
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Barbearia do João",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Corte Moderno e Clássico • Especializada em cortes modernos, degradês e tradicionais",
                            color = BarberAiCyan,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = BarberGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Acessar Painel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Animated Barber Logo / Video Avatar Component.
 * Replaces the static barber profile photo with the video logo of "Barbearia do João".
 */
@Composable
fun BarberLogoVideoAvatar(
    primaryColor: Color,
    videoUrl: String = DEFAULT_BARBER_VIDEO_URL,
    shopName: String = "Barbearia do João - Corte Moderno e Clássico",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, primaryColor, RoundedCornerShape(16.dp))
            .background(Color.Black)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Video Player inside Avatar Frame
        SplashVideoPlayer(
            videoUrl = videoUrl,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Badge indicating Video Logo
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(2.dp)
                .background(primaryColor, CircleShape)
                .size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Vídeo Logo",
                tint = Color.Black,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@Composable
fun SplashVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    var isError by remember { mutableStateOf(false) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isError) {
            // Animated Fallback Logo Component if Video offline
            BarberShopLogoGraphicFallback()
        } else {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val uri = Uri.parse(videoUrl)
                        setVideoURI(uri)

                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            start()
                        }

                        setOnErrorListener { _, _, _ ->
                            isError = true
                            true
                        }
                    }
                },
                update = { videoView ->
                    if (!videoView.isPlaying) {
                        try {
                            videoView.start()
                        } catch (_: Exception) {}
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Custom Vector Graphics Fallback representing the "Barbearia do João - Corte Moderno e Clássico" Logo
 */
@Composable
fun BarberShopLogoGraphicFallback() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14181D)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCut,
                contentDescription = null,
                tint = BarberGold,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "BARBEARIA DO JOÃO",
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 9.sp
            )
            Text(
                text = "CORTE MODERNO E CLÁSSICO",
                fontWeight = FontWeight.Bold,
                color = BarberAiCyan,
                fontSize = 7.sp
            )
        }
    }
}
