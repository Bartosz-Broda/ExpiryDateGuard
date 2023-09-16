package com.bbroda.expirydateguard.ui.mvp.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.MainMenuActivity
import com.bbroda.expirydateguard.ui.adapters.ProductsRecyclerAdapter
import com.bbroda.expirydateguard.ui.classes.foodPreferencesDatabase.Preference
import com.bbroda.expirydateguard.ui.classes.productdatabase.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference


class MainMenuView(var activity: MainMenuActivity, val bus: EventBus) : NavigationView.OnNavigationItemSelectedListener {

    private val activityRef = WeakReference(activity)

    private val drawerLayout: DrawerLayout? = activity.findViewById(R.id.drawer_layout)
    private val navigationView: NavigationView? = activity.findViewById(R.id.nav_view)
    private val toolbar: Toolbar? = activity.findViewById(R.id.my_toolbar)
    private var recyclerView: RecyclerView? = activity.findViewById(R.id.my_products_recycler)
    private val fabAddProduct: FloatingActionButton? = activity.findViewById(R.id.FAB_Add_product)
    private val nestedScrollView: NestedScrollView? = activity.findViewById(R.id.nestedScrollView)
    private val ingredientsCardView: CardView? = activity.findViewById(R.id.ingredients_for_meal)
    private val ingredientsForMealTextView: TextView? = activity.findViewById(R.id.ingredients_for_meal_textview)
    private val approveProductsForMealImageButton: ImageButton? = activity.findViewById(R.id.approve_products_for_meal)


    lateinit var adapter: ProductsRecyclerAdapter

    init {
        navigationView?.bringToFront()
        activity.setSupportActionBar(toolbar)
        toolbar?.title = ""
        //ingredientsCardView?.visibility = View.GONE

        val toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        toolbar?.bringToFront()
        navigationView?.bringToFront()
        navigationView!!.setNavigationItemSelectedListener(this)


        fabAddProduct?.bringToFront()
        fabAddProduct?.setOnClickListener {
            Log.d(TAG, "FAB: CLICKED!xxxxx")
            bus.post(AddProduct())
        }

        approveProductsForMealImageButton?.setOnClickListener {
            bus.post(ApproveProductsForMeal())
/*            ingredientsCardView?.visibility = View.GONE
            recyclerView?.setPadding(0,0,0,0)
            bus.post(ClearProductsInListDatabase())
            bus.post(SetSharedPrefTo0())
            bus.post(ReloadProducts())*/

        }

        Log.d(TAG, "iniT UI: XXXXXX")
        bus.post(ClearProductsInListDatabase())

    }

    fun initRecyclerView(products: MutableList<Product>) {
        //bringing back original UI
        ingredientsCardView?.visibility = View.GONE
        recyclerView?.setPadding(0,0,0,0)
        bus.post(ClearProductsInListDatabase())
        bus.post(SetSharedPrefTo0())

        Log.d(TAG, "initRecyclerView: xxxx INIT RECYCLERVIEW")
        adapter = ProductsRecyclerAdapter(products, activity)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = adapter
        recyclerView!!.visibility = View.VISIBLE

        if(recyclerView!!.itemDecorationCount == 0){
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager(activity).orientation
            )
        )
        }
    }

    private fun addProductsToDish() {
        //change trashcan icons to plus icons
        try {
            //changing elements of recyclerview
            val itemCount = recyclerView?.adapter?.itemCount
            if(itemCount == 0){
                val mBuilder = AlertDialog.Builder(activity)
                mBuilder.setMessage(activity.getString(R.string.no_products_main_menu_message))
                mBuilder.setNegativeButton("Ok", null)
                val mDialog = mBuilder.create()
                mDialog.show()
            }else{
                bus.post(UserIsAddingProductsToMeal())
                bus.post(ClearProductsInListDatabase())

                for (i in 0 until itemCount!!) {
                    val holder = recyclerView?.findViewHolderForAdapterPosition(i)
                    if (holder != null) {
                        if(adapter.dataSet[holder.adapterPosition].englishType==""){
                            val infoButton = holder.itemView.findViewById<View>(R.id.info_button_product)
                            infoButton.visibility = View.VISIBLE
                            val deleteButton =
                                holder.itemView.findViewById<View>(R.id.delete_product_button)
                            deleteButton.visibility = View.GONE
                            val addButton =
                                holder.itemView.findViewById<View>(R.id.add_product_to_meal_button)
                            addButton.visibility = View.GONE

                            holder.itemView.setBackgroundResource(R.drawable.recycler_item_no_background)
                            val minusButton =
                                holder.itemView.findViewById<View>(R.id.delete_product_from_meal_button)
                            minusButton.visibility = View.GONE
                        }else {
                            holder.itemView.setBackgroundResource(R.drawable.recycler_item_no_background)
                            val minusButton =
                                holder.itemView.findViewById<View>(R.id.delete_product_from_meal_button)
                            minusButton.visibility = View.GONE

                            val infoButton = holder.itemView.findViewById<View>(R.id.info_button_product)
                            infoButton.visibility = View.GONE
                            //holder.itemView.setBackgroundResource(R.drawable.recycler_tem_bg_green)
                            val deleteButton =
                                holder.itemView.findViewById<View>(R.id.delete_product_button)
                            deleteButton.visibility = View.GONE
                            val addButton =
                                holder.itemView.findViewById<View>(R.id.add_product_to_meal_button)
                            addButton.visibility = View.VISIBLE
                        }
                    }
                }

                //changing other UI elements
                recyclerView?.clipToPadding = false
                recyclerView?.setPadding(0,0,0,330)
                ingredientsCardView?.visibility = View.VISIBLE
                changeNumberOfProductsOnList(0)
            }


        }catch(e: java.lang.Exception){
            Log.d(TAG, "addProductsToDish: EXCEPTION: $e")
        }

    }

    fun showChangeLang(listItems: Array<String>) {

        //val listItmes = arrayOf(activity.getString(R.string.jezyk_pl), activity.getString(R.string.jezyk_en))

        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle(activity.getString(R.string.language_choice))
        mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
            if (which == 0) {
                bus.post(ChangeLanguage("pl"))
                dialog.dismiss()
                drawerLayout?.close()
            } else if (which == 1) {
                bus.post(ChangeLanguage("en"))
                drawerLayout?.close()
                dialog.dismiss()
            }
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun showNotificationOptions(isEnabled: Boolean) {

        val options = arrayOf(activity.getString(R.string.wlacz_powiadomienia))
        val isEnabled = booleanArrayOf(isEnabled)
        var isNotificationEnabled = true

        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle(activity.getString(R.string.ustawienia_powiadomien))
        mBuilder.setMultiChoiceItems(options,isEnabled){ dialog, which, isChecked ->
            // The user checked or unchecked a notification on/off box
            if(which==0){
                isNotificationEnabled = isChecked
            }
        }
        mBuilder.setPositiveButton("Ok"){ dialog, which ->
            val editor = activity.getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE).edit()
            editor.putBoolean("Notifications", isNotificationEnabled)
            editor.apply()

            //FOR LOGGING PURPOSE
            val sharedPreferences = activity.getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
            val notifications = sharedPreferences.getBoolean("Notifications",true)
            Log.d(TAG, "showNotificationOptions: IS NOTIFICATION ENABLED: $notifications")
        }

        mBuilder.setNegativeButton(activity.getString(R.string.cancel), null)
        
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun showFoodPreferences(foodPreferences: List<Preference>){
            // Set up the alert builder
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(activity.getText(R.string.food_preferences))

            Log.d(TAG, "showFoodPreferences: ${foodPreferences}")

            val keys = arrayListOf<Int>()
            val booleanValues = arrayListOf<Boolean>()
            val preferencesList = arrayListOf<String>()

            //retrieving food preferences in adequate language
            for (i in foodPreferences) {
                val key = i.stringID
                if (key != null) {
                    keys.add(key)
                }
                booleanValues.add(i.isChecked)
                preferencesList.add(activity.getString(key!!))
                /*val mKey = i.toString().split("=")[0]
                val intKey = mKey.toInt()
                keys.add(intKey)
                Log.d(TAG, "showFoodPreferences item: $i")
                preferencesList.add(activity.getString(intKey))*/
            }

            val typedArray = preferencesList.toTypedArray()

            // Add a checkbox list
            builder.setMultiChoiceItems(
                typedArray,
                booleanValues.toBooleanArray()
            ) { dialog, which, isChecked ->

                // The user checked or unchecked a box
                val clickedItem = typedArray[which]
                Log.d(TAG, "showFoodPreferences: Clicked on $clickedItem")


                for (i in foodPreferences) {
                    val mKey = i.stringID
                    if (activity.getString(mKey) == clickedItem) {
                        i.isChecked = isChecked
                        Log.d(
                            TAG,
                            "showFoodPreferences: GET STRING (ITEM) = ${activity.getString(mKey)}"
                        )
                        Log.d(
                            TAG,
                            "showFoodPreferences: CHANGED ITEM: ${i.apiLabel} VALUE: ${i.isChecked}"
                        )
                    }
                }
            }

            builder.setPositiveButton("OK") { dialog, which ->
                bus.post(StoreFoodPrefInSharedPref(foodPreferences))
            }

            builder.setNegativeButton("Cancel", null)


            val dialog = builder.create()
            dialog.show()

    }

    fun notifyAdapter(){
        //poprawić potem na coś wydajniejszego
        recyclerView?.adapter?.notifyDataSetChanged()
    }
    fun doSomething(){}

    fun getContext():Context{
        return activity
    }

    @SuppressLint("SetTextI18n")
    fun changeNumberOfProductsOnList(numberOfProducts: Int){
        ingredientsForMealTextView?.text = "${activity.getString(R.string.cardview_tekst)} $numberOfProducts"
    }

    fun showToast(text: String){
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.new_recipe_with_my_products) {
            drawerLayout?.closeDrawer(GravityCompat.START)
            addProductsToDish()
        }

        if(item.itemId == R.id.my_recepies){
            bus.post(ShowMyRecipies())
        }

        if(item.itemId == R.id.change_language){
            bus.post(ShowLanguageChoice())
            Log.d(TAG, "onNavigationItemSelected: click!")
        }

        if(item.itemId == R.id.notifications){
            bus.post(ShowNotificationOptions())
            Log.d(TAG, "onNavigationItemSelected: click!")
        }

        if(item.itemId == R.id.food_preferences){
            bus.post(GetFoodPrefFromSharedPref())
        }

        if(item.itemId == R.id.about){
            bus.post(ShowAboutScreen())
        }
       return true
    }



    class AddProduct{}
    class DeleteProduct(var product: Product){}
    class InitRecyclerView{}
    class ReloadProducts
    class AddProductToRecipe(val product: Product)
    class UserIsAddingProductsToMeal
    class DeleteProductFromDish(val product: Product)
    class OpenProductDetails(val primaryKey: Int)
    class ShowLanguageChoice
    class ChangeLanguage(val lang: String)
    class ClearProductsInListDatabase
    class SetSharedPrefTo0()
    class ApproveProductsForMeal()
    class GetFoodPrefFromSharedPref
    class StoreFoodPrefInSharedPref(val foodPreferences: List<Preference>)
    class ShowAboutScreen()
    class ShowNotificationOptions
    class ShowMyRecipies

}

