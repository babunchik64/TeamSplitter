package com.app.teamsplitter.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.teamsplitter.data.model.GameSession
import com.app.teamsplitter.data.model.Match
import com.app.teamsplitter.data.model.Player
import com.app.teamsplitter.data.model.SessionPlayer
import com.app.teamsplitter.data.model.SessionTeam
import com.app.teamsplitter.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val viewModel: HistoryViewModel = viewModel()
    val sessions by viewModel.getAllSessions().observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = TopAppBarDefaults.windowInsets
            )
        }
    ) { innerPadding ->
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No sessions yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Save a session to see it here",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    SessionCard(
                        session = session,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    session: GameSession,
    viewModel: HistoryViewModel
) {
    val teams by viewModel.getTeamsBySession(session.id).observeAsState(initial = emptyList())
    val matches by viewModel.getMatchesBySession(session.id).observeAsState(initial = emptyList())
    val players by viewModel.getPlayersBySession(session.id).observeAsState(initial = emptyList())
    val sessionPlayers by viewModel.getSessionPlayersBySession(session.id).observeAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddMatchDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(session.date))

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete session?") },
            text = { Text("Session from $date will be deleted permanently.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSession(session)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddMatchDialog) {
        AddMatchDialog(
            session = session,
            teams = teams,
            viewModel = viewModel,
            onDismiss = { showAddMatchDialog = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок карточки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoChip("👥 ${players.size} players")
                        InfoChip("🏆 ${teams.size} teams")
                        InfoChip("⚽ ${matches.size} matches")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Icon(
                        if (expanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Развёрнутый контент
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Команды
                if (teams.isNotEmpty()) {
                    Text(
                        "Teams",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    teams.chunked(2).forEach { rowTeams ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTeams.forEach { team ->
                                Box(modifier = Modifier.weight(1f)) {
                                    val wins = matches.count { m ->
                                        m.status == Match.Status.FINISHED &&
                                                (m.homeTeamId == team.id && m.homeScore > m.awayScore ||
                                                        m.awayTeamId == team.id && m.awayScore > m.homeScore)
                                    }
                                    TeamRow(team = team, players = players, sessionPlayers = sessionPlayers, wins = wins)
                                }
                            }
                            // Якщо непарна кількість команд — заповнюємо пустим місцем
                            if (rowTeams.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Матчи
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Matches",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    TextButton(onClick = { showAddMatchDialog = true }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add match")
                    }
                }

                if (matches.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No matches yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    matches.forEach { match ->
                        MatchRow(
                            match = match,
                            teams = teams,
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TeamRow(
    team: SessionTeam,
    players: List<Player>,
    sessionPlayers: List<SessionPlayer>,
    wins: Int = 0
) {
    var expanded by remember { mutableStateOf(false) }

    val teamPlayers = sessionPlayers
        .filter { it.teamId == team.id }
        .mapNotNull { sp -> players.find { it.id == sp.playerId } }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = team.teamName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(${teamPlayers.size})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (wins > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${wins}W",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Icon(
                if (expanded) Icons.Filled.KeyboardArrowUp
                else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
        }

        if (expanded && teamPlayers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            teamPlayers.forEach { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (player.photoPath != null) {
                            AsyncImage(
                                model = player.photoPath,
                                contentDescription = player.getName(),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = player.getName().firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = player.getName(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun MatchRow(
    match: Match,
    teams: List<SessionTeam>,
    viewModel: HistoryViewModel
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val homeTeam = teams.find { it.id == match.homeTeamId }
    val awayTeam = teams.find { it.id == match.awayTeamId }

    if (showEditDialog) {
        EditMatchDialog(
            match = match,
            teams = teams,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete match?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMatch(match)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Домашняя команда
            Text(
                text = homeTeam?.teamName ?: "Team ${match.homeTeamId}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End  // ← додати
            )

            // Счёт
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (match.status == Match.Status.FINISHED)
                        "${match.homeScore} : ${match.awayScore}"
                    else "vs",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Гостевая команда
            Text(
                text = awayTeam?.teamName ?: "Team ${match.awayTeamId}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start  // ← додати
            )

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete match",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddMatchDialog(
    session: GameSession,
    teams: List<SessionTeam>,
    viewModel: HistoryViewModel,
    onDismiss: () -> Unit
) {
    var homeTeamIndex by remember { mutableStateOf(0) }
    var awayTeamIndex by remember { mutableStateOf(if (teams.size > 1) 1 else 0) }
    var homeScore by remember { mutableStateOf("") }
    var awayScore by remember { mutableStateOf("") }
    var hasScore by remember { mutableStateOf(false) }

    val homeInt = homeScore.toIntOrNull()
    val awayInt = awayScore.toIntOrNull()
    val scoreError = if (hasScore && homeInt != null && awayInt != null)
        volleyballScoreError(homeInt, awayInt) else null
    val scoreValid = !hasScore || (homeInt != null && awayInt != null && scoreError == null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add match") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Home team", style = MaterialTheme.typography.labelMedium)
                TeamSelector(
                    teams = teams,
                    selectedIndex = homeTeamIndex,
                    onSelect = { homeTeamIndex = it }
                )

                Text("Away team", style = MaterialTheme.typography.labelMedium)
                TeamSelector(
                    teams = teams,
                    selectedIndex = awayTeamIndex,
                    onSelect = { awayTeamIndex = it }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = hasScore,
                        onCheckedChange = { hasScore = it }
                    )
                    Text("Add score")
                }

                if (hasScore) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = homeScore,
                            onValueChange = { homeScore = it.filter { c -> c.isDigit() } },
                            label = { Text("Home") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = scoreError != null
                        )
                        Text(":", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = awayScore,
                            onValueChange = { awayScore = it.filter { c -> c.isDigit() } },
                            label = { Text("Away") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = scoreError != null
                        )
                    }
                    if (scoreError != null) {
                        Text(
                            text = scoreError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (homeTeamIndex != awayTeamIndex) {
                        val homeTeam = teams[homeTeamIndex]
                        val awayTeam = teams[awayTeamIndex]
                        val match = Match(session.id, homeTeam.id, awayTeam.id)
                        if (hasScore && homeInt != null && awayInt != null) {
                            match.setHomeScore(homeInt)
                            match.setAwayScore(awayInt)
                            match.setStatus(Match.Status.FINISHED)
                        }
                        viewModel.addMatch(match, null)
                        onDismiss()
                    }
                },
                enabled = homeTeamIndex != awayTeamIndex && scoreValid
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditMatchDialog(
    match: Match,
    teams: List<SessionTeam>,
    viewModel: HistoryViewModel,
    onDismiss: () -> Unit
) {
    var homeScore by remember { mutableStateOf(match.homeScore.toString()) }
    var awayScore by remember { mutableStateOf(match.awayScore.toString()) }

    val homeTeam = teams.find { it.id == match.homeTeamId }
    val awayTeam = teams.find { it.id == match.awayTeamId }

    val homeInt = homeScore.toIntOrNull()
    val awayInt = awayScore.toIntOrNull()
    val scoreError = if (homeInt != null && awayInt != null)
        volleyballScoreError(homeInt, awayInt) else null
    val scoreValid = homeInt != null && awayInt != null && scoreError == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit match") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "${homeTeam?.teamName ?: "Team ${match.homeTeamId}"} vs ${awayTeam?.teamName ?: "Team ${match.awayTeamId}"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text("Score", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = homeScore,
                        onValueChange = { homeScore = it.filter { c -> c.isDigit() } },
                        label = { Text("Home") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = scoreError != null
                    )
                    Text(":", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = awayScore,
                        onValueChange = { awayScore = it.filter { c -> c.isDigit() } },
                        label = { Text("Away") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = scoreError != null
                    )
                }
                if (scoreError != null) {
                    Text(
                        text = scoreError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    match.setHomeScore(homeInt ?: 0)
                    match.setAwayScore(awayInt ?: 0)
                    match.setStatus(Match.Status.FINISHED)
                    viewModel.updateMatch(match)
                    onDismiss()
                },
                enabled = scoreValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun isValidVolleyballScore(home: Int, away: Int): Boolean {
    val maxScore = maxOf(home, away)
    val minScore = minOf(home, away)
    val diff = maxScore - minScore

    if (diff < 2) return false
    if (maxScore < 25) return false
    if (maxScore == 25) return minScore <= 23
    return minScore >= 24 && diff == 2
}

fun volleyballScoreError(home: Int, away: Int): String? {
    if (home == away) return "Score cannot be equal"
    val maxScore = maxOf(home, away)
    val minScore = minOf(home, away)
    val diff = maxScore - minScore
    if (maxScore < 25) return "Winner must reach at least 25"
    if (diff < 2) return "Winner must lead by at least 2"
    if (maxScore == 25 && minScore > 23) return "At 24:24 play continues until +2 lead"
    if (maxScore > 25 && minScore < 24) return "Invalid score"
    if (maxScore > 25 && diff != 2) return "Must win by exactly 2 after 24:24"
    return null
}

@Composable
fun TeamSelector(
    teams: List<SessionTeam>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        teams.forEachIndexed { index, team ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = { Text(team.teamName) }
            )
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}