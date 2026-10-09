package com.example.miprimeraaplicacion2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)

        // 1. BOTÓN INGRESAR (Firebase)
        btnIngresar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val clave = etPassword.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "¡Bienvenido/a!", Toast.LENGTH_SHORT).show()
                        irALecturas()
                    } else {
                        Toast.makeText(this, "Error al ingresar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 2. BOTÓN REGISTRARSE (Firebase)
        btnRegistrarse.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val clave = etPassword.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Ingresa un correo y clave para registrar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (clave.length < 6) {
                Toast.makeText(this, "La clave debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                        irALecturas()
                    } else {
                        Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 3. BOTÓN LIMPIAR
        btnLimpiar.setOnClickListener {
            etCorreo.setText("")
            etPassword.setText("")
        }

        // 4. BOTÓN CONTINUAR CON GOOGLE (Paso directo para demostración)
        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Iniciando sesión con Google...", Toast.LENGTH_SHORT).show()
            irALecturas()
        }
    }

    private fun irALecturas() {
        val intent = Intent(this, LecturasActivity::class.java)
        startActivity(intent)
        finish()
    }
}