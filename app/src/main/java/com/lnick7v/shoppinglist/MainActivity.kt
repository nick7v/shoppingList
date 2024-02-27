package com.lnick7v.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    lateinit var buttonAddBasket: FloatingActionButton
    lateinit var recyclerViewBasket: RecyclerView
    lateinit var basketsAdapter: BasketsAdapter
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initViews()
        basketsAdapter = BasketsAdapter()
        recyclerViewBasket.adapter = basketsAdapter

        mainViewModel.getBaskets().observe(this, Observer { baskets ->
            basketsAdapter.setBaskets(baskets)
        })


        mainViewModel.add(Basket("name 1", 0,"01.01.2023" ))
        mainViewModel.add(Basket("name 2", 1,"02.02.2022" ))





        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper
            .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val basket = basketsAdapter.getBaskets()[position]
                mainViewModel.remove(basket)
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerViewBasket)


        buttonAddBasket.setOnClickListener {
            startActivity(AddBasketActivity.newIntent(this))
        }
    }



    private fun initViews() {
        buttonAddBasket = findViewById(R.id.buttonAddBasket)
        recyclerViewBasket = findViewById(R.id.recyclerViewBasket)

    }
}