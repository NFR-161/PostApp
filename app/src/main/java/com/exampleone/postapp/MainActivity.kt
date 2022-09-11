package com.exampleone.postapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampleone.postapp.act.EditAdsAct
import com.exampleone.postapp.adapters.AdsRcAdapter
import com.exampleone.postapp.data.Ad
import com.exampleone.postapp.database.DbManager
import com.exampleone.postapp.database.ReadDataCallback
import com.exampleone.postapp.databinding.ActivityMainBinding
import com.exampleone.postapp.dialoghelper.DialogConst
import com.exampleone.postapp.dialoghelper.DialogHelper
import com.exampleone.postapp.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ReadDataCallback {
    private lateinit var tvAccount: TextView
    val dbManager = DbManager(this)
    val adapter = AdsRcAdapter()

    private val rootElement by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(rootElement.root)
        init()
        initRecyclerView()
        dbManager.readDataFromDb()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_new_ads) {
            val i = Intent(this, EditAdsAct::class.java)
            startActivity(i)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return menu.let { super.onCreateOptionsMenu(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error : ${e.message}")
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun init() {
        setSupportActionBar(rootElement.mainContent.toolbar)
        val toggle =
            ActionBarDrawerToggle(
                this,
                rootElement.drawerLayout,
                rootElement.mainContent.toolbar,
                R.string.open,
                R.string.close
            )
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    private fun initRecyclerView() {
        rootElement.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.id_my_ads -> {

            }
            R.id.id_my_fav -> {

            }
            R.id.id_car -> {

            }

            R.id.id_pc -> {

            }
            R.id.id_smart -> {

            }
            R.id.id_dm -> {

            }
            R.id.id_sign_up -> {

                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
            R.id.id_sign_in -> {

                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.id_sign_out -> {

                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutG()
            }
        }
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else {
            user.email
        }
    }

    override fun readData(list: List<Ad>) {
        adapter.updateAdapter(list)
    }

}