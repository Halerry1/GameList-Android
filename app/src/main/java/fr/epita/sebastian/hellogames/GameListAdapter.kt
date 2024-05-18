package fr.epita.sebastian.hellogames

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameListAdapter(var games: List<GameItem>, val onItemClickListener: View.OnClickListener):RecyclerView.Adapter<GameListAdapter.GameViewHolder>() {
    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameNameTextView: TextView = itemView.findViewById(R.id.list_item_game_name_textView)
        val gameImageView: ImageView = itemView.findViewById(R.id.list_item_game_image)
        fun bind(game: GameItem) {
            gameNameTextView.text = game.name

            // Load game picture using Glide
            Glide.with(itemView.context)
                .load(game.picture)
                .placeholder(R.drawable.placeholder) // Placeholder image
                .error(R.drawable.error) // Error image
                .into(gameImageView)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_games, parent, false)

        //attach item click listner to the row view
        //one or multiple listners can be attached to inner views in viewholder
        itemView.setOnClickListener(onItemClickListener)

        return GameViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        //retrieve data item from  the requested position
        val game = games[position]
        holder.bind(game)
        holder.itemView.tag=position
    }
    // Update the list of games
    fun updateGames(newGames: List<GameItem>) {
        games = newGames
        notifyDataSetChanged()
    }

}