package com.app.teamsplitter.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.teamsplitter.data.model.Player
import com.app.teamsplitter.viewmodel.HistoryViewModel
import com.app.teamsplitter.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(navController: NavController) {
    val playerViewModel: PlayerViewModel = viewModel()
    val allPlayers by playerViewModel.getAllPlayers().observeAsState(initial = emptyList<Player>())

    var step by remember { mutableStateOf(1) }
    val selectedPlayers = remember { mutableStateListOf<Player>() }

    if (step == 1) {
        SelectPlayersStep(
            allPlayers = allPlayers,
            selectedPlayers = selectedPlayers,
            onNext = { step = 2 }
        )
    } else {
        SplitTeamsStep(
            selectedPlayers = selectedPlayers,
            onBack = { step = 1 }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPlayersStep(
    allPlayers: List<Player>,
    selectedPlayers: MutableList<Player>,
    onNext: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = TopAppBarDefaults.windowInsets
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onNext,
                    enabled = selectedPlayers.size >= 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Divide into teams (${selectedPlayers.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    ) { innerPadding ->
        if (allPlayers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No players",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add players first",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Players (${allPlayers.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        TextButton(onClick = {
                            if (selectedPlayers.size == allPlayers.size) {
                                selectedPlayers.clear()
                            } else {
                                selectedPlayers.clear()
                                selectedPlayers.addAll(allPlayers)
                            }
                        }) {
                            Text(
                                if (selectedPlayers.size == allPlayers.size) "Remove everyone" else "Select all"
                            )
                        }
                    }
                }
                items(allPlayers) { player ->
                    val isSelected = selectedPlayers.contains(player)
                    PlayerSelectCard(
                        player = player,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) selectedPlayers.remove(player)
                            else selectedPlayers.add(player)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerSelectCard(
    player: Player,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(16.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (player.photoPath != null) {
                AsyncImage(
                    model = player.photoPath,
                    contentDescription = player.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.first().uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                if (!player.comment.isNullOrEmpty()) {
                    Text(
                        text = player.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitTeamsStep(
    selectedPlayers: List<Player>,
    onBack: () -> Unit
) {
    val historyViewModel: HistoryViewModel = viewModel()
    val teamNames = remember { mutableStateListOf("Team A", "Team B") }
    val teamPlayers = remember {
        mutableStateListOf(
            mutableStateListOf<Player>(),
            mutableStateListOf<Player>()
        )
    }
    val maxPlayersPerTeam = 6

    var draggingPlayer by remember { mutableStateOf<Player?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val teamBounds = remember { mutableMapOf<Int, androidx.compose.ui.geometry.Rect>() }
    var showSaveDialog by remember { mutableStateOf(false) }
    var sessionSaved by remember { mutableStateOf(false) }

    fun findPlayerTeam(player: Player): Int? {
        teamPlayers.forEachIndexed { index, players ->
            if (players.any { it.id == player.id }) return index
        }
        return null
    }

    fun isAssigned(player: Player) = findPlayerTeam(player) != null

    fun findDropTarget(pos: Offset): Int? {
        teamBounds.forEach { (index, bounds) ->
            if (bounds.contains(pos)) return index
        }
        return null
    }

    fun assignToTeam(player: Player, teamIndex: Int) {
        if (teamPlayers[teamIndex].size < maxPlayersPerTeam &&
            !teamPlayers[teamIndex].any { it.id == player.id }
        ) {
            teamPlayers[teamIndex].add(player)
        }
    }

    fun removeFromTeam(player: Player) {
        teamPlayers.forEach { it.removeAll { p -> p.id == player.id } }
    }

    // Диалог сохранения
    if (showSaveDialog) {
        val assigned = teamPlayers.sumOf { it.size }
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save session?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("The following will be saved:")
                    Text("• Players: ${selectedPlayers.size}")
                    Text("• Teams: ${teamNames.size}")
                    Text("• Assigned: $assigned players")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val teamPlayersCopy = teamPlayers.map { it.toList() }
                    historyViewModel.saveSession(
                        selectedPlayers.toList(),
                        teamNames.toList(),
                        teamPlayersCopy,
                        null
                    )
                    sessionSaved = true
                    showSaveDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Teams", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    windowInsets = TopAppBarDefaults.windowInsets,
                    actions = {
                        if (sessionSaved) {
                            Text(
                                "✓ Saved",
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            TextButton(onClick = { showSaveDialog = true }) {
                                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        TextButton(onClick = {
                            teamPlayers.forEach { it.clear() }
                            val shuffled = selectedPlayers.shuffled()
                            val playersPerTeam = Math.ceil(shuffled.size.toDouble() / teamPlayers.size).toInt()

                            // Якщо не вміщуються — додаємо команди автоматично
                            if (playersPerTeam > maxPlayersPerTeam) {
                                val neededTeams = Math.ceil(shuffled.size.toDouble() / maxPlayersPerTeam).toInt()
                                while (teamPlayers.size < neededTeams) {
                                    teamNames.add("Team ${('A' + teamPlayers.size)}")
                                    teamPlayers.add(mutableStateListOf())
                                }
                            }

                            shuffled.forEachIndexed { index, player ->
                                teamPlayers[index % teamPlayers.size].add(player)
                            }
                        }) {
                            Text("Randomize", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp)
            ) {
                Text(
                    "Players (${selectedPlayers.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedPlayers.forEach { player ->
                        val assigned = isAssigned(player)
                        val isDragging = draggingPlayer?.id == player.id
                        var cellPosition by remember { mutableStateOf(Offset.Zero) }
                        var showMenu by remember { mutableStateOf(false) }

                        Box {
                            Box(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(48.dp)
                                    .onGloballyPositioned { coords ->
                                        cellPosition = coords.boundsInWindow().topLeft
                                    }
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isDragging -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            assigned -> MaterialTheme.colorScheme.surfaceVariant
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                    )
                                    .then(
                                        if (!assigned) {
                                            Modifier.pointerInput(player.id, assigned) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { localOffset ->
                                                        showMenu = false
                                                        draggingPlayer = player
                                                        dragPosition = cellPosition + localOffset
                                                        dragOffset = Offset.Zero
                                                    },
                                                    onDrag = { change, amount ->
                                                        change.consume()
                                                        dragOffset += amount
                                                    },
                                                    onDragEnd = {
                                                        val target = findDropTarget(dragPosition + dragOffset)
                                                        if (target != null) {
                                                            assignToTeam(player, target)
                                                        }
                                                        draggingPlayer = null
                                                        dragOffset = Offset.Zero
                                                    },
                                                    onDragCancel = {
                                                        draggingPlayer = null
                                                        dragOffset = Offset.Zero
                                                    }
                                                )
                                            }
                                        } else Modifier
                                    )
                                    .then(
                                        if (!assigned && !isDragging) {
                                            Modifier.clickable { showMenu = true }
                                        } else Modifier
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (assigned) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = player.name.first().uppercase(),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.outline,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = player.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            maxLines = 1
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (player.photoPath != null) {
                                                AsyncImage(
                                                    model = player.photoPath,
                                                    contentDescription = player.name,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Text(
                                                    text = player.name.first().uppercase(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = player.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = showMenu && !assigned,
                                onDismissRequest = { showMenu = false }
                            ) {
                                teamNames.forEachIndexed { index, name ->
                                    val isFull = teamPlayers[index].size >= maxPlayersPerTeam
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    name,
                                                    color = if (isFull) MaterialTheme.colorScheme.outline
                                                    else MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    if (isFull) "full"
                                                    else "${teamPlayers[index].size}/$maxPlayersPerTeam",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isFull) MaterialTheme.colorScheme.error
                                                    else MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        },
                                        onClick = {
                                            if (!isFull) {
                                                assignToTeam(player, index)
                                                showMenu = false
                                            }
                                        },
                                        enabled = !isFull
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Teams (${teamNames.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    TextButton(
                        onClick = {
                            val letters = listOf("A", "B", "C", "D", "E", "F")
                            val nextLetter = letters.firstOrNull { letter ->
                                teamNames.none { it.endsWith(" $letter") }
                            } ?: letters.last()
                            teamNames.add("Team $nextLetter")
                            teamPlayers.add(mutableStateListOf())
                        },
                        enabled = teamNames.size < 6
                    ) {
                        Text("+Add team")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val rows = (teamNames.size + 1) / 2
                    items(rows) { rowIndex ->
                        val firstIndex = rowIndex * 2
                        val secondIndex = firstIndex + 1
                        val hasSecond = secondIndex < teamNames.size

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SimpleTeamCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .onGloballyPositioned { coords ->
                                        teamBounds[firstIndex] = coords.boundsInWindow()
                                    },
                                teamName = teamNames[firstIndex],
                                players = teamPlayers[firstIndex].toList(),
                                maxPlayers = maxPlayersPerTeam,
                                isDragTarget = draggingPlayer != null &&
                                        findDropTarget(dragPosition + dragOffset) == firstIndex,
                                onRemovePlayer = { player ->
                                    teamPlayers[firstIndex].removeAll { it.id == player.id }
                                },
                                onRename = { newName -> teamNames[firstIndex] = newName },
                                onDelete = if (firstIndex > 1) {{
                                    teamPlayers[firstIndex].clear()
                                    teamPlayers.removeAt(firstIndex)
                                    teamNames.removeAt(firstIndex)
                                }} else null
                            )

                            if (hasSecond) {
                                SimpleTeamCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .onGloballyPositioned { coords ->
                                            teamBounds[secondIndex] = coords.boundsInWindow()
                                        },
                                    teamName = teamNames[secondIndex],
                                    players = teamPlayers[secondIndex].toList(),
                                    maxPlayers = maxPlayersPerTeam,
                                    isDragTarget = draggingPlayer != null &&
                                            findDropTarget(dragPosition + dragOffset) == secondIndex,
                                    onRemovePlayer = { player ->
                                        teamPlayers[secondIndex].removeAll { it.id == player.id }
                                    },
                                    onRename = { newName -> teamNames[secondIndex] = newName },
                                    onDelete = if (secondIndex > 1) {{
                                        teamPlayers[secondIndex].clear()
                                        teamPlayers.removeAt(secondIndex)
                                        teamNames.removeAt(secondIndex)
                                    }} else null
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        draggingPlayer?.let { player ->
            val pos = dragPosition + dragOffset
            Box(
                modifier = Modifier
                    .offset {
                        androidx.compose.ui.unit.IntOffset(
                            (pos.x - 60.dp.toPx()).toInt(),
                            (pos.y - 28.dp.toPx()).toInt()
                        )
                    }
                    .zIndex(10f)
                    .shadow(12.dp, RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (player.photoPath != null) {
                            AsyncImage(
                                model = player.photoPath,
                                contentDescription = player.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = player.name.first().uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleTeamCard(
    modifier: Modifier = Modifier,
    teamName: String,
    players: List<Player>,
    maxPlayers: Int,
    isDragTarget: Boolean,
    onRemovePlayer: (Player) -> Unit,
    onDelete: (() -> Unit)? = null,
    onRename: (String) -> Unit = {}
) {
    val isFull = players.size >= maxPlayers
    var isEditing by remember { mutableStateOf(false) }
    var nameValue by remember(teamName) { mutableStateOf(teamName) }
    val focusRequester = remember { FocusRequester() }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragTarget && !isFull)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragTarget) 6.dp else 2.dp
        ),
        border = if (isDragTarget && !isFull)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    BasicTextField(
                        value = nameValue,
                        onValueChange = { nameValue = it },
                        textStyle = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        keyboardActions = KeyboardActions(onDone = {
                            onRename(nameValue)
                            isEditing = false
                        }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                } else {
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isEditing = true }
                    )
                }
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Delete team",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = "${players.size}/$maxPlayers",
                style = MaterialTheme.typography.labelSmall,
                color = if (isFull) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            LinearProgressIndicator(
                progress = { players.size.toFloat() / maxPlayers },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isFull) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (players.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Drag here",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                players.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (player.photoPath != null) {
                                AsyncImage(
                                    model = player.photoPath,
                                    contentDescription = player.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = player.name.first().uppercase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            softWrap = true
                        )
                        IconButton(
                            onClick = { onRemovePlayer(player) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}