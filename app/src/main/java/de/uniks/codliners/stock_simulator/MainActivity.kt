package de.uniks.codliners.stock_simulator

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.an.biometric.BiometricCallback
import com.an.biometric.BiometricManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * The main [Activity](https://developer.android.com/reference/androidx/appcompat/app/AppCompatActivity) of this application.
 * Handles cross-fragment navigation and settings.
 *
 * @author Jan Müller
 * @author Jonas Thelemann
 */
class MainActivity : AppCompatActivity(), BiometricCallback {

    /**
     * Allows fingerprint authentication.
     */
    lateinit var biometricManager: BiometricManager

    /**
     * Flag to decide on how to react to failed or successful fingerprint authentications.
     */
    private var isApplicationUnlocked = false

    /**
     * Sets the content view, initializes navigation and biometric authentication.
     *
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_account,
                R.id.navigation_search,
                R.id.navigation_history,
                R.id.navigation_stockbrot,
                R.id.navigation_achievements
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Create the fingerprint authenticator
        biometricManager = BiometricManager.BiometricBuilder(this)
            .setTitle(getString(R.string.add_fingerprint))
            .setSubtitle("")
            .setDescription("")
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        // If the application is locked and fingerprint authentication is enabled...
        if (!isApplicationUnlocked && getPreferences(Context.MODE_PRIVATE).getBoolean(
                getString(R.string.prefs_fingerprint_added),
                false
            )
        ) {
            // ... open fingerprint authentication.
            biometricManager.authenticate(this)
        } else {
            // ... unlock application.
            isApplicationUnlocked = true
        }
    }

    /**
     * Biometric authentication callback for unsupported SDK versions.
     */
    override fun onSdkVersionNotSupported() {
        Toast.makeText(
            this,
            getString(R.string.biometric_error_sdk_not_supported),
            Toast.LENGTH_LONG
        ).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for unsupported biometric authentication.
     */
    override fun onBiometricAuthenticationNotSupported() {
        Toast.makeText(
            this,
            getString(R.string.biometric_error_hardware_not_supported),
            Toast.LENGTH_LONG
        ).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for unavailable biometric authentication.
     */
    override fun onBiometricAuthenticationNotAvailable() {
        Toast.makeText(
            this,
            getString(R.string.biometric_error_fingerprint_not_available),
            Toast.LENGTH_LONG
        ).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for missing permissions.
     */
    override fun onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(
            this,
            getString(R.string.biometric_error_permission_not_granted),
            Toast.LENGTH_LONG
        ).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for internal biometric authentication errors.
     */
    override fun onBiometricAuthenticationInternalError(error: String?) {
        Toast.makeText(this, "Biometric authentication internal error: $error", Toast.LENGTH_LONG)
            .show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for authentication failures.
     */
    override fun onAuthenticationFailed() {
        Toast.makeText(this, getString(R.string.biometric_failure), Toast.LENGTH_LONG).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for canceled authentication.
     */
    override fun onAuthenticationCancelled() {
        Toast.makeText(this, getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }

        // This is not implemented in this release of "biometric-auth" yet.
        //
        // biometricManager.cancelAuthentication()
    }

    /**
     * Biometric authentication callback for authentication errors.
     */
    override fun onAuthenticationError(
        errorCode: Int,
        errString: CharSequence?
    ) {
        Toast.makeText(this, "Authentication error: $errString", Toast.LENGTH_LONG).show()

        // Close app as it wasn't unlocked successfully.
        if (!isApplicationUnlocked) {
            finishAndRemoveTask()
        }
    }

    /**
     * Biometric authentication callback for authentication help.
     */
    override fun onAuthenticationHelp(
        helpCode: Int,
        helpString: CharSequence?
    ) {
        Toast.makeText(this, "Authentication help: $helpString", Toast.LENGTH_LONG).show()
    }

    /**
     * Biometric authentication callback for authentication success.
     */
    override fun onAuthenticationSuccessful() {
        Toast.makeText(this, getString(R.string.biometric_success), Toast.LENGTH_LONG)
            .show()

        // Unlock application.
        with(getPreferences(Context.MODE_PRIVATE).edit()) {
            putBoolean(getString(R.string.prefs_fingerprint_added), true)
            isApplicationUnlocked = true
            apply()
        }
    }

    /**
     * Enables up-navigation from the toolbar's navigation button.
     *
     * @return true if navigation was successful and false otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }
}
