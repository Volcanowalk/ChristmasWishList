package com.example.christmaswishlist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

/*
    The adapter class for the Recycler View
 */

class RecyclerViewAdapter(private val information: List<ChristmasGift>)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        //Define the view holder

        val txtName: TextView = itemView.findViewById<TextView>(R.id.txtName)
        val txtDescription : TextView = itemView.findViewById<TextView>(R.id.txtDescription)
        val giftImageView : ImageView = itemView.findViewById(R.id.giftImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //A View Holder has a CardView layout
        //Each item is a card
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_gift, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Set the data / values to each field on the CardView layout
        holder.txtName.text = information[position].giftName
        holder.txtDescription.text = information[position].giftDescription

        val imgFile : File = File(information[position].giftImageUri!!)

        if (imgFile.exists()) {

            val imgBitmap : Bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

            holder.giftImageView.setImageBitmap(imgBitmap)

        } else {

            holder.giftImageView.setImageResource(R.drawable.no_image)

        }
    }

    override fun getItemCount(): Int {
        return information.size
    }


}