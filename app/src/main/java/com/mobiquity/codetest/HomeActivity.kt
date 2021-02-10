package com.mobiquity.codetest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobiquity.codetest.database.AppDatabase
import com.mobiquity.codetest.database.CitiesDao
import com.mobiquity.codetest.database.CitiesInfo
import com.ziraff.hrcursor.ui.Attendance.CitiesListAdapter
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity() {

    private var citiesDao: CitiesDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        var layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        listCities.setLayoutManager(layoutManager)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "CitiesInfo"
        ).build()
        citiesDao = db.citiesDao()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            startActivity(Intent(this, MapsActivityCurrentPlace::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        Executors.newSingleThreadExecutor().execute(Runnable {
            val cities: List<CitiesInfo>? = citiesDao?.getAll()
            Handler(Looper.getMainLooper()).post(Runnable {
                listCities.adapter = CitiesListAdapter(this, cities!!)

            })
        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}