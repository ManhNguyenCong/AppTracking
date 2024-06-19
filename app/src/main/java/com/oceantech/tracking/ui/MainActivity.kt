package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.mvrx.viewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseException
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityMainBinding
import com.oceantech.tracking.ui.home.viewmodel.HomeViewAction
import com.oceantech.tracking.ui.home.viewmodel.HomeViewEvent
import com.oceantech.tracking.ui.home.viewmodel.HomeViewModel
import com.oceantech.tracking.ui.home.viewmodel.HomeViewState
import com.oceantech.tracking.ui.home.viewmodel.TestViewModel
import com.oceantech.tracking.utils.LocalHelper
import java.util.Locale
import javax.inject.Inject

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var sharedActionViewModel: TestViewModel

    private var isNavigateUp = false

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        sharedActionViewModel = viewModelProvider.get(TestViewModel::class.java)
        setContentView(views.root)
        setupToolbar()
        setupDrawer()
        sharedActionViewModel.test()

        homeViewModel.subscribe(this) {
            if (it.isLoading()) {
                views.appBarMain.contentMain.waitingView.visibility = View.VISIBLE
            } else
                views.appBarMain.contentMain.waitingView.visibility = View.GONE
        }

        homeViewModel.observeViewEvents {
            if (it is HomeViewEvent.SetNavUp) {
                isNavigateUp = true
            }
        }


    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setupToolbar() {
        toolbar = views.toolbar
        toolbar.title = ""
        views.title.text = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    @SuppressLint("ResourceType")
    private fun setupDrawer() {
        drawerLayout = views.appBarMain.drawerLayout
        navView = views.appBarMain.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_HomeFragment,
                R.id.blogFragment,
                R.id.personalInfoFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // settings
        navView.setNavigationItemSelectedListener { menuItem ->

            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)

            when (menuItem.itemId) {
                R.id.exit -> {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(homeIntent)
                }

                R.id.nav_change_langue -> {
                    showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
                }

                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
            handled
        }

        val menu: Menu = navView.menu
        val menuItem = menu.findItem(R.id.nav_change_langue)
        val actionView: View = MenuItemCompat.getActionView(menuItem)

        val lang = resources.configuration.locale.displayLanguage
        if (lang == "English") {
            homeViewModel.language = 0
            menuItem.title = getString(R.string.en)
        } else {
            menuItem.title = getString(R.string.vi)
            homeViewModel.language = 1
        }

        val buttonShowMenu = actionView as AppCompatImageView
        buttonShowMenu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_drop))
        buttonShowMenu.setOnClickListener {
            showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
        }
    }

    private fun changeLanguage(lang: String) {
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        val myLocale = Locale(lang)
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
        updateLanguage(lang)
        SessionManager(this).saveLanguageKey(lang)
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 20F
        popup.setBackgroundDrawable(getDrawable(R.drawable.backgound_box))
        popup.showAsDropDown(v, 280, -140, Gravity.CENTER_HORIZONTAL)
        view.findViewById<LinearLayout>(R.id.to_lang_en).setOnClickListener {
            changeLanguage("en")
            homeViewModel.language = 0
            popup.dismiss()
            homeViewModel.handle(HomeViewAction.ResetLang)
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLanguage("vi")
            homeViewModel.language = 1
            popup.dismiss()
            homeViewModel.handle(HomeViewAction.ResetLang)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun navigateTo(fragmentId: Int) {
        navController.navigate(fragmentId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (isNavigateUp) {
                    isNavigateUp = false
                    navController.navigateUp()
                    return true
                }

                if (drawerLayout.isOpen) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }

            R.id.menu_notification -> {
                navigateTo(R.id.notificationsFragment)
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun updateLanguage(lang: String) {
        val menu: Menu = navView.menu
        menu.findItem(R.id.nav_HomeFragment).title = getString(R.string.menu_home)
        menu.findItem(R.id.blogFragment).title = getString(R.string.blog)
        menu.findItem(R.id.personalInfoFragment).title = getString(R.string.personal_info)
        menu.findItem(R.id.nav_change_langue).title =
            if (lang == "en") getString(R.string.en) else getString(R.string.vi)
        menu.findItem(R.id.exit).title = getString(R.string.exit)
    }


}

