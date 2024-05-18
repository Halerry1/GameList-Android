package fr.epita.sebastian.hellogames

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.epita.sebastian.hellogames.R

class LoginActivity : AppCompatActivity() {

    private val authorizedUsers = mapOf(
        "MOM" to 1,
        "DAD" to 2,
        "ME" to 3,
        "SISTER" to 4,
        "GUEST" to 9
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val imageViewEpitaLogo: ImageView = findViewById(R.id.imageView)
        imageViewEpitaLogo.setOnClickListener {
            openEpitaWebsite()
        }
        val editTextUsername = findViewById<EditText>(R.id.editTextText3)
        val buttonLogin = findViewById<Button>(R.id.loginbutton)
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            if (authorizedUsers.containsKey(username)) {
                val userId = authorizedUsers[username]
                // Create an Intent to start MainActivity2
                val intent = Intent(this, MainActivity::class.java)
                // Pass username and userId as extras
                intent.putExtra("username", username)
                intent.putExtra("userId", userId)
                // Start MainActivity2
                startActivity(intent)
                // For demonstration, let's just display a toast message
                Toast.makeText(this, "Login successful for $username with ID: $userId", Toast.LENGTH_SHORT).show()
            } else {
                // Display error message for unknown user
                Toast.makeText(this, "Unknown user. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun openEpitaWebsite() {
        val epitaUrl = "https://www.epita.fr"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(epitaUrl))
        startActivity(intent)
    }
}