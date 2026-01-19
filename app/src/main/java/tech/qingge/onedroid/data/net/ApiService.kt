package tech.qingge.onedroid.data.net

import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Date

data class CommonResp<T>(
    val code: Int,
    val msg: String,
    val data: T?
)

class CheckUpdate{

    data class Version(
        val versionName: String,
        val versionInfo: String,
        val releaseTime: Date,
        val downloadUrl: String
    )

    data class Body(
        val appVersionName: String
    )

    data class Resp(
        val appUsable: Int,
        val newestVersion: Version?
    )
}



interface ApiService {

    @POST("checkUpdate")
    suspend fun checkUpdate(@Body body: CheckUpdate.Body): CommonResp<CheckUpdate.Resp>

}