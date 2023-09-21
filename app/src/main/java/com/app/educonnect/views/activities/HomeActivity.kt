package com.app.educonnect.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.educonnect.R
import com.app.educonnect.databinding.ActivityHomeBinding
import com.app.educonnect.databinding.NavHeaderBinding
import com.app.educonnect.utils.Extensions
import com.app.educonnect.utils.FirebaseUtils.firebaseAuth
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawerLayout) }
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }
    private val navigationView: NavigationView by lazy { findViewById(R.id.navView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewHeader = binding.navView.getHeaderView(0)
        val navViewHeaderBinding: NavHeaderBinding = NavHeaderBinding.bind(viewHeader)
        val userMail = Extensions.getSharedPreferenceUser(this)
        navViewHeaderBinding.txtUser.text = userMail


        // Show and Manage the Drawer and Back Icon
        setupActionBarWithNavController(navController, drawerLayout)
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> logOut()
                else -> {
                    NavigationUI.onNavDestinationSelected(it, navController)
                    drawerLayout.closeDrawers()
                }
            }

            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return navController.navigateUp(drawerLayout)
    }

    private fun logOut() {
        Extensions.clearPreferences(this)
        firebaseAuth.signOut()
        val intent = Intent(this@HomeActivity, MainActivity::class.java)
        finish()
        startActivity(intent)
    }


}