package com.app.educonnect.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.educonnect.R
import com.app.educonnect.models.ItemsViewModel
import com.bumptech.glide.Glide

class ListAdapter(private val mList: List<ItemsViewModel>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_listview, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        Glide.with(holder.itemView.context).load(ItemsViewModel.image).into(holder.imgItem)
        holder.txtName.text = ItemsViewModel.name
        holder.txtDesc.text = ItemsViewModel.desc

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imgItem: ImageView = itemView.findViewById(R.id.imgItem)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtDesc: TextView = itemView.findViewById(R.id.txtDesc)
    }
}