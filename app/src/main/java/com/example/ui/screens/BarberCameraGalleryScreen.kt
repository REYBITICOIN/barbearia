package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.data.local.BarberMediaEntity
import com.example.ui.BarberLabViewModel
import com.example.ui.theme.BarberAiCyan
import com.example.ui.theme.BarberDarkCard
import com.example.ui.theme.BarberGold
import com.example.ui.theme.parseHexColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberCameraGalleryScreen(viewModel: BarberLabViewModel) {
    val context = LocalContext.current
    val activeShop by viewModel.activeBarbershop.collectAsState()
    val mediaList by viewModel.barberMedia.collectAsState()
    val barbers by viewModel.barbers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val primaryColor = parseHexColor(activeShop?.primaryColorHex, BarberGold)

    var currentSubTab by remember { mutableStateOf(1) } // Default to Portfolio Gallery (1)
    var selectedMediaForView by remember { mutableStateOf<BarberMediaEntity?>(null) }
    var showSaveDialog by remember { mutableStateOf<String?>(null) } // Uri/Url string when photo captured

    // Selected Barber Filter for Portfolio Organization ("ALL" or specific barberId)
    var selectedBarberIdFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStyleFilter by remember { mutableStateOf("TODOS") }

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Image Picker fallback / gallery import
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            showSaveDialog = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Sub-Tabs Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BarberDarkCard, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { currentSubTab = 0 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("subtab_camera"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentSubTab == 0) primaryColor else Color.Transparent,
                    contentColor = if (currentSubTab == 0) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Câmera / Captura", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = { currentSubTab = 1 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("subtab_gallery"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentSubTab == 1) primaryColor else Color.Transparent,
                    contentColor = if (currentSubTab == 1) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Portfólio / Galeria (${mediaList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentSubTab == 0) {
            // Camera Tab
            if (!hasCameraPermission) {
                CameraPermissionRequestView(
                    onRequestPermission = { launcher.launch(Manifest.permission.CAMERA) },
                    onPickFromGallery = { galleryPickerLauncher.launch("image/*") },
                    primaryColor = primaryColor
                )
            } else {
                CameraCaptureView(
                    primaryColor = primaryColor,
                    barberName = currentUser?.fullName ?: activeShop?.ownerName ?: "Barbeiro",
                    onPhotoCaptured = { uriStr ->
                        showSaveDialog = uriStr
                    },
                    onOpenGallery = { galleryPickerLauncher.launch("image/*") }
                )
            }
        } else {
            // Media Portfolio Gallery Tab with Coil & Individual Barber Organization
            BarberCoilGalleryView(
                mediaList = mediaList,
                barbers = barbers,
                primaryColor = primaryColor,
                selectedBarberIdFilter = selectedBarberIdFilter,
                onSelectBarberFilter = { selectedBarberIdFilter = it },
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedStyleFilter = selectedStyleFilter,
                onStyleFilterChange = { selectedStyleFilter = it },
                onMediaClick = { selectedMediaForView = it },
                onDeleteMedia = { viewModel.deleteBarberMedia(it.mediaId) },
                onPublishToMarketing = { media ->
                    viewModel.publishMetaPost(media.haircutStyle, "Veja o resultado espetacular do nosso profissional ${media.barberName}!")
                    Toast.makeText(context, "Mídia enviada para divulgação nas redes via Agente IA!", Toast.LENGTH_SHORT).show()
                },
                onAddPhotoForBarber = {
                    currentSubTab = 0
                }
            )
        }
    }

    // Save Media Modal Dialog
    showSaveDialog?.let { uriStr ->
        SaveMediaMetadataDialog(
            uriStr = uriStr,
            primaryColor = primaryColor,
            barbers = barbers,
            defaultBarberName = currentUser?.fullName ?: activeShop?.ownerName ?: "Barbeiro",
            onDismiss = { showSaveDialog = null },
            onSave = { title, style, client, selectedBarber, isVideo ->
                viewModel.addBarberMedia(
                    mediaType = if (isVideo) "VIDEO" else "PHOTO",
                    fileUriOrUrl = uriStr,
                    title = title,
                    haircutStyle = style,
                    clientName = client,
                    barberId = selectedBarber.barberId,
                    barberName = selectedBarber.name
                )
                showSaveDialog = null
                currentSubTab = 1 // Switch to gallery
                Toast.makeText(context, "Mídia cadastrada no portfólio de ${selectedBarber.name}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Fullscreen View Dialog
    selectedMediaForView?.let { media ->
        FullscreenMediaDialog(
            media = media,
            primaryColor = primaryColor,
            onDismiss = { selectedMediaForView = null }
        )
    }
}

@Composable
fun CameraPermissionRequestView(
    onRequestPermission: () -> Unit,
    onPickFromGallery: () -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Permissão de Câmera Necessária",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Para capturar fotos e vídeos dos cortes e barbas direto no app BarberLab, ative o acesso à câmera.",
                fontSize = 14.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_request_camera_perm"),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Permitir Acesso à Câmera", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPickFromGallery,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_pick_gallery_fallback"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BarberAiCyan)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escolher Foto/Vídeo do Dispositivo")
            }
        }
    }
}

@Composable
fun ColumnScope.CameraCaptureView(
    primaryColor: Color,
    barberName: String,
    onPhotoCaptured: (String) -> Unit,
    onOpenGallery: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isSimulatedCapture by remember { mutableStateOf(false) }

    val cameraExecutor: Executor = remember(context) { ContextCompat.getMainExecutor(context) }

    val sampleHaircutUrls = remember {
        listOf(
            "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?w=800",
            "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800",
            "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=800",
            "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=800",
            "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Live CameraX Preview View
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imgCapture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()
                            imageCapture = imgCapture

                            val cameraSelector = if (isFrontCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imgCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, cameraExecutor)

                    previewView
                },
                update = {
                    // Update if camera position toggles
                }
            )

            // Overlay Camera Controls Grid
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Camera Overlay Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Red, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CAMERAX LIVE - $barberName",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera }
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = "Alternar Câmera", tint = primaryColor)
                    }
                }

                // Bottom Controls Bar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                        .padding(top = 24.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = "Toque no botão central para capturar a foto do trabalho",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery Button
                        IconButton(
                            onClick = onOpenGallery,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.DarkGray.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeria", tint = Color.White)
                        }

                        // Main Capture Trigger Button
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .border(4.dp, primaryColor, CircleShape)
                                .padding(6.dp)
                                .background(Color.White, CircleShape)
                                .clickable {
                                    val capture = imageCapture
                                    if (capture != null) {
                                        val photoFile = File(
                                            context.cacheDir,
                                            "haircut_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                                        )
                                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                        capture.takePicture(
                                            outputOptions,
                                            cameraExecutor,
                                            object : ImageCapture.OnImageSavedCallback {
                                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                    val savedUri = Uri.fromFile(photoFile).toString()
                                                    onPhotoCaptured(savedUri)
                                                }

                                                override fun onError(exception: ImageCaptureException) {
                                                    // Fallback in case device emulator camera produces zero-byte
                                                    val randomSample = sampleHaircutUrls.random()
                                                    onPhotoCaptured(randomSample)
                                                }
                                            }
                                        )
                                    } else {
                                        // Fallback simulation capture
                                        val randomSample = sampleHaircutUrls.random()
                                        onPhotoCaptured(randomSample)
                                    }
                                }
                                .testTag("btn_take_photo_camerax"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Camera,
                                contentDescription = "Tirar Foto",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Quick Sample Test Button
                        IconButton(
                            onClick = {
                                val randomSample = sampleHaircutUrls.random()
                                onPhotoCaptured(randomSample)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(BarberAiCyan.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Exemplo Rápido", tint = BarberAiCyan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaveMediaMetadataDialog(
    uriStr: String,
    primaryColor: Color,
    barbers: List<com.example.data.local.BarberEntity>,
    defaultBarberName: String,
    onDismiss: () -> Unit,
    onSave: (title: String, style: String, client: String, selectedBarber: com.example.data.local.BarberEntity, isVideo: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var haircutStyle by remember { mutableStateOf("Mid Fade") }
    var clientName by remember { mutableStateOf("") }
    var isVideo by remember { mutableStateOf(false) }

    // Selected Barber
    var selectedBarber by remember {
        mutableStateOf(barbers.firstOrNull() ?: com.example.data.local.BarberEntity(
            barberId = "barber_master",
            tenantId = "master",
            name = defaultBarberName,
            specialty = "Barbeiro Master",
            photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            phone = "11999999999"
        ))
    }

    val popularStyles = listOf("Mid Fade", "Taper Fade", "Barba Imperial", "Low Fade", "Corte Tesoura", "Pigmentação")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Salvar no Portfólio do Barbeiro", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Image Preview Box using Coil AsyncImage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = uriStr,
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Select Barber Portfolio
                Text("Barbeiro Responsável pelo Corte:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(4.dp))

                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(barbers.size) { index ->
                        val b = barbers[index]
                        val isSelected = selectedBarber.barberId == b.barberId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBarber = b },
                            label = { Text(b.name, fontSize = 11.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                ) {
                                    AsyncImage(
                                        model = b.photoUrl,
                                        contentDescription = b.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Corte / Trabalho") },
                    placeholder = { Text("ex: Mid Fade com Degradê Perfeito") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_media_title"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nome do Cliente (Opcional)") },
                    placeholder = { Text("ex: Lucas Gabriel") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_media_client"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Estilo / Categoria:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(4.dp))

                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(popularStyles.size) { idx ->
                        val styleOption = popularStyles[idx]
                        FilterChip(
                            selected = haircutStyle == styleOption,
                            onClick = { haircutStyle = styleOption },
                            label = { Text(styleOption, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = if (title.isBlank()) "$haircutStyle por ${selectedBarber.name}" else title
                    onSave(finalTitle, haircutStyle, clientName, selectedBarber, isVideo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                modifier = Modifier.testTag("btn_save_barber_media")
            ) {
                Text("Salvar no Portfólio", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = BarberDarkCard
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberCoilGalleryView(
    mediaList: List<BarberMediaEntity>,
    barbers: List<com.example.data.local.BarberEntity>,
    primaryColor: Color,
    selectedBarberIdFilter: String,
    onSelectBarberFilter: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStyleFilter: String,
    onStyleFilterChange: (String) -> Unit,
    onMediaClick: (BarberMediaEntity) -> Unit,
    onDeleteMedia: (BarberMediaEntity) -> Unit,
    onPublishToMarketing: (BarberMediaEntity) -> Unit,
    onAddPhotoForBarber: () -> Unit
) {
    val styleCategories = listOf("TODOS", "Mid Fade", "Taper Fade", "Barba", "Pigmentação", "Tesoura")

    // Filter media list by barber, search query, and style
    val filteredMedia = mediaList.filter { media ->
        val matchesBarber = selectedBarberIdFilter == "ALL" || media.barberId == selectedBarberIdFilter || media.barberName.contains(selectedBarberIdFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || media.title.contains(searchQuery, ignoreCase = true) || media.haircutStyle.contains(searchQuery, ignoreCase = true) || media.barberName.contains(searchQuery, ignoreCase = true)
        val matchesStyle = selectedStyleFilter == "TODOS" || media.haircutStyle.contains(selectedStyleFilter, ignoreCase = true)
        matchesBarber && matchesSearch && matchesStyle
    }

    val selectedBarberObj = barbers.find { it.barberId == selectedBarberIdFilter }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. Barber Individual Portfolio Selector Chips ---
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PORTFÓLIOS INDIVIDUAIS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "${filteredMedia.size} mídias",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // "All Barbers" Chip
                item {
                    val isSelected = selectedBarberIdFilter == "ALL"
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectBarberFilter("ALL") },
                        label = { Text("Todos os Profissionais (${mediaList.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("filter_barber_all")
                    )
                }

                // Individual Barber Portfolio Chips with Coil Avatar Icons
                items(barbers.size) { idx ->
                    val barber = barbers[idx]
                    val isSelected = selectedBarberIdFilter == barber.barberId
                    val barberPhotoCount = mediaList.count { it.barberId == barber.barberId || it.barberName == barber.name }

                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectBarberFilter(barber.barberId) },
                        label = { Text("${barber.name} ($barberPhotoCount)", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, if (isSelected) Color.Black else primaryColor, CircleShape)
                            ) {
                                AsyncImage(
                                    model = barber.photoUrl,
                                    contentDescription = barber.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("filter_barber_${barber.barberId}")
                    )
                }
            }
        }

        // --- 2. Active Individual Barber Header Card (If a specific barber is selected) ---
        selectedBarberObj?.let { barber ->
            val barberMediaList = mediaList.filter { it.barberId == barber.barberId || it.barberName == barber.name }
            Card(
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(2.dp, primaryColor, CircleShape)
                    ) {
                        AsyncImage(
                            model = barber.photoUrl,
                            contentDescription = barber.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(barber.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = BarberGold, shape = RoundedCornerShape(4.dp)) {
                                Text("★ ${barber.rating}", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Especialidade: ${barber.specialty}", fontSize = 11.sp, color = BarberAiCyan)
                        Text("Portfólio Individual: ${barberMediaList.size} trabalhos cadastrados", fontSize = 11.sp, color = Color.LightGray)
                    }

                    IconButton(
                        onClick = onAddPhotoForBarber,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(primaryColor)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Adicionar Foto", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // --- 3. Search & Category Filters Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Buscar no portfólio...", fontSize = 12.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFF2E3842),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("input_search_portfolio"),
                singleLine = true
            )
        }

        // Category Filter Chips
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(styleCategories.size) { i ->
                val cat = styleCategories[i]
                val isSelected = selectedStyleFilter == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { onStyleFilterChange(cat) },
                    label = { Text(cat, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BarberAiCyan.copy(alpha = 0.3f),
                        selectedLabelColor = BarberAiCyan
                    )
                )
            }
        }

        // --- 4. Portfolio Coil Images Grid ---
        if (filteredMedia.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = BarberDarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Collections, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Nenhum resultado no portfólio selecionado", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tire fotos de novos cortes ou limpe os filtros de busca.", fontSize = 12.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredMedia, key = { it.mediaId }) { media ->
                    MediaCardItem(
                        media = media,
                        primaryColor = primaryColor,
                        onClick = { onMediaClick(media) },
                        onDelete = { onDeleteMedia(media) },
                        onPublish = { onPublishToMarketing(media) }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaCardItem(
    media: BarberMediaEntity,
    primaryColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPublish: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("media_card_${media.mediaId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BarberDarkCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Coil AsyncImage for smooth image loading and rendering
                AsyncImage(
                    model = media.fileUriOrUrl,
                    contentDescription = media.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Style Badge
                Surface(
                    color = primaryColor,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = media.haircutStyle,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (media.mediaType == "VIDEO") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Vídeo", tint = Color.White)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = media.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BarberAiCyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = media.barberName,
                        fontSize = 11.sp,
                        color = BarberAiCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPublish,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Divulgar nas Redes", tint = BarberAiCyan, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FullscreenMediaDialog(
    media: BarberMediaEntity,
    primaryColor: Color,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black)
            ) {
                Text("Fechar", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = media.fileUriOrUrl,
                        contentDescription = media.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(media.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text("Estilo: ${media.haircutStyle} • Barbeiro: ${media.barberName}", fontSize = 12.sp, color = primaryColor)
                if (media.clientName.isNotBlank()) {
                    Text("Cliente: ${media.clientName}", fontSize = 12.sp, color = Color.LightGray)
                }
            }
        },
        containerColor = BarberDarkCard
    )
}

