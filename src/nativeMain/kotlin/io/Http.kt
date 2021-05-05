package io

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*


actual fun buildHttpClient(): HttpClient =
    HttpClient(Curl) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    } // TODO : find out why ktor-client leaks memory
        .also { Platform.isMemoryLeakCheckerActive = false }
