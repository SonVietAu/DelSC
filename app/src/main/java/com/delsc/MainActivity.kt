package com.delsc

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.delsc.ui.main.DepositsFragment
import com.delsc.ui.main.CustomersFragment
import com.delsc.ui.main.DriversFragment
import com.delsc.ui.main.MainFragment
import com.delsc.ui.main.PricesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var driversMenuItem: MenuItem
    private lateinit var customersMenuItem: MenuItem
    private lateinit var pricesMenuItem: MenuItem
    private lateinit var depositMenuItem: MenuItem

    // TODO: Hide Driver, Customers, Prices MenuItems from non-admin users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        driversMenuItem = menu.findItem(R.id.driversMenuItem)
        customersMenuItem = menu.findItem(R.id.customersMenuItem)
        pricesMenuItem = menu.findItem(R.id.pricesMenuItem)
        depositMenuItem = menu.findItem(R.id.depositsMenuItem)

        driversMenuItem.setVisible(false)
        customersMenuItem.setVisible(false)
        pricesMenuItem.setVisible(false)
        depositMenuItem.setVisible(false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        val visibleFragment = supportFragmentManager.fragments.first {
            it.isVisible
        }
        return when (item.itemId) {
            R.id.driversMenuItem -> {

                if (visibleFragment !is DriversFragment)
                    commitFragmentReplacement(DriversFragment())

                true
            }

            R.id.customersMenuItem -> {

                if (visibleFragment !is CustomersFragment)
                    commitFragmentReplacement(CustomersFragment.newInstance())

                true
            }

            R.id.pricesMenuItem -> {

                if (visibleFragment !is PricesFragment)
                    commitFragmentReplacement(PricesFragment.newInstance())

                true
            }

            R.id.depositsMenuItem -> {

                if (visibleFragment !is DepositsFragment)
                    commitFragmentReplacement(DepositsFragment())

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun isToShowMenuItems(isToShowDepositMenuItem: Boolean, isAdmin: Boolean) {

        driversMenuItem.setVisible(isAdmin)
        customersMenuItem.setVisible(isAdmin)
        pricesMenuItem.setVisible(isAdmin)

        depositMenuItem.setVisible(isToShowDepositMenuItem)
    }

    private fun commitFragmentReplacement(fragment: Fragment) {
        val transaction: FragmentTransaction =
            getSupportFragmentManager().beginTransaction()

        transaction.replace(R.id.container, fragment).addToBackStack(null).commit()
    }

}