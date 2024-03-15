package com.lnick7v.shoppinglist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer


const val BASKET_ID = "idBasket"

class EditBasketActivity : AppCompatActivity() {
    private lateinit var editTextBasketName: EditText
    private lateinit var editTextDateOfBasket: EditText
    private lateinit var radioButtonLow: RadioButton
    private lateinit var radioButtonMedium: RadioButton
    private lateinit var radioButtonHigh: RadioButton
    private lateinit var buttonSave: Button
    private lateinit var viewModel: EditBasketViewModel
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var recyclerViewProducts: RecyclerView
    private val handler = Handler(Looper.getMainLooper())
    private val compositeDisposable = CompositeDisposable()

    private lateinit var refresh: FloatingActionButton  //!!!!!!!!!!!!!ВРЕМЕННО

    private var basketID: Long = -1 // -1 - default value for creating new basket Activity mode

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!??????????????????????????????????????????????????????
    /*TODO("1.1. КАК организовать кеш для списка ппокупок??? т.е. чтобы изменения или добавления
        продуктов и полей корзины писались не в базу напрямую а вначале в кэш и уже после нажатия
        "сохранить" попадали из кэша в базу????????????????????????????????????????????????????????????????????

        // это вопросы не к тебе, это памятка для себя
        1. ичезновение блока над RV при появлении клавиатуры и первого скрола вниз - появление
        при скрытиии клавиатуры и скрола до самого верха - сделать когда изучу фрагменты
        2. добавить поле кол-во в item RV
        3. Проработать цену
        4. Свайп вправо и свайп влево - разные режимы: факт покупки с указанием цены в диалоге и без указания цены -
        перемещение item-а в конец списка, с изменением цвета фона и проставление галочки
        5. Удаление элемента по долгому нажатию с выходом контекстного меню: удалить, отменить покупку, указаьть цену
        6. Появление блока подсказки о свайпах влево и вправо при старте активити
        7. Подумать о режимах старта активити: создание нового списка, редактирования сущ., и совершения покупки
        8. Слушатель системной кнопки назад - появление диалога сохранить список или нет -> удаление списка и продуктов из БД
        9. Запрет на поворот экрана во всех активити (сейчас еще при повороте экрана в режиме создания
        новой корзины, каждый раз создает новую корзину
        10. Не сохранять в корзину пустые продукты
        ")*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_basket)
        viewModel = ViewModelProvider(this)[EditBasketViewModel::class.java]
        initViews()
        selectActivityStartMode()  // edit existing or create new basket

        productsAdapter = ProductsAdapter()

        /* productsAdapter.setProductAddTextChangedListener(object: ProductsAdapter.ProductAddTextChangedListener{
             override fun afterTextChanged(text: Editable, position: Int) {
                 //Toast.makeText(this@EditBasketActivity, text, Toast.LENGTH_SHORT).show()//!!!!!!!!!!!!!!!!!!!!!
                 if (viewModel.getProductsSize() == position + 1) {
                     viewModel.addProduct(Product("$position", 0.0, basketID))
                 }
             }
         } )*/



        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                recyclerViewProducts.clearFocus()
                AlertDialog.Builder(this@EditBasketActivity).apply {
                    setTitle(getString(R.string.closing_list_of_products))
                    setMessage(getString(R.string.save_changes_question))
                    setPositiveButton("Да") { dialog, id ->
                        saveBasket(false)
                    }
                    setNegativeButton("Нет") { dialog, id ->
                        viewModel.removeBasketAndExit(basketID)
                        //TODO("replace removing basket to removing cache of basket")
                    }
                    setNeutralButton("Отмена") {dialog, _ ->
                        dialog.cancel()
                    }
                }.show()
            }
        })


        productsAdapter.setOnProductFocusChangeListener(object :
            ProductsAdapter.OnProductFocusChangeListener {
            override fun onProductFocusChange(product: Product, view: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    val newName = (view as EditText).text.toString().trim()
                    viewModel.updateProduct(newName, product.id)
                    //***** КОСТЫЛЬ, нужно вызывать notifyItemChanged в другом месте, после обновления БД в адаптере
                    /*Thread {
                        Thread.sleep(2000)
                        productsAdapter.notifyItemChanged(position)
                    }.start()*/
                    //*************************ВРОДЕ СТАЛ РАБОТАТЬ И БЕЗ ЭТОГО КОСТЫЛЯ
                }
            }
        })

        productsAdapter.setOnAddProductClickListener(object :
            ProductsAdapter.OnAddProductClickListener {
            override fun onAddProductClick(productsSize: Int) {
                addEmptyProductAndSetFocus(productsSize)
            }
        })


        productsAdapter.setOnProductKeyListener(object : ProductsAdapter.OnProductKeyListener {
            override fun onProductKeyListener(
                productsSize: Int,
                keyCode: Int,
                event: KeyEvent,
                position: Int
            ): Boolean {
                return if (keyCode == KeyEvent.KEYCODE_ENTER && position == productsSize - 1) {
                    addEmptyProductAndSetFocus(productsSize)
                    true
                } else false
            }
        })



        recyclerViewProducts.adapter = productsAdapter
        //перенес в метод, который вызывается после выбора режима старта активити (редактирования сущ.
        //или создания новой корзины, т.к. при создании новой корзины, RxJava создает новую корзину
        //после того как происходит запрос в БД если оставить код здесь
        /*viewModel.getProducts(basketID).observe(this) { products ->
            productsAdapter.setProducts(products)
        }*/

        viewModel.getCloseScreen().observe(this) { closeScreen ->
            if (closeScreen) finish()
        }

        /**
         * if user is scrolling RV through the list of products, then we hide the focus to save data
         * and to prevent displaying focus on other items when scrolling
         */
        recyclerViewProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    recyclerViewProducts.clearFocus()
                }
            }
        })

        /*KeyboardUtils.addKeyboardToggleListener(this) { isVisible ->
            Log.e("MyActivity", "keyboard visible: $isVisible")
        }*/

        /**
         * implementation of removing a product by swiping
         * TODO("see TODO code at the top of the class about left and right swiping")
         */
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper
        .SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val product = productsAdapter.getProducts()[position]
                recyclerViewProducts.clearFocus()
                viewModel.removeProduct(product)
                //***** КОСТЫЛЬ, нужно вызывать notifyItemRemoved в другом месте, после обновления БД в адаптере
                Thread {
                    Thread.sleep(500)
                    handler.post { productsAdapter.notifyItemRemoved(position) }
                    handler.post {
                        productsAdapter.notifyItemRangeChanged(
                            position,
                            productsAdapter.itemCount
                        )
                    }
                }.start()
                //*************************
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerViewProducts)



        buttonSave.setOnClickListener {
            saveBasket(true)
        }

        editTextBasketName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) productsAdapter.notifyItemRangeChanged(0, productsAdapter.itemCount)
        }

        refresh.setOnClickListener {
            productsAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        Thread { //ОЧЕРЕДНОЙ КОСТЫЛЬ - нужно чтобы при повороте экрана RV отображался
            Thread.sleep(300)
            handler.post { productsAdapter.notifyDataSetChanged() }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //в правильном ли ЖЦ активити указал отмену подписки ????????????????????????
        compositeDisposable.dispose()
    }



    private fun saveBasket(checkFields: Boolean) {
        if ((editTextBasketName.text.isEmpty() || editTextDateOfBasket.text.isEmpty()) && checkFields) {
            Toast.makeText(this, "Укажите название и дату", Toast.LENGTH_SHORT).show()
        } else {
            val name = editTextBasketName.text.toString().trim()
            val date = editTextDateOfBasket.text.toString().trim()
            viewModel.updateBasket(name, date, getPriority(), basketID)
        }
    }

    private fun addEmptyProductAndSetFocus(productsSize: Int) {
        recyclerViewProducts.clearFocus() // For saving text in focused EditText
        viewModel.addEmptyProductToEnd(basketID)
        ////////// КОСТЫЛЬ, нужно вызывать notifyItemChanged в другом месте, после обновления БД в адаптере
        val t1 = Thread {
            Thread.sleep(500)
            handler.post {
                productsAdapter.notifyItemRangeChanged(productsSize - 1, productsSize)
            }
        }
        t1.start()
        t1.join()
        ///////////////////////
        Thread {  /////////// ЕЩЕ КОСТЫЛЬ, нужно разбираться куда это все перенести
            Thread.sleep(500)
            handler.post { recyclerViewProducts.smoothScrollToPosition(productsSize) }
            Thread.sleep(500)
            handler.post { recyclerViewProducts.findViewHolderForAdapterPosition(productsSize)!!.itemView.requestFocus() }
        }.start()
    }


    private fun selectActivityStartMode() {
        basketID = intent.getLongExtra(BASKET_ID, -1)
        if (basketID != -1L) { //Edit an existing basket mode
            startActivityInEditBasketMode(basketID)
            observeBasketBD()
        } else { //Create a new basket
            startActivityInNewBasketMode()
        }
    }

   private fun startActivityInNewBasketMode() {
        val disposable = viewModel.addBasket(Basket("", getPriority(), ""))
            .subscribe({ idBasket ->
                Log.d("EditBasketActivity", "addBasket: basket with id $idBasket")
                basketID = idBasket
                observeBasketBD()
                viewModel.addEmptyProductToEnd(basketID)
            })
            {
                Log.e("EditBasketActivity", "Error: addBasket - ${it.printStackTrace()}()")
            }
        compositeDisposable.add(disposable)
    }

    private fun startActivityInEditBasketMode(idBasket: Long) {
        val disposable = viewModel.getOneBasket(idBasket)
            .subscribe(object : Consumer<Basket> {
                override fun accept(oneBasket: Basket) {
                    editTextBasketName.setText(oneBasket.name)
                    editTextDateOfBasket.setText(oneBasket.date)
                    setPriority(oneBasket.priority)
                    Log.d("EditBasketActivity", "getOneBasket: with id $idBasket")
                }
            }, object : Consumer<Throwable> {
                override fun accept(error: Throwable) {
                    Log.e(
                        "EditBasketActivity",
                        "Error: getOneBasket - ${error.printStackTrace()}()"
                    )
                }
            })
        compositeDisposable.add(disposable)
    }

    private fun observeBasketBD() {
        viewModel.getProducts(basketID).observe(this) { products ->
            productsAdapter.setProducts(products)
        }
    }


    private fun setPriority(priority: Int) {
        val listOfPriorityradioButton = listOf(
            radioButtonLow,
            radioButtonMedium,
            radioButtonHigh
        )
        listOfPriorityradioButton[priority].isChecked = true
    }

    private fun getPriority() = when {
        radioButtonLow.isChecked -> 0
        radioButtonMedium.isChecked -> 1
        else -> 2
    }

    private fun initViews() {
        editTextBasketName = findViewById(R.id.editTextBasketName)
        editTextDateOfBasket = findViewById(R.id.editTextDateOfBasket)
        radioButtonLow = findViewById(R.id.radioButtonLow)
        radioButtonMedium = findViewById(R.id.radioButtonMedium)
        radioButtonHigh = findViewById(R.id.radioButtonHigh)
        buttonSave = findViewById(R.id.buttonSave)
        recyclerViewProducts = findViewById(R.id.recyclerViewProduct)
        refresh = findViewById(R.id.refresh)  ///!!!!!! TEMPORARY
    }

    companion object {
        fun newCreateIntent(context: Context): Intent {
            return Intent(context, EditBasketActivity::class.java)
        }

        fun newEditIntent(context: Context, idBasket: Long): Intent {
            val intent = Intent(context, EditBasketActivity::class.java)
            intent.putExtra(BASKET_ID, idBasket)
            return intent
        }
    }
}