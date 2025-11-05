package com.example.myapplicationarturocashfaster.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationarturocashfaster.R

class SliderAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivSliderImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageRes = images[position]
        // DEBUG: Verificar que las imágenes se cargan
        Log.d("SliderAdapter", "Loading image at position $position - Resource: $imageRes")
        holder.imageView.setImageResource(imageRes)
    }

    override fun getItemCount(): Int = images.size
}