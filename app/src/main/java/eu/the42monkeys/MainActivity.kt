package eu.the42monkeys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import eu.the42monkeys.databinding.ActivityMainBinding
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

        // Send device id to server
        val token = SharedPrefsHelper.getNotificationToken(applicationContext)
        val requestBody = "{\"token\":\"$token\",\"platform\":\"android\"}"
        val jwtToken = SharedPrefsHelper.getJwtToken(applicationContext)

        if (jwtToken != null) {
            Fuel.post("${BuildConfig.BACKEND_URL}/devices.json")
                .header("Authorization", jwtToken)
                .header("Content-Type" to "application/json")
                .body(requestBody, Charset.forName("UTF-8"))
                .response { _, _, result ->
                    when (result) {
                        is Result.Success -> {
                        }

                        is Result.Failure -> {
                            val exception = result.getException()
                            Toast.makeText(
                                this,
                                "Send token to server error $exception",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
        }

        // if coming from notification
        val temper = intent.getStringExtra("temper")
        val body = intent.getStringExtra("body")

        if (temper != null && body != null) {
            val bundle = Bundle()
            bundle.putString("body", body)
            bundle.putString("temper", temper)
            navController.navigate(R.id.action_Initial_to_Reminder, bundle)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val jwtToken = SharedPrefsHelper.getJwtToken(this) ?: return false
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}