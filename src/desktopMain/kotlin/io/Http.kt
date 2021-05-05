@file:JvmName("HttpClientBuilder")
package io

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import okhttp3.OkHttpClient

actual fun buildHttpClient(): HttpClient {
    val okHttpClient = OkHttpClient.Builder().build()
    return HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = defaultSerializer()
        }
        engine {
            preconfigured = okHttpClient
        }
    }
}