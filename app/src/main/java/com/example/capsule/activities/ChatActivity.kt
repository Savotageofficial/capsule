package com.example.capsule.activities


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.data.model.Message
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import com.example.capsule.util.formatChatTime
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ReceiverName = intent.getStringExtra("Name")
        val RecieverID = intent.getStringExtra("Id")

        //unused variables (delete)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        setContent {
            ChatApp(
                name = ReceiverName,
                RecId = RecieverID,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatApp(
    modifier: Modifier = Modifier,
    name: String?,
    RecId: String?,
    onBackClick: () -> Unit
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val scope = rememberCoroutineScope()


    // Fetch messages
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
                            recieverId = it.getString("receiverId") ?: ""
                        )
                    }
                    messages = fetchedMessages.filter {(it.recieverId == RecId && it.senderId == currentUser?.uid) || (it.recieverId == currentUser?.uid && it.senderId == RecId)}
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteSmoke)
            .padding(8.dp)
    ) {

        // Top Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = name ?: "",
                    color = Teal,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Cyan,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onBackClick() }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Teal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
        )

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(messages) { item ->
                MessageItem(
                    message = item,
                    isCurrentUser = item.senderId == currentUser?.uid
                )
            }
        }

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                        .fillMaxWidth(),
                    decorationBox = { innerText ->
                        if (messageText.text.isEmpty()) {
                            Text(
                                "Type a message...",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                        innerText()
                    }
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    if (messageText.text.isNotBlank()) {
                        scope.launch {
                            db.collection("messages").add(
                                mapOf(
                                    "message" to messageText.text,
                                    "senderId" to currentUser?.uid,
                                    "timestamp" to Timestamp.now(),
                                    "receiverId" to RecId
                                )
                            )
                            messageText = TextFieldValue("")
                        }
                    }
                },
                modifier = Modifier
                    .padding(end = 4.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal
                )
            ) {
                Text("Send", color = Color.White)
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentUser: Boolean) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser)
            Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isCurrentUser) Teal else Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.message,
                    fontSize = 16.sp,
                    color = if (isCurrentUser) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatChatTime(message.timestamp),
                    fontSize = 11.sp,
                    color = if (isCurrentUser) Color(0xFFE0E0E0) else Color.Gray
                )
            }
        }
    }
}

