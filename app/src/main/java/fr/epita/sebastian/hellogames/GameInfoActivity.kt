package fr.epita.sebastian.hellogames

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.games_info_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve game ID from intent extras
        val gameId = intent.getIntExtra("gameId", -1)

        // The base URL where the webservice is located
        val serverURL = "https://www.surleweb.xyz/api/game/"

        // Use GSON library to create a JSON parser
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())

        // Create a Retrofit client object targeting the provided URL.
        // Add a JSON converter (because we are expecting JSON responses)
        val retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .addConverterFactory(jsonConverter)
            .build()

        // Use the client to create a service:
        // an object implementing the interface to the WebService
        val service: WSinterface = retrofit.create(WSinterface::class.java)


        if (gameId != -1) {

            // Make network request asynchronously
            val wsCallback: Callback<GameDetails> = object : Callback<GameDetails>  {
                override fun onResponse(
                    call: Call<GameDetails>,
                    response: Response<GameDetails>
                ) {
                    if (response.isSuccessful) {
                        val gameDetails = response.body()
                        gameDetails?.let {
                            updateViews(it)
                        }
                    }
                    else {
                        // Handle unsuccessful response
                        Toast.makeText(this@GameInfoActivity, "Failed to fetch game details. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GameDetails>, t: Throwable) {
                    Log.e(TAG, "Network call failed", t)
                    Toast.makeText(this@GameInfoActivity, "Network call failed. Please check your internet connection.", Toast.LENGTH_SHORT).show()
                }

            }

            // Finally, use the service to enqueue the callback
            // This will asynchronously call the method.
            service.getGameInfoForGameId(gameId).enqueue(wsCallback)
        } else {
            // Handle invalid game ID
            Toast.makeText(this@GameInfoActivity, "Invalid game ID. Please select a valid game.", Toast.LENGTH_SHORT).show()
            // Optionally, you can finish the activity or take appropriate action
            finish()
        }
    }

    private fun updateViews(gameDetails: GameDetails) {
        // Update views with game details
        findViewById<TextView>(R.id.gamesInfoNameDynamicTextView).text = gameDetails.name
        findViewById<TextView>(R.id.gamesInfoTypeDynamicTextView).text = gameDetails.type
        findViewById<TextView>(R.id.gamesInfoYearDynamicTextView).text = gameDetails.year.toString()
        findViewById<TextView>(R.id.gamesInfoNbplayersDynamicTextView).text = gameDetails.players.toString()
        findViewById<TextView>(R.id.gameInfoActivityGameDescTextView).text = gameDetails.description_en

        // Load game image using Glide or any other image loading library
        Glide.with(this)
            .load(gameDetails.picture)
            .into(findViewById<ImageView>(R.id.gameInfoActivityGameImageView))

        // Handle button click event
        findViewById<Button>(R.id.gameInfoActivityMoreInfoButton).setOnClickListener {
            // Open game URL in a web browser
            val implicitIntent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(gameDetails.url))
            startActivity(implicitIntent)
        }
    }

}
