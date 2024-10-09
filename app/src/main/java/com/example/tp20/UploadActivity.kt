package com.example.tp20

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UploadActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var CargarButton: Button
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        CargarButton = findViewById(R.id.CargarButton)

        CargarButton.setOnClickListener {
            uploadPost()
            goToHome()
        }
    }


    private fun uploadPost() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show()
            return
        }

        val post = hashMapOf(
            "title" to title,
            "description" to description
        )
        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post subido con éxito", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Log.e("UploadActivity", "Error al subir el post", e)
                Toast.makeText(this, "Error al subir el post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()  // Cierra la actividad actual para que el usuario no pueda volver a la pantalla de login presionando 'Atrás'
    }
}

