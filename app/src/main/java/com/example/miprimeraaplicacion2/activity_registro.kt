package com.example.miprimeraaplicacion2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etNombre = findViewById<EditText>(R.id.etNombreRegistro)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etPassword = findViewById<EditText>(R.id.etPasswordRegistro)
        val etConfirmarPassword = findViewById<EditText>(R.id.etConfirmarPasswordRegistro)
        val btnRegistrarUsuario = findViewById<Button>(R.id.btnRegistrarUsuario)
        val btnVolverLogin = findViewById<Button>(R.id.btnVolverLogin)

        btnVolverLogin.setOnClickListener {
            finish()
        }

        btnRegistrarUsuario.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val clave = etPassword.text.toString().trim()
            val confirmarClave = etConfirmarPassword.text.toString().trim()

            var esValido = true

            if (nombre.isEmpty()) {
                etNombre.error = "Ingresa tu nombre"
                esValido = false
            }

            if (!correo.contains("@") || !correo.contains(".")) {
                etCorreo.error = "Ingresa un email válido (ej: usuario@correo.com)"
                esValido = false
            }

            if (clave.length < 6) {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                esValido = false
            }

            if (clave != confirmarClave) {
                etConfirmarPassword.error = "Las contraseñas no coinciden"
                esValido = false
            }

            if (!esValido) return@setOnClickListener

            // Crear usuario en Firebase Auth
            auth.createUserWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        val usuarioMap = hashMapOf(
                            "nombre" to nombre,
                            "correo" to correo
                        )

                        // Guardar datos adicionales en Firestore
                        db.collection("usuarios").document(uid)
                            .set(usuarioMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LecturasActivity::class.java)
                                intent.putExtra("EMAIL", correo)
                                startActivity(intent)
                                finishAffinity()
                            }
                    } else {
                        Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}