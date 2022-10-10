package com.exampleone.postapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampleone.postapp.act.EditAdsAct
import com.exampleone.postapp.adapters.AdsRcAdapter
import com.exampleone.postapp.databinding.ActivityMainBinding
import com.exampleone.postapp.dialoghelper.DialogConst
import com.exampleone.postapp.dialoghelper.DialogHelper
import com.exampleone.postapp.dialoghelper.GoogleAccConst
import com.exampleone.postapp.model.Ad
import com.exampleone.postapp.viewmodel.FireBaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdsRcAdapter.Listener {
    private lateinit var tvAccount: TextView

    val mAuth = Firebase.auth
    val adapter = AdsRcAdapter(this)
    private val fireBaseViewModel: FireBaseViewModel by viewModels()

    private val rootElement by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val dialogHelper = DialogHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(rootElement.root)
        init()
        initRecyclerView()
        initViewModel()
        fireBaseViewModel.loadAllAds()
        bottomMenuOnClick()
    }

    override fun onResume() {
        super.onResume()
        rootElement.mainContent.bNavView.selectedItemId = R.id.id_home
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


    private fun initViewModel() {
        fireBaseViewModel.liveAdsData.observe(this) {
            adapter.updateAdapter(it)
        }
    }

    private fun bottomMenuOnClick() = with(rootElement) {
        mainContent.bNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)

                }
                R.id.id_my_ads -> {
                    fireBaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_favs -> {
                    fireBaseViewModel.loadMyFavs()
                }
                R.id.id_home -> {
                    fireBaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.def)
                }
            }
            true
        }


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

    override fun onDeleteItem(ad: Ad) {
        fireBaseViewModel.deleteItem(ad)

    }

    override fun onAdViewed(ad: Ad) {
        fireBaseViewModel.adViewed(ad)
    }

    override fun onFavClicked(ad: Ad) {
        fireBaseViewModel.onFavClick(ad)
    }

    companion object {

        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
    }
}