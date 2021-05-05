package io

import cli.CliConfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable

expect fun buildHttpClient(): HttpClient

suspend fun fetchAndPrintRandomQuote() {
    buildHttpClient().use { client ->
        val quote = client.get<GameOfThroneQuote>(CliConfig.API_URL) {

        }
        println("> ${quote.quote}")
        println("-- ${quote.character}")
    }
}

@Serializable
data class GameOfThroneQuote(
    val quote: String,
    val character: String,
)