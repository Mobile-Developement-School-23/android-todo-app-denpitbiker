package com.advancedsolutionsdevelopers.todoapp.presentation.notificationsPermFragment

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advancedsolutionsdevelopers.todoapp.R
import com.advancedsolutionsdevelopers.todoapp.presentation.MainActivity
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.AppTheme
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.ToDoTypography
import com.advancedsolutionsdevelopers.todoapp.presentation.theme.switchBackColor
import com.advancedsolutionsdevelopers.todoapp.utils.Constant.SHOW_NOTIFICATIONS_KEY
import javax.inject.Inject

class NotificationPermFragment : Fragment() {
    @Inject
    lateinit var sp: SharedPreferences
    private lateinit var arl: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).activityComponent.inject(this)
        arl =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    notificationGranted()
                } else {
                    notificationDenied()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    NotificationPermScreen()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun PreviewScreen() {
        AppTheme {
            NotificationPermScreen()
        }
    }


    @Preview
    @Composable
    private fun NotificationPermScreen() {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(vertical = 20.dp, horizontal = 20.dp)
            ) {
                (Image(
                    painterResource(R.drawable.bl),
                    contentDescription = stringResource(R.string.here_goes_your_ad),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                ))
                Box(Modifier.padding(vertical = 40.dp)) {
                    Text(
                        stringResource(R.string.notify_desc),
                        style = ToDoTypography.bodyMedium
                    )
                }
                Spacer(Modifier.weight(1f))
                Row {
                    TextButton(onClick = { notificationDenied() }) {
                        Text(
                            stringResource(R.string.skip),
                            color = switchBackColor,
                            style = ToDoTypography.labelLarge
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { askForPerm() }) {
                        Text(
                            text = stringResource(R.string.oke),
                            modifier = Modifier,
                            style = ToDoTypography.titleMedium
                        )
                    }
                }
            }
        }
    }

    private fun askForPerm() {
        if (android.os.Build.VERSION.SDK_INT > 32) {
            arl.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificationGranted()
        }
    }

    private fun notificationGranted() {
        saveChoice(true)
        Toast.makeText(
            requireContext(),
            getString(R.string.enjoy_notifications),
            Toast.LENGTH_SHORT
        ).show()
        exitFragment()
    }

    private fun notificationDenied() {
        Toast.makeText(
            requireContext(),
            getString(R.string.turn_later),
            Toast.LENGTH_SHORT
        ).show()
        saveChoice(false)
        exitFragment()
    }

    private fun saveChoice(isGranted: Boolean) {
        sp.edit().putBoolean(SHOW_NOTIFICATIONS_KEY, isGranted).apply()
    }

    private fun exitFragment() {
        requireActivity().supportFragmentManager.popBackStack()
    }
}