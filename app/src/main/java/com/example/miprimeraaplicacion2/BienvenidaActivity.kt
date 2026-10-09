package com.example.miprimeraaplicacion2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        auth = FirebaseAuth.getInstance()

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)

        // 1. BOTÓN INGRESAR (Inicia sesión en Firebase Auth)
        btnIngresar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val clave = etPassword.text.toString().trim()

            var esValido = true

            if (!correo.contains("@") || !correo.contains(".")) {
                etCorreo.error = "Ingresa un email válido con @"
                esValido = false
            }

            if (clave.isEmpty()) {
                etPassword.error = "Ingresa tu contraseña"
                esValido = false
            }

            if (!esValido) return@setOnClickListener

            auth.signInWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "¡Bienvenido/a!", Toast.LENGTH_SHORT).show()
                        irALecturas(correo)
                    } else {
                        Toast.makeText(this, "Error al ingresar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 2. BOTÓN REGISTRARSE (Abre el formulario en RegistroActivity)
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        // 3. BOTÓN LIMPIAR
        btnLimpiar.setOnClickListener {
            etCorreo.setText("")
            etPassword.setText("")
            etCorreo.error = null
            etPassword.error = null
        }

        // 4. BOTÓN CONTINUAR CON GOOGLE
        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Iniciando sesión con Google...", Toast.LENGTH_SHORT).show()
            irALecturas("usuario@google.com")
        }
    }

    // 5. RECUPERAR CONTRASEÑA (android:onClick en XML)
    fun olvideClaveClick(view: View) {
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val correo = etCorreo.text.toString().trim()

        if (correo.isEmpty()) {
            Toast.makeText(this, "Escribe tu correo en la casilla arriba primero", Toast.LENGTH_LONG).show()
        } else {
            auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Correo de recuperación enviado a $correo", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun irALecturas(correo: String) {
        val intent = Intent(this, LecturasActivity::class.java)
        intent.putExtra("EMAIL", correo)
        startActivity(intent)
        finish()
    }
}