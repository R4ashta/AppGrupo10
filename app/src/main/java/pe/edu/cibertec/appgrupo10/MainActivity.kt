package pe.edu.cibertec.appgrupo10

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, Pregunta2Activity::class.java)
        startActivity(intent)
        finish()
    }
}