package fr.epita.sebastian.hellogames

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var gameListAdapter: GameListAdapter // Declare adapter variable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.games_info_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // List to store objects
        val gameList = arrayListOf<GameItem>()

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

        // The callback object to handle future response from the web server
        val wsCallback: Callback<List<GameItem>> = object : Callback<List<GameItem>> {
            override fun onResponse(call: Call<List<GameItem>>, response: Response<List<GameItem>>) {
                if (response.isSuccessful) {


                    // Check if body is not null in order to use it
                    response.body()?.let {
                        gameList.addAll(it)
                        // Update adapter with new data
                        gameListAdapter.updateGames(gameList)
                    }
                }
            }

            override fun onFailure(call: Call<List<GameItem>>, t: Throwable) {
                Log.d("TAG", "An error occurred with the WS" + t.message)
            }
        }

        // Finally, use the service to enqueue the callback
        // This will asynchronously call the method.
        service.getAllGamesFromServer().enqueue(wsCallback)

        // Define and bind UI element
        val gamesListView: RecyclerView = findViewById(R.id.recyclerViewGamesList)

        // Specify this is a standard vertical list
        gamesListView.layoutManager = LinearLayoutManager(this@MainActivity)

        // Optional: performance optimization when list widget size does not change
        gamesListView.setHasFixedSize(true)

        // Optional: add separators between lines
        gamesListView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))

        // Define list item onClick listener
        val onItemClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(clickedView: View?) {
                // Extract clicked position from clicked view tag
                val clickedPosition: Int = clickedView!!.tag as Int
                /*// Retrieve corresponding data item
                val item: GameItem = gameList[clickedPosition]
                // Do stuff with clicked item data
                Toast.makeText(
                    this@MainActivity
                    , "Clicked ${item.id} ${item.name}"
                    , Toast.LENGTH_SHORT
                ).show()*/
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    val gameItem = gameList[clickedPosition]
                    val intent = Intent(clickedView.context, GameInfoActivity::class.java)
                    intent.putExtra("gameId", gameItem.id)
                    clickedView.context.startActivity(intent)
                }
            }
        }

        // Initialize adapter with an empty list
        gameListAdapter = GameListAdapter(emptyList(), onItemClickListener)

        // Attach adapter to RecyclerView
        gamesListView.adapter = gameListAdapter
    }
}