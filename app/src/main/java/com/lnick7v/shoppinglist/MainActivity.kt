package com.lnick7v.shoppinglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var buttonAddBasket: FloatingActionButton
    private lateinit var recyclerViewBasket: RecyclerView
    private lateinit var basketsAdapter: BasketsAdapter
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initViews()
        basketsAdapter = BasketsAdapter()

        basketsAdapter.setOnBasketClickListener(object: BasketsAdapter.OnBasketClickListener {
            override fun onBasketClick(basket: Basket) {
                val intent = EditBasketActivity.newEditIntent(this@MainActivity, basket.id)
                startActivity(intent)
            }
        } )

        recyclerViewBasket.adapter = basketsAdapter

        mainViewModel.getBaskets().observe(this) { baskets ->
            basketsAdapter.setBaskets(baskets)
        }





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
            startActivity(EditBasketActivity.newCreateIntent(this))
        }
    }



    private fun initViews() {
        buttonAddBasket = findViewById(R.id.buttonAddBasket)
        recyclerViewBasket = findViewById(R.id.recyclerViewBasket)

    }
}