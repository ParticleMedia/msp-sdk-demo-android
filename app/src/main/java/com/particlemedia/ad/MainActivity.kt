package com.particlemedia.ad
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonActivity1 = findViewById<Button>(R.id.button_activity1)
        buttonActivity1.setOnClickListener {
            val intent = Intent(this, NativeBannerMultiFormatActivity::class.java)
            startActivity(intent)
        }

        val buttonActivity2 = findViewById<Button>(R.id.button_activity2)
        buttonActivity2.setOnClickListener {
            val intent = Intent(this, BannerActivity::class.java)
            startActivity(intent)
        }

        val buttonActivity3 = findViewById<Button>(R.id.button_activity3)
        buttonActivity3.setOnClickListener {
            val intent = Intent(this, InterstitialFormatActivity::class.java)
            startActivity(intent)
        }
    }
}
