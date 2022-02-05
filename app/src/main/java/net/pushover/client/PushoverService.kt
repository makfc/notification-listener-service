package net.pushover.client

import com.makfc.notification_listener_service.BuildConfig
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface PushoverService {
    @FormUrlEncoded
    @POST("1/messages.json")
    fun pushMessage(@FieldMap fields: Map<String, String>): Call<MessageResponse>

    @GET("1/sounds.json?token=${BuildConfig.APP_API_TOKEN}")
    fun getSounds(): Call<SoundsResponse>
}