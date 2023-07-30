package com.advancedsolutionsdevelopers.todoapp.presentation.settingsFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.data.network.PassportAuthContract
import com.advancedsolutionsdevelopers.todoapp.presentation.MainActivity
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.AppTheme
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.ToDoTypography
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.lightOnPrimary
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.redColor
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.switchBackColor
import com.advancedsolutionsdevelopers.todoapp.presentation.todoListFragment.RegisterResultCallback
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SHOW_NOTIFICATIONS_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SP_NAME
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.THEME_CODE_KEY
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.TOKEN_KEY
import javax.inject.Inject

class SettingsFragment : Fragment() {
    @Inject
    lateinit var callback: RegisterResultCallback

    @Inject
    lateinit var sp: SharedPreferences
    private val themeButtonState = mutableStateOf(0)
    private var isAuthorized = mutableStateOf(true)
    private var startForResult: ActivityResultLauncher<Any?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).activityComponent.inject(this)
        themeButtonState.value = sp.getInt(THEME_CODE_KEY, -1)
        if (themeButtonState.value == -1) themeButtonState.value++
        startForResult = registerForActivityResult(
            PassportAuthContract(), callback
        )
    }

    override fun onResume() {
        super.onResume()
        isAuthorized.value = sp.contains(TOKEN_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    SettingsScreen()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun PreviewScreen() {
        AppTheme {
            SettingsScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun SettingsScreen() {
        val isAuth by remember {isAuthorized}
        Scaffold(
            topBar = {
                Surface(shadowElevation = 10.dp) {
                    TopAppBar(title = {}, navigationIcon = {
                        IconButton(onClick = { requireActivity().supportFragmentManager.popBackStack() }) {
                            Icon(Icons.Default.Close, null)
                        }
                    })
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(vertical = 40.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.app_theme),
                        Modifier.padding(10.dp),
                        style = ToDoTypography.titleMedium
                    )
                    ThemeSwitch()
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentEnforcement provides false,
                    ) {
                        Card(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(vertical = 30.dp),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    sp.edit().remove(SHOW_NOTIFICATIONS_KEY).apply()
                                    requireActivity().supportFragmentManager.popBackStack()
                                },
                                Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = stringResource(R.string.drop_notify_prefs),
                                    color = redColor,
                                    style = ToDoTypography.bodyMedium
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .width(100.dp)
                                .wrapContentHeight(),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            if (isAuth) {
                                TextButton(
                                    onClick = {
                                        sp.edit().remove(TOKEN_KEY).apply()
                                        requireActivity().supportFragmentManager.popBackStack()
                                    },
                                    Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.log_out), color = redColor,
                                        style = ToDoTypography.bodyMedium
                                    )
                                }
                            } else {
                                TextButton(
                                    onClick = {
                                        startForResult!!.launch(null)
                                    },
                                    Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.log_in),
                                        style = ToDoTypography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun ThemeSwitch() {
        val modes = stringArrayResource(R.array.themes)
        val checkedButton = remember { themeButtonState }
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement provides false,
        ) {
            Card(
                modifier = Modifier
                    .wrapContentHeight(), elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Row {
                    for (i in modes.indices) {
                        val buttonColors =
                            if (checkedButton.value == i) ButtonDefaults.buttonColors(
                                containerColor = switchBackColor,
                                contentColor = lightOnPrimary
                            ) else ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        TextButton(
                            onClick = {
                                checkedButton.value = i
                                val mode =
                                    checkedButton.value + if (checkedButton.value == 0) -1 else 0
                                sp.edit().putInt(THEME_CODE_KEY, mode).apply()
                                AppCompatDelegate.setDefaultNightMode(mode)
                            },
                            shape = RectangleShape,
                            colors = buttonColors
                        ) {
                            Text(
                                text = modes[i],
                                style = ToDoTypography.bodyMedium
                            )

                        }
                    }
                }
            }
        }
    }
}