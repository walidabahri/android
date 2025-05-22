package com.walid.abahri.mealplanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.walid.abahri.mealplanner.R

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain NavHost fragment and NavController
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        // Dynamically choose start destination based on login status:contentReference[oaicite:28]{index=28}
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("isLoggedIn", false)) {
            // User is logged in, start at Dashboard
            navGraph.setStartDestination(R.id.dashboardFragment)
        } else {
            // Not logged in, start at Login
            navGraph.setStartDestination(R.id.loginFragment)
        }
        navController.graph = navGraph  // Set the modified navigation graph
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle action bar "Up" button
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
