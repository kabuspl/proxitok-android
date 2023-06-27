package ovh.kabus.proxitok

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun SettingsScreen(viewModel: VideoViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.proxitokInstance.collectAsState().value,
                onValueChange = {
                    viewModel.proxitokInstance.value=it
                    runBlocking {
                        viewModel.setStringPref("instance_url", it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.instance_url))
                }
            )
            SettingsSwitch(
                title = stringResource(id = R.string.autoplay),
                changeAction = {
                    viewModel.autoplay.value = it
                    runBlocking {
                        viewModel.setBoolPref("autoplay", it)
                    }
               },
                defaultValue = viewModel.autoplay.collectAsState().value
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Prev() {
    SettingsSwitch(title = "Skurwiel", changeAction = {

    }, defaultValue = true)
}

@Composable
fun SettingsSwitch(title: String, changeAction: ((Boolean)->Unit)?, defaultValue: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = rememberRipple(bounded = true), onClick = {
            if (changeAction != null) {
                changeAction(!defaultValue)
            }
        })
    ) {
        Text(title,modifier = Modifier.weight(1F), fontSize = MaterialTheme.typography.titleMedium.fontSize)
        Switch(checked = defaultValue, onCheckedChange = changeAction)
    }
}