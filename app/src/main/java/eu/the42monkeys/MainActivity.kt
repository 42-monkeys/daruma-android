package eu.the42monkeys

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import eu.the42monkeys.databinding.ActivityMainBinding
import eu.the42monkeys.model.Resolution
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { _ ->
            navController.navigate(R.id.action_ResolutionsList_to_EditResolution)
        }

        val requestBody = "{\"user\":{\"email\":\"lorenzo.farnararo@gmail.com\",\"password\":\"password\"}}"

        Fuel.post("${BuildConfig.BACKEND_URL}/users/sign_in.json")
            .header("Content-Type" to "application/json")
            .body(requestBody, Charset.forName("UTF-8"))
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        var bearerToken = response["Authorization"].first()
                        SharedPrefsHelper.saveJwtToken(this.applicationContext, bearerToken)
                        Log.d("","")
                    }

                    is Result.Failure -> {
                        val exception = result.getException()
                        Toast.makeText(
                            this,
                            "${R.string.list_resolution_error_server_call}: $exception",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}