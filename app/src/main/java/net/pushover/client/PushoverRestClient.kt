package net.pushover.client

import com.makfc.notification_listener_service.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object PushoverRestClient : PushoverClient {
    private const val BASE_URL = "https://api.pushover.net/"

    private var interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private var client: OkHttpClient

    init {
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    private val service = retrofit.create(PushoverService::class.java)

    @Throws(PushoverException::class)
    override fun pushMessage(msg: PushoverMessage): MessageResponse {
        val map: MutableMap<String, String> = HashMap()
        map["token"] = msg.apiToken
        map["user"] = msg.userId
        if (!msg.htmlMessage.isNullOrBlank()) {
            map["message"] = msg.htmlMessage
        } else {
            map["message"] = msg.message
        }
        addPairIfNotNull(map, "title", msg.title)
        addPairIfNotNull(map, "url", msg.url)
        addPairIfNotNull(map, "url_title", msg.titleForURL)
        addPairIfNotNull(map, "device", msg.device)
        addPairIfNotNull(map, "timestamp", msg.timestamp)
        addPairIfNotNull(map, "sound", msg.sound)
        if (!msg.htmlMessage.isNullOrBlank()) {
            addPairIfNotNull(map, "html", "1")
        }
        if (MessagePriority.NORMAL != msg.priority) {
            addPairIfNotNull(map, "priority", msg.priority)
        }
        return try {
            service.pushMessage(map).execute().body()!!
        } catch (e: Exception) {
            throw PushoverException(e.message, e.cause)
        }
    }

    @Throws(PushoverException::class)
    override fun getSounds(): Set<PushOverSound> {
        try {
            val soundsResponse = service.getSounds().execute().body()!!
            val set = mutableSetOf<PushOverSound>()
            soundsResponse.sounds!!.forEach { entry ->
                set.add(
                    PushOverSound(
                        entry.key,
                        entry.value
                    )
                )
            }
            return set
        } catch (e: Exception) {
            throw PushoverException(e.message, e.cause)
        }
    }

    private fun addPairIfNotNull(map: MutableMap<String, String>, key: String, value: Any?) {
        if (value != null) {
            map[key] = "$value"
        }
    }
}