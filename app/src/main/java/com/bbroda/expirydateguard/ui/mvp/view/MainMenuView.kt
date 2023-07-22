package com.bbroda.expirydateguard.ui.mvp.view

import android.content.ContentValues.TAG
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bbroda.expirydateguard.R
import com.bbroda.expirydateguard.ui.activities.MainMenuActivity
import com.bbroda.expirydateguard.ui.adapters.RecyclerAdapter
import com.bbroda.expirydateguard.ui.classes.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference


class MainMenuView(var activity: MainMenuActivity, bus: EventBus) : NavigationView.OnNavigationItemSelectedListener {

    private val activityRef = WeakReference(activity)

    private val drawerLayout: DrawerLayout? = activity.findViewById(R.id.drawer_layout)
    private val navigationView: NavigationView? = activity.findViewById(R.id.nav_view)
    private val toolbar: Toolbar? = activity.findViewById(R.id.my_toolbar)
    private var recyclerView: RecyclerView? = activity.findViewById(R.id.my_products_recycler)
    private val fabAddProduct: FloatingActionButton? = activity.findViewById(R.id.FAB_Add_product)

    lateinit var adapter: RecyclerAdapter

    init {
        navigationView?.bringToFront()
        activity.setSupportActionBar(toolbar)
        toolbar?.title = ""

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

        Log.d(TAG, "iniT UI: XXXXXX")
    }

    fun initRecyclerView(products: MutableList<Product>) {
        Log.d(TAG, "initRecyclerView: xxxx INIT RECYCLERVIEW")
        adapter = RecyclerAdapter(products)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = adapter
        recyclerView!!.visibility = View.VISIBLE
    }

    fun notifyAdapter(){
        //poprawić potem na coś wydajniejszego
        recyclerView?.adapter?.notifyDataSetChanged()
    }
    fun doSomething(){}

    class AddProduct{}

    class DeleteProduct(var product: Product){}

    class InitRecyclerView{}

    class ReloadProducts

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}

