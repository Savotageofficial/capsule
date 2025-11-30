// ChatActivity.kt
package com.example.capsule


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.data.model.Message
//import com.example.capsule.model.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val doctorName = intent.getStringExtra("Name")
        val doctorId = intent.getStringExtra("Id")

        val auth =  FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        setContent {
                ChatApp(name = doctorName , RecId = doctorId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatApp(modifier: Modifier = Modifier , name: String? , RecId : String?) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val scope = rememberCoroutineScope()

    // Fetch messages from Firestore
    LaunchedEffect(Unit) {
        db.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null) {
                    val fetchedMessages = snapshot.documents.map {
                        Message(
                            message = it.getString("message") ?: "",
                            senderId = it.getString("senderId") ?: "",
                            timestamp = it.getTimestamp("timestamp")?.toDate().toString(),
                            recieverId = it.getString("recieverId") ?: ""
                        )
                    }
                    messages = fetchedMessages.filter{
                        it.recieverId.equals(RecId)
                    }

                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {


        CenterAlignedTopAppBar(
            modifier = modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Vertical
            ),
            title = {
                if (name != null) {
                    Text(name, overflow = TextOverflow.Ellipsis)
                }
                else{
                    Text("" , overflow = TextOverflow.Ellipsis)
                }
            }
        )


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages) { item ->
                Log.d("trace" , item.recieverId)
                Log.d("trace" , RecId!!)
                MessageItem(
                    message = item,
                    isCurrentUser = item.senderId == currentUser?.uid
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray, CircleShape)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (messageText.text.isNotBlank()) {
                        scope.launch {
                            val messageData = mapOf(
                                "message" to messageText.text,
                                "senderId" to currentUser?.uid,
                                "timestamp" to com.google.firebase.Timestamp.now(),
                                "recieverId" to RecId
                            )
                            db.collection("messages").add(messageData)
                            messageText = TextFieldValue("")
                        }
                    }
                }
            ) {
                Text(text = "Send")
            }
        }
    }

}

@Composable
fun MessageItem(message: Message, isCurrentUser: Boolean) {
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.Gray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp,
            color = backgroundColor
        ) {
            Text(
                text = "${message.message}\n${message.timestamp}",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

//data class Message(
//    val message: String,
//    val senderId: String,
//    val timestamp: String
//)
/*

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val chatViewModel = viewModel<ChatViewModel>()
            val currentUserId = "user1"
            val otherUserId = "user2"

            CapsuleTheme {
                // instead of a compose function, scaffold contains unique compose functions
                Scaffold(
                    topBar = {
                        TopNavBar(
                            doctorName = "Dr name goes here",
                            statusText = "online", //Online - typing - offline - dead
                            avatarPainter = null, // replace
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
                                chatViewModel.sendMessage(currentUserId, message)
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
                            currentUserId = "user1",
                            otherUserId = "user2",
                            chatViewModel = chatViewModel
                        )

                        // MessageNavBar is in bottomBar of the Scaffold; keep layout simple
                    }
                }
            }
        }
    }
}

/** composable **/
/**  HAMZA HESHAM MADE THIS SHIT!  **/

@Composable
fun TopNavBar(
    doctorName: String,
    statusText: String,
    avatarPainter: Painter?, onBack: () -> Unit, //the arrow onclick
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(), //MAGIC, THIS SHIT IS AMAZING
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
        tonalElevation = 0.dp, //useless line, kinda funny! ha!
        shadowElevation = 0.dp //its not useless, its just broken
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
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
                            painter = avatarPainter, contentDescription = "Doctor Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        // fail safe in case image didn't load
                        Image(
                            painter = painterResource(id = R.drawable.doc_prof_unloaded),
                            contentDescription = "Doctor Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(//doctor name display code
                            text = doctorName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        val statusColor = when (statusText.lowercase()) {
                            "online" -> MaterialTheme.colorScheme.secondary
                            "writing" -> MaterialTheme.colorScheme.primary
                            "offline" -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                        Text(//status name display code
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
                    imageVector = Icons.Default.MoreVert, contentDescription = "Options"
                )
            }
        }
    }
}

/** ChatArea: flexible, shows messages from Firebase (hooked later).
 *  Currently it renders an empty placeholder and a note where to integrate Firebase. */
@Composable
fun ChatArea(
    currentUserId: String,
    otherUserId: String,
    chatViewModel: ChatViewModel
) {
    // Start observing messages for this chat
    LaunchedEffect(Unit) {
        chatViewModel.startObserving(currentUserId, otherUserId)
    }

    val messages by chatViewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {

        // Chat messages list
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {
            items(messages) { message ->
                ChatBubble(message = message, isCurrentUser = message.senderId == currentUserId)
            }
        }

        // Auto-scroll to bottom on new messages
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isCurrentUser: Boolean) {
    val bubbleColor = if (isCurrentUser) Color(0xFF19CEFF) else Color(0xFFEFEFEF)
    val textColor = if (isCurrentUser) Color.White else Color.Black

    Box(
        modifier = Modifier.fillMaxWidth()
            .background(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = if (isCurrentUser) 16.dp else 4.dp,
                    topEnd = if (isCurrentUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ),
        contentAlignment = (if (isCurrentUser) Alignment.End else Alignment.Start) as Alignment
    ) {
        Box(
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(
                text = message.message,
                color = textColor
            )
        }
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
        shadowElevation = 2.dp, modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()//pushes up when nav bar or keyboard is visible
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f) //drops down to the bottom of composable
                    .heightIn(min = 56.dp), //doesn't allow it to shrink so much
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors( //all this to remove underline, shitty compose
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Type a message...") },
                singleLine = false,
                maxLines = 4,
                trailingIcon = {
                    IconButton(onClick = onAttach) {
                        Icon(
                            imageVector = Icons.Default.AttachFile, contentDescription = "Attach"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(onClick = {
                    if (text.isNotBlank()) {
                        onSend(text.trim())
                        text = ""
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatPreview() {

    val chatViewModel = viewModel<ChatViewModel>()

    CapsuleTheme {
        // Root scaffold contains the top, center (chat) and bottom (message) parts
        Scaffold(
            topBar = {
                TopNavBar(
                    doctorName = "Dr. Mohamed safwat", //place holder
                    statusText = "online", avatarPainter = null,
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
                    currentUserId = "me",
                    otherUserId = "doctor",
                    chatViewModel = chatViewModel
                )

                // MessageNavBar is in bottomBar of the Scaffold; keep layout simple
            }
        }
    }
}

 */