package com.app.teamsplitter.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.teamsplitter.data.model.GameSession
import com.app.teamsplitter.data.model.Match
import com.app.teamsplitter.data.model.SessionTeam
import com.app.teamsplitter.viewmodel.HistoryViewModel

// Діалог додавання нового матчу до ігрової сесії
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
    var isShortGame by remember { mutableStateOf(false) }

    val homeInt = homeScore.toIntOrNull()
    val awayInt = awayScore.toIntOrNull()
    val scoreError = if (hasScore && homeInt != null && awayInt != null)
        volleyballScoreError(homeInt, awayInt, isShortGame) else null
    val scoreValid = !hasScore || (homeInt != null && awayInt != null && scoreError == null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add match") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Home team", style = MaterialTheme.typography.labelMedium)
                TeamSelector(teams = teams, selectedIndex = homeTeamIndex, onSelect = { homeTeamIndex = it })

                Text("Away team", style = MaterialTheme.typography.labelMedium)
                TeamSelector(teams = teams, selectedIndex = awayTeamIndex, onSelect = { awayTeamIndex = it })

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(checked = hasScore, onCheckedChange = { hasScore = it })
                    Text("Add score")
                }

                if (hasScore) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(checked = isShortGame, onCheckedChange = { isShortGame = it })
                        Text("Short game (up to 15)")
                    }
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
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Діалог редагування рахунку існуючого матчу
@Composable
fun EditMatchDialog(
    match: Match,
    teams: List<SessionTeam>,
    viewModel: HistoryViewModel,
    onDismiss: () -> Unit
) {
    var homeScore by remember { mutableStateOf(match.homeScore.toString()) }
    var awayScore by remember { mutableStateOf(match.awayScore.toString()) }
    var isShortGame by remember { mutableStateOf(maxOf(match.homeScore, match.awayScore) in 1..24) }

    val homeTeam = teams.find { it.id == match.homeTeamId }
    val awayTeam = teams.find { it.id == match.awayTeamId }

    val homeInt = homeScore.toIntOrNull()
    val awayInt = awayScore.toIntOrNull()
    val scoreError = if (homeInt != null && awayInt != null)
        volleyballScoreError(homeInt, awayInt, isShortGame) else null
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(checked = isShortGame, onCheckedChange = { isShortGame = it })
                    Text("Short game (up to 15)")
                }
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
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Валідація рахунку: підтримує звичайну гру (до 25) та коротку (до 15)
fun volleyballScoreError(home: Int, away: Int, shortGame: Boolean = false): String? {
    val target = if (shortGame) 15 else 25
    val deuce = target - 1
    if (home == away) return "Score cannot be equal"
    val maxScore = maxOf(home, away)
    val minScore = minOf(home, away)
    val diff = maxScore - minScore
    if (maxScore < target) return "Winner must reach at least $target"
    if (diff < 2) return "Winner must lead by at least 2"
    if (maxScore == target && minScore >= deuce) return "At $deuce:$deuce play continues until +2 lead"
    if (maxScore > target && minScore < deuce) return "Invalid score"
    if (maxScore > target && diff != 2) return "Must win by exactly 2 after $deuce:$deuce"
    return null
}