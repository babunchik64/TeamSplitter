package com.app.teamsplitter.ui.screens.players

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.teamsplitter.data.model.Player
import com.app.teamsplitter.utils.FileUtils
import com.app.teamsplitter.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerEditScreen(navController: NavController, playerId: Int?) {
    val viewModel: PlayerViewModel = viewModel()
    val context = LocalContext.current

    // Если редактируем — загружаем игрока
    val existingPlayer by if (playerId != null) {
        viewModel.getById(playerId).observeAsState()
    } else {
        remember { mutableStateOf<Player?>(null) }
    }

    var name by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var isLoaded by remember { mutableStateOf(false) }

    // Заполняем поля когда загрузился игрок
    LaunchedEffect(existingPlayer) {
        if (existingPlayer != null && !isLoaded) {
            name = existingPlayer!!.name
            comment = existingPlayer!!.comment ?: ""
            photoPath = existingPlayer!!.photoPath
            isLoaded = true
        }
    }

    // Лаунчер для выбора фото из галереи
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = FileUtils.saveImageToAppStorage(context, it)
            if (savedPath != null) {
                photoPath = savedPath
            }
        }
    }

    var nameError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (playerId == null) "Добавить игрока" else "Редактировать",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Фото
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { photoLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoPath != null) {
                    AsyncImage(
                        model = photoPath,
                        contentDescription = "Фото игрока",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Добавить фото",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (photoPath != null) {
                TextButton(onClick = { photoPath = null }) {
                    Text("Удалить фото", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле имени
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Имя игрока *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = nameError,
                supportingText = {
                    if (nameError) Text("Введите имя игрока")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле комментария
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Комментарий") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка сохранить
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    if (playerId == null) {
                        viewModel.insert(Player(name.trim(), photoPath, comment.trim()))
                    } else {
                        existingPlayer?.let { player ->
                            player.setName(name.trim())
                            player.setComment(comment.trim())
                            player.setPhotoPath(photoPath)
                            viewModel.update(player)
                        }
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (playerId == null) "Добавить" else "Сохранить",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}