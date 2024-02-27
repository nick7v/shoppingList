package com.lnick7v.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BasketsAdapter: RecyclerView.Adapter<BasketsAdapter.BasketsViewHolder>() {
    private var baskets = listOf<Basket>()

    fun getBaskets(): List<Basket> {
        return ArrayList(baskets)
    }

    fun setBaskets(baskets: List<Basket>){
        this.baskets = baskets
        notifyDataSetChanged()
    }

    class BasketsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var textViewBasket = view.findViewById<TextView>(R.id.textViewBasket)
        var textViewBasketDate = view.findViewById<TextView>(R.id.textViewBasketDate)
        var cardViewBasketItem = view.findViewById<CardView>(R.id.cardViewBasketItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.basket_item, parent, false)
        return BasketsViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasketsViewHolder, position: Int) {
        val basket = baskets[position]
        holder.textViewBasket.text = basket.name
        holder.textViewBasketDate.text = basket.date

        //block of code for assigning the background color to cardView
        val colorResId = when (basket.priority) {
            0 -> android.R.color.holo_green_light
            1 -> android.R.color.holo_orange_light
            else -> android.R.color.holo_red_light
        }
        holder.cardViewBasketItem
            .setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorResId))
    }

    override fun getItemCount(): Int {
        return baskets.size
    }


}