package ovh.kabus.proxitok

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(viewModel: VideoViewModel, navController: NavController) {
    val mainActivity = LocalContext.current as MainActivity

    val openDialog = remember { mutableStateOf(false) }
    val openDialogUrl = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Proxitok")
                },
                actions = {
                    if(viewModel.isVideoLoaded.collectAsState().value) {
                        IconButton(onClick = { openDialog.value = true }) {
                            Icon(painter = painterResource(id = R.drawable.link), contentDescription = stringResource(id = R.string.video_from_url), modifier = Modifier.size(24.dp))
                        }
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = stringResource(id = R.string.settings))
                    }
                }
            )
        },
        content = {
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                        openDialogUrl.value = ""
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(stringResource(id = R.string.video_from_url), fontSize = MaterialTheme.typography.titleLarge.fontSize)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = openDialogUrl.value, modifier = Modifier.fillMaxWidth(), label = { Text(stringResource(id = R.string.url)) }, maxLines = 1 , onValueChange = {
                                openDialogUrl.value = it
                            })
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    openDialog.value = false
                                    openDialogUrl.value = ""
                                }) {
                                    Text(stringResource(id = R.string.cancel))
                                }
                                TextButton(onClick = {
                                    openDialog.value = false
                                    viewModel.playVideo(Uri.parse(openDialogUrl.value))
                                    openDialogUrl.value = ""
                                }) {
                                    Text(stringResource(id = R.string.open))
                                }
                            }
                        }
                    }
                }
            }
            if(viewModel.isVideoLoaded.collectAsState().value) {
                VideoPlayingUI(viewModel = viewModel, padding = it, mainActivity = mainActivity)
            }else{
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(stringResource(id = R.string.no_video_loaded))
                    Button(onClick = {
                        openDialog.value = true
                    }) {
                        Text(text = stringResource(id = R.string.video_from_url))
                    }
                }
            }
        }
    )
}

@Composable
fun VideoPlayingUI(mainActivity: MainActivity, viewModel: VideoViewModel, padding: PaddingValues) {
    Column(modifier = Modifier
        .padding(padding)
        .verticalScroll(rememberScrollState())
    ) {
        TiktokPlayer(viewModel)
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoStat(
                    icon = painterResource(id = R.drawable.favorite),
                    statDescription = "Likes",
                    text = viewModel.videoLikes.collectAsState().value
                )
                VideoStat(
                    icon = painterResource(id = R.drawable.visibility),
                    statDescription = "Views",
                    text = viewModel.videoViews.collectAsState().value
                )
                VideoStat(
                    icon = painterResource(id = R.drawable.chat),
                    statDescription = "Comments",
                    text = viewModel.videoComments.collectAsState().value
                )
                VideoStat(
                    icon = painterResource(id = R.drawable.share),
                    statDescription = "Shares",
                    text = viewModel.videoShared.collectAsState().value
                )
            }
            Column {
                Text(viewModel.videoAuthor.collectAsState().value, fontSize = 15.sp, color = MaterialTheme.colorScheme.secondary)
                Text(viewModel.videoTitle.collectAsState().value, fontSize = 20.sp, lineHeight = 25.sp)
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    Button(onClick = { viewModel.shareMenuExpanded.value = true },) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(id = R.string.share),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(id = R.string.share))
                    }
                    DropdownMenu(
                        expanded = viewModel.shareMenuExpanded.collectAsState().value,
                        onDismissRequest = { viewModel.shareMenuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.instance))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock),
                                    contentDescription = stringResource(id = R.string.instance),
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            },
                            onClick = {
                                mainActivity.shareLink(viewModel.proxitokInstance.value+"redirect/search?type=url&term="+viewModel.getCurrentVideoUrl())
                                viewModel.shareMenuExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.original))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.lock_open),
                                    contentDescription = stringResource(id = R.string.original),
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            },
                            onClick = {
                                mainActivity.shareLink(viewModel.getCurrentVideoUrl().toString())
                                viewModel.shareMenuExpanded.value = false
                            }
                        )
                    }
                }

                Box {
                    OutlinedButton(onClick = { viewModel.downloadMenuExpanded.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.download),
                            contentDescription = stringResource(id = R.string.download),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(id = R.string.download))
                    }
                    DropdownMenu(
                        expanded = viewModel.downloadMenuExpanded.collectAsState().value,
                        onDismissRequest = { viewModel.downloadMenuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.watermark))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.water_drop_filled),
                                    contentDescription = stringResource(id = R.string.watermark),
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            },
                            onClick = {
                                viewModel.downloadWithWatermark()
                                viewModel.downloadMenuExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.no_watermark))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.water_drop),
                                    contentDescription = stringResource(id = R.string.no_watermark),
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            },
                            onClick = {
                                viewModel.download()
                                viewModel.downloadMenuExpanded.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoStat(icon: Painter, statDescription: String, text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(painter = icon, contentDescription = statDescription, modifier = Modifier.size(18.dp))
            Text(text, fontSize = 16.sp)
        }
    }
}

@Composable
fun VideoStat(icon: ImageVector, statDescription: String, text: String) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = statDescription, modifier = Modifier.size(20.dp))
        Text(text, fontSize = 20.sp)
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun TiktokPlayer(viewModel: VideoViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(viewModel.videoComponentAspectRatio.collectAsState().value),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                PlayerView(it).also {
                    it.player = viewModel.player
                    it.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                    it.player!!.addListener(ExoEventListener(viewModel, it.player!!))
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
        if(viewModel.isLoading.collectAsState().value) {
            CircularProgressIndicator()
        }
    }
}
