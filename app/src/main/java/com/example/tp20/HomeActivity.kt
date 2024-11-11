package com.example.tp20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadButton: Button
    private lateinit var CerrarSesion: Button
    private val db = FirebaseFirestore.getInstance()
    private val postList = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var AdView_Home: AdView
    private var isFetchingPosts = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)
        CerrarSesion = findViewById(R.id.CerrarSesion)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        AdView_Home = findViewById(R.id.adView_Home)
        var Registro_banner_home = "Banner_Home"

        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        IniciarBanner()

        // Configurar el botón para ir a UploadActivity
        uploadButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        CerrarSesion.setOnClickListener {
            CerrarApp()
        }
    }

    //Inicializa la Publicidad
    private fun IniciarBanner(){

            // Initialize the Google Mobile Ads SDK on a background thread.
        MobileAds.initialize(this@HomeActivity) {}


        AdView_Home = findViewById(R.id.adView_Home)
        val adRequest = AdRequest.Builder().build()
        AdView_Home.loadAd(adRequest)

        AdView_Home.adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdClicked()
                Toast.makeText(this@HomeActivity, "Banner Clicked", Toast.LENGTH_SHORT).show()
            }
            override fun onAdClosed() {
                super.onAdClosed()
                Toast.makeText(this@HomeActivity, "El usuario regresa a la aplicación", Toast.LENGTH_SHORT).show()
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Toast.makeText(this@HomeActivity, "Banner Failed to Load", Toast.LENGTH_SHORT).show()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Toast.makeText(this@HomeActivity, "Se registro un anuncio", Toast.LENGTH_SHORT).show()
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                Toast.makeText(this@HomeActivity, "Banner Loaded", Toast.LENGTH_SHORT).show()
            }
            override fun onAdOpened() {
                super.onAdOpened()
                Toast.makeText(this@HomeActivity, "El usuario abrio el anuncio", Toast.LENGTH_SHORT).show()
            }

        }
    }




    private fun CerrarApp() {
        auth.signOut()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Método para recargar los posts cada vez que vuelvas a la actividad
    override fun onResume() {
        super.onResume()
        if (!isFetchingPosts) {
            fetchPosts()}
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
