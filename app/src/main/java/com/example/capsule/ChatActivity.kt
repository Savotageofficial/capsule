// ChatActivity.kt
package com.example.capsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.capsule.ui.theme.CapsuleTheme

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapsuleTheme {
                // instead of a compose function, scaffold contains unique compose functions
                Scaffold(
                    topBar = {
                        TopNavBar(
                            doctorName = "Dr name goes here",
                            statusText = "online", //Online - typing - offline - dead
                            avatarPainter = painterResource(id = R.drawable.unloaded_image), // replace
                            onBack = { finish() }, //previous activity, or just finish and it goes back automatically
                            //if u had a bug with onBack, then previous activity is set to terminate
                            onSettings = { /* TODO: open settings */ } //intent = intent blah blah blah
                        )
                    },
                    bottomBar = {
                        MessageNavBar(
                            onAttach = { /* TODO: opens attachment drop up menu */ },//should contain
                            //image, file, document, audio, prescription, or anything else u think is worth it!
                            onSend = { message ->
                                // TODO: send message to Firebase using liveDate! pain! and suffering!
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ChatArea(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // flexible: takes remaining space above bottom bar
                        )
                        // MessageNavBar is in bottomBar of the Scaffold; keep layout simple
                    }
                }
            }
        }
    }
}

/** composables **/
/**  HAMZA HESHAM MADE THIS SHIT!  **/

@Composable
fun TopNavBar(
    doctorName: String,
    statusText: String,
    avatarPainter: Painter?,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().statusBarsPadding(), //MAGIC, THIS SHIT IS AMAZING
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
        tonalElevation = 0.dp, //useless line, kinda funny! ha!
        shadowElevation = 2.dp //its not useless, its just broken
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back arrow stuck to left edge
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Center area that will contain avatar + name/status and stays centered
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    if (avatarPainter != null) {
                        Image(
                            painter = avatarPainter,
                            contentDescription = "Doctor avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        // fallback circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = doctorName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        val statusColor = when (statusText.lowercase()) {
                            "online" -> MaterialTheme.colorScheme.secondary
                            "writing" -> MaterialTheme.colorScheme.primary
                            "offline" -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor
                        )
                    }
                }
            }

            // Settings (three dots) stuck to right edge
            IconButton(onClick = onSettings, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

/** ChatArea: flexible, shows messages from Firebase (hooked later).
 *  Currently it renders an empty placeholder and a note where to integrate Firebase. */
@Composable
fun ChatArea(modifier: Modifier = Modifier) {
    // TODO: Replace this placeholder with a LazyColumn that observes Firebase messages.
    // Example plan:
    // 1) Create a ViewModel that exposes a Flow / State<List<Message>> from Firebase.
    // 2) CollectAsState in this composable and feed a LazyColumn with items.
    // 3) Scroll to bottom on new messages.
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No messages yet — Firebase integration goes here.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/** Bottom message bar: input, attach button, send button.
 *  We leave the send/attach handlers empty for you to implement the Firebase / image picker logic. */
@Composable
fun MessageNavBar(
    onAttach: () -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
            .navigationBarsPadding()//pushes up when nav bar or keyboard is visible
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttach) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach"
                )
            }

            TextField(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp),
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Type a message...") },
                singleLine = false,
                maxLines = 4,
                trailingIcon = {
                    IconButton(onClick = {
                        if (text.isNotBlank()) {
                            onSend(text.trim())
                            text = ""
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun ChatPreview() {
    CapsuleTheme {
        // Root scaffold contains the top, center (chat) and bottom (message) parts
        Scaffold(
            topBar = {
                TopNavBar(
                    doctorName = "Dr. Anya Sharma",
                    statusText = "online",
                    avatarPainter = painterResource(id = R.drawable.unloaded_image), // replace
                    onBack = {  },
                    onSettings = { /* TODO: open settings */ }
                )
            },
            bottomBar = {
                MessageNavBar(
                    onAttach = { /* TODO: open image picker */ },
                    onSend = { message ->
                        // TODO: send message to Firebase
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ChatArea(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // flexible: takes remaining space above bottom bar
                )
                // MessageNavBar is in bottomBar of the Scaffold; keep layout simple
            }
        }
    }
}