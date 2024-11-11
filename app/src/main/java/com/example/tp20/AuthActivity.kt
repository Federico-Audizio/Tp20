package com.example.tp20

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()
        VerificarUsuarioLogueado()

        // Setup
        setup()
    }

    private fun setup() {
        title = "Autenticación"

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.singUpButton)
        val googleButton = findViewById<Button>(R.id.googleButton)

        // Registrar usuario
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Usuario registrado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToHome()
                        } else {
                            Toast.makeText(
                                this,
                                "Error en el registro: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, rellena todos los campos",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        // Iniciar sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Inicio de sesión exitoso",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            goToHome()
                        } else {
                            Toast.makeText(
                                this,
                                "Error en el inicio de sesión: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, rellena todos los campos",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        // Iniciar sesión con Google
        /*googleButton.setOnClickListener {
                    val googleConf =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                    val googleClient = GoogleSignIn.getClient(this, googleConf)
                    googleClient.signOut()
                    startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
                }*/
    }

    // Método para redirigir a HomeActivity
    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Opcional: cierra la actividad actual para que el usuario no pueda volver a la pantalla de login presionando 'Atrás'
    }

    private fun VerificarUsuarioLogueado() {
        if (auth.currentUser != null) {
            // El usuario ya está logueado, redirigir a HomeActivity
            goToHome()
        } else {
            // El usuario no está logueado, continuar con el proceso de autenticación
        }
    }
}

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        }
    }*/

