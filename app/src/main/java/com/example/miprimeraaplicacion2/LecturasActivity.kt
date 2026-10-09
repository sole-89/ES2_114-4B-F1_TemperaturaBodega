package com.example.miprimeraaplicacion2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LecturasActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var etNombreBodega: EditText
    private lateinit var etTemperatura: EditText
    private lateinit var btnGuardarLectura: Button
    private lateinit var txtHistorial: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lecturas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Vincular componentes de la vista
        etNombreBodega = findViewById(R.id.etNombreBodega)
        etTemperatura = findViewById(R.id.etTemperatura)
        btnGuardarLectura = findViewById(R.id.btnGuardarLectura)
        txtHistorial = findViewById(R.id.txtHistorial)

        // Escuchar clic en el botón Guardar
        btnGuardarLectura.setOnClickListener {
            guardarLectura()
        }

        // Cargar registros existentes desde Firestore
        cargarLecturas()
    }

    private fun guardarLectura() {
        val bodega = etNombreBodega.text.toString().trim()
        val tempTexto = etTemperatura.text.toString().trim()

        if (bodega.isEmpty() || tempTexto.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val tempValor = tempTexto.toDoubleOrNull()
        if (tempValor == null) {
            Toast.makeText(this, "Ingresa un número válido de temperatura", Toast.LENGTH_SHORT).show()
            return
        }

        // Evaluar alerta
        val estado = if (tempValor > 8.0) "⚠️ ALERTA (Alta Temp)" else "✅ Normal"

        // Crear mapa de datos a guardar en Firestore
        val lectura = hashMapOf(
            "bodega" to bodega,
            "temperatura" to tempValor,
            "estado" to estado,
            "fecha" to System.currentTimeMillis()
        )

        db.collection("lecturas_temperatura")
            .add(lectura)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro guardado exitosamente", Toast.LENGTH_SHORT).show()
                etNombreBodega.setText("")
                etTemperatura.setText("")
                cargarLecturas() // Recargar historial
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarLecturas() {
        db.collection("lecturas_temperatura")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    txtHistorial.text = "No hay lecturas registradas aún."
                    return@addOnSuccessListener
                }

                val builder = StringBuilder()
                for (document in result) {
                    val bodega = document.getString("bodega") ?: "Bodega"
                    val temp = document.getDouble("temperatura") ?: 0.0
                    val estado = document.getString("estado") ?: "Normal"

                    builder.append("📍 $bodega\n")
                    builder.append("   Temperatura: $temp °C\n")
                    builder.append("   Estado: $estado\n")
                    builder.append("-----------------------------------\n")
                }
                txtHistorial.text = builder.toString()
            }
            .addOnFailureListener { e ->
                txtHistorial.text = "Error al cargar datos: ${e.message}"
            }
    }
}