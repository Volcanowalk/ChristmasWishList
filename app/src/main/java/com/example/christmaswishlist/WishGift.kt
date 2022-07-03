package com.example.christmaswishlist

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

class WishGift : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_gift)

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtName = findViewById<EditText>(R.id.editTextName)
        val txtDescription = findViewById<EditText>(R.id.editTextDescription)
        val imageView = findViewById<ImageView>(R.id.giftPhoto)


        var bundle: Bundle? = intent.extras

        //Fetch the saved Uri from the intent
        var image : String = bundle!!.getString("savedUri").toString()

        //File path processing
        image = image.replace("file://", "")

        //Initialize the Data Access Object
        val db = Room.databaseBuilder(
            applicationContext, GiftAppDatabase::class.java, "database-gift"
        ).build().giftDAO()

        //Initialize the File object
        val imgFile : File = File(image)

        if (imgFile.exists()) { //If the file path is valid

            val imgBitmap : Bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

            imageView.setImageBitmap(imgBitmap)

        } else { //If the file path is invalid

            imageView.setImageResource(R.drawable.no_image)

        }


        //The user clicks the submit button
        btnSubmit.setOnClickListener {

            val name = txtName.text.toString()
            val description = txtDescription.text.toString()

            //giftID 0 is the initial ID for the first gift
            //After the first gift, the database will automatically increment the ID.
            val newGift : ChristmasGift = ChristmasGift(0, name, description, image)

            val intent = Intent(this@WishGift, MainActivity::class.java)

            //Runs the process that could block the Main thread on the background IO thread
            //Coroutines
            runBlocking {
                withContext(Dispatchers.IO){
                    db.addGift(newGift)
                }
            }

            Toast.makeText(applicationContext, "New gift has been added successfully!",
                Toast.LENGTH_SHORT).show()

            startActivity(intent)

        }
    }
}