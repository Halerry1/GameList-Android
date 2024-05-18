package fr.epita.sebastian.hellogames

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WSinterface {

    @GET("list")
    fun getAllGamesFromServer(): Call<List<GameItem>>

    @GET("details{gameid}")
    fun getGameInfoForGameId(@Path("gameid") gameId:Int):Call<GameDetails>

}