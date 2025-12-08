package com.example.capsule.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.LocaleListCompat
import com.example.capsule.R
import com.example.capsule.data.repository.AuthRepository
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.Teal
import com.example.capsule.ui.theme.WhiteSmoke
import java.util.Locale
import androidx.core.content.edit
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {

    val authRepo = remember { AuthRepository() }

    // removed darkMode state (temporarily hidden)
    // var darkMode by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Notifications state is read from the system (not toggleable directly).
    var notificationsEnabled by remember { mutableStateOf(isNotificationsEnabled(context)) }

    var aboutOpen by remember { mutableStateOf(false) }
    var securitySupportOpen by remember { mutableStateOf(false) }

    // Language dropdown state
    val languages = listOf(
        "English" to "en",
        "العربية" to "ar"
        // add more pairs if needed: "Français" to "fr"
    )
    var expanded by remember { mutableStateOf(false) }
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var selectedLangTag by remember {
        mutableStateOf(prefs.getString("app_lang", Locale.getDefault().language) ?: "en")
    }
    val selectedLangLabel = languages.find { it.second == selectedLangTag }?.first ?: selectedLangTag

    MaterialTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.settings),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
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
                        containerColor = WhiteSmoke,
                        titleContentColor = Teal
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .background(WhiteSmoke)
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {

                Text(
                    text = "Preferences",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF464646),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Notifications card — shows system state and opens notification settings on click
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            // open app notification settings
                            openAppNotificationSettings(context)
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notifications",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (notificationsEnabled) "Enabled — tap to manage" else "Disabled — tap to enable",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        // show a non-interactive switch reflecting system state
                        Switch(checked = notificationsEnabled, onCheckedChange = { /* no-op; open settings on click the card */ })
                    }
                }

                // Language dropdown (ExposedDropdownMenu style)
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 12.dp),
//                    shape = RoundedCornerShape(20.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color.White)
//                ) {
//                    Column(modifier = Modifier.padding(12.dp)) {
//                        Text(
//                            text = "Language",
//                            fontSize = 17.sp,
//                            fontWeight = FontWeight.SemiBold,
//                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
//                        )
//
//                        ExposedDropdownMenuBox(
//                            expanded = expanded,
//                            onExpandedChange = { expanded = !expanded }
//                        ) {
//                            TextField(
//                                readOnly = true,
//                                value = selectedLangLabel,
//                                onValueChange = { },
//                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .background(Color.White)
//                            )
//                            ExposedDropdownMenu(
//                                expanded = expanded,
//                                onDismissRequest = { expanded = false }
//                            ) {
//                                languages.forEach { (label, tag) ->
//                                    DropdownMenuItem(
//                                        text = { Text(label) },
//                                        onClick = {
//                                            expanded = false
//                                            if (tag != selectedLangTag) {
//                                                selectedLangTag = tag
//                                                // save
//                                                prefs.edit {putString("app_lang", tag)}
//                                                // apply locales using AppCompatDelegate (works on many API levels)
//                                                val localeList = LocaleListCompat.forLanguageTags(tag)
//                                                AppCompatDelegate.setApplicationLocales(localeList)
//                                                // try to recreate activity for immediate effect
//                                                (context as? ComponentActivity)?.recreate()
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Support & Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF464646),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { aboutOpen = !aboutOpen }
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "About App",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    AnimatedVisibility(
                        visible = aboutOpen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Version: 0.0.1 Alpha", color = Color.Gray)
                            Text("Developed by: Capsule Team", color = Color.Gray)
                            Text("Developed with love", color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { securitySupportOpen = !securitySupportOpen }
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Outlined.Security,
                            contentDescription = null,
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Security & Privacy",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    AnimatedVisibility(
                        visible = securitySupportOpen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("• Change Password", color = Color.Gray)
                            Text("• Manage Permissions", color = Color.Gray)
                            Text("• Two-Factor Authentication", color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // logout button
                Button(
                    onClick = {
                        authRepo.logout()
                        // Instead of starting new activity, we'll handle this in MainActivity
                        onLogout()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE5E5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text(
                        text = "Log Out",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

/** Utility: check system notification status */
private fun isNotificationsEnabled(context: Context): Boolean {
    val nm = NotificationManagerCompat.from(context)
    return nm.areNotificationsEnabled()
}

/** Utility: open app notification settings so user can enable/disable notifications */
private fun openAppNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        // for Android 8 and above, you can also include the UID, but it's optional:
        // putExtra(Settings.EXTRA_CHANNEL_ID, yourChannelId)
    }
    // fallback to general app settings if ACTION_APP_NOTIFICATION_SETTINGS not handled
    try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallback)
    }
}
//removed dark mode code entirely, it was only composeable so it was meaning less to keep or comment