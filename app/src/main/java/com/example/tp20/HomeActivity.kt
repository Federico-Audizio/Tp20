package com.example.tp20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val postList = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        // Configurar el botón para ir a UploadActivity
        uploadButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    // Método para recargar los posts cada vez que vuelvas a la actividad
    override fun onResume() {
        super.onResume()
        fetchPosts() // Llamar para recargar la lista
    }

    private fun fetchPosts() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                postList.clear() // Limpiar lista antes de agregar nuevos datos
                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val address = document.getString("address") ?: "Ubicación no disponible"

                    // Agrega el post con título, descripción y dirección
                    postList.add(Post(title, description, address))
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
