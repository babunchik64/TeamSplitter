package com.app.teamsplitter.ui

object NavRoutes {
    const val PLAYERS = "players"
    const val PLAYER_EDIT = "player_edit/{playerId}"
    const val PLAYER_ADD = "player_add"
    const val SESSION = "session"
    const val MATCHES = "matches/{sessionId}"
    const val HISTORY = "history"

    // Хелперы для навигации с параметрами
    fun playerEdit(playerId: Int) = "player_edit/$playerId"
    fun matches(sessionId: Int) = "matches/$sessionId"
}