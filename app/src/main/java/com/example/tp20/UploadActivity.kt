package com.example.tp20

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class UploadActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var cargarButton: Button
    private lateinit var mapWebView: WebView
    private var selectedAddress: String? = null // Cambiar a dirección en lugar de coordenadas
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        cargarButton = findViewById(R.id.CargarButton)
        mapWebView = findViewById(R.id.mapWebView)

        val webSettings: WebSettings = mapWebView.settings
        webSettings.javaScriptEnabled = true
        mapWebView.webViewClient = WebViewClient()
        mapWebView.loadDataWithBaseURL(null, loadLeafletMap(), "text/html", "UTF-8", null)

        mapWebView.addJavascriptInterface(object {
            @JavascriptInterface
            fun onLocationSelected(address: String) {
                selectedAddress = address
                Toast.makeText(this@UploadActivity, "Ubicación seleccionada: $address", Toast.LENGTH_SHORT).show()
            }
        }, "Android")

        cargarButton.setOnClickListener {
            uploadPost()
        }
    }

    private fun uploadPost() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || selectedAddress == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona una ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        val post = hashMapOf(
            "title" to title,
            "description" to description,
            "address" to selectedAddress // Guardar la dirección en lugar de coordenadas
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post subido con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun savePostWithLocation(address: String) {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || selectedAddress.isNullOrEmpty()) {
            Toast.makeText(this, "Completa todos los campos y selecciona una ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        val post = hashMapOf(
            "title" to title,
            "description" to description,
            "address" to selectedAddress // Solo guardamos la dirección
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post subido con éxito", Toast.LENGTH_SHORT).show()
                finish()  // Cierra la actividad
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadLeafletMap(): String {
        return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
        <style>
            #map { width: 100%; height: 100%; margin: 0; padding: 0; }
            html, body { height: 100%; margin: 0; }
        </style>
    </head>
    <body>
        <div id="map"></div>
        <script>
            var map = L.map('map').setView([37.7749, -122.4194], 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);

            var selectedAddress = "";  // Variable temporal para almacenar la dirección

            map.on('click', function(e) {
                var lat = e.latlng.lat;
                var lng = e.latlng.lng;
                var url = "https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lng + "&format=json&addressdetails=1";
                
                fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        selectedAddress = data.display_name || "Dirección no encontrada";
                        Android.onLocationSelected(selectedAddress); // Enviar la dirección a Android cuando esté lista
                    })
                    .catch(error => console.error("Error al obtener dirección:", error));
            });
        </script>
    </body>
    </html>
    """
    }
}




 

