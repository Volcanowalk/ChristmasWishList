package com.example.christmaswishlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
    Heon Lee
    991280638

    Reference

    Room database tutorial
    https://developer.android.com/training/data-storage/room

    Coroutines tutorial
    https://developer.android.com/kotlin/coroutines
 */
class MainActivity : AppCompatActivity() {

    //Lateinit variables
    private lateinit var data : List<ChristmasGift>
    private lateinit var db : ChristmasGiftDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val btnAdd = findViewById<Button>(R.id.btnAdd)

        data = ArrayList()

        //Initialize the Data Access Object of the database
        db = Room.databaseBuilder(
                applicationContext, GiftAppDatabase::class.java, "database-gift"
        ).build().giftDAO()

        //All queries accessing the Room database must not run in the main UI thread
        // Execute on the background thread
        runBlocking {
            withContext(Dispatchers.IO) {
                data = db.getAll()
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, GiftCamera::class.java)
            startActivity(intent)
        }

        //Attach the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = RecyclerViewAdapter(data)

    }
}