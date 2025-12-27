package isimm.ing1.mobile.presentation.screens.password

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import isimm.ing1.mobile.ui.theme.MobileTheme

/**
 * Legacy Activity - keeping for backwards compatibility.
 * Navigation is now handled through MainActivity with NavHost.
 */
class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResetPasswordScreen(
                        paddingValues = innerPadding,
                        onResetSuccess = {
                            // TODO: Navigate back to login
                            finish()
                        },
                        onBackClick = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}
