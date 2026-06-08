package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHubApp(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiChatHistory by viewModel.aiChatHistory.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("dashboard") }
    var showAiChatSheet by remember { mutableStateOf(false) }

    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!isLoggedIn) {
                LoginScreen(
                    onLoginSuccess = { email, password -> viewModel.login(email, password) },
                    onGoogleLogin = { name, email -> viewModel.googleSignIn(name, email) }
                )
            } else {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.app_name),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { showAiChatSheet = true },
                                    modifier = Modifier.testTag("ai_assistant_shortcut_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Assistant",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.logout() }) {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = "Log out",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.navigationBarsPadding()
                        ) {
                            NavigationBarItem(
                                selected = activeTab == "dashboard",
                                onClick = { activeTab = "dashboard" },
                                label = { Text("Home") },
                                icon = { Icon(Icons.Default.Dashboard, "Home") }
                            )
                            NavigationBarItem(
                                selected = activeTab == "academics",
                                onClick = { activeTab = "academics" },
                                label = { Text("Academics") },
                                icon = { Icon(Icons.Default.CalendarMonth, "Academics") }
                            )
                            NavigationBarItem(
                                selected = activeTab == "tasks",
                                onClick = { activeTab = "tasks" },
                                label = { Text("Tasks") },
                                icon = { Icon(Icons.Outlined.Assignment, "Tasks") }
                            )
                            NavigationBarItem(
                                selected = activeTab == "community",
                                onClick = { activeTab = "community" },
                                label = { Text("Peers") },
                                icon = { Icon(Icons.Default.Forum, "Community") }
                            )
                            NavigationBarItem(
                                selected = activeTab == "finances_prep",
                                onClick = { activeTab = "finances_prep" },
                                label = { Text("Prep & Spend") },
                                icon = { Icon(Icons.Default.TrendingUp, "Prep & Spend") }
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showAiChatSheet = true },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.testTag("ai_floating_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Study Assistant",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = activeTab,
                            transitionSpec = {
                                fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                            },
                            label = "MainTabsAnimation"
                        ) { currentTab ->
                            when (currentTab) {
                                "dashboard" -> DashboardScreen(viewModel)
                                "academics" -> AcademicsModule(viewModel)
                                "tasks" -> TasksModule(viewModel)
                                "community" -> CommunityModule(viewModel)
                                "finances_prep" -> FinancesAndPrepModule(viewModel)
                            }
                        }
                    }
                }
            }

            // Gemini Study Assistant Sheet over screen
            if (showAiChatSheet) {
                AiStudyAssistantSheet(
                    chatHistory = aiChatHistory,
                    isLoading = isAiLoading,
                    onSendMessage = { viewModel.askAiStudyHelper(it) },
                    onStartQuiz = { viewModel.triggerAiQuiz(it) },
                    onGeneratePlan = { viewModel.triggerAiStudyPlan(it) },
                    onClose = { showAiChatSheet = false },
                    onClearChat = { viewModel.clearChat() }
                )
            }
        }
    }
}

// ==========================================
// THEME IMPLEMENTATION (To match Material 3 style)
// ==========================================
@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF9EBAF2),
            onPrimary = Color(0xFF042F66),
            primaryContainer = Color(0xFF1E4680),
            onPrimaryContainer = Color(0xFFD6E2FF),
            secondary = Color(0xFFBEC6DC),
            onSecondary = Color(0xFF283141),
            surface = Color(0xFF111418),
            background = Color(0xFF111418),
            onSurface = Color(0xFFE2E2E6),
            onBackground = Color(0xFFE2E2E6),
            surfaceVariant = Color(0xFF43474E),
            onSurfaceVariant = Color(0xFFC3C7D0)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF3B5E99),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFD6E2FF),
            onPrimaryContainer = Color(0xFF001A43),
            secondary = Color(0xFF565F71),
            onSecondary = Color(0xFFFFFFFF),
            surface = Color(0xFFFAF9FD),
            background = Color(0xFFFAF9FD),
            onSurface = Color(0xFF191C20),
            onBackground = Color(0xFF191C20),
            surfaceVariant = Color(0xFFE0E2EC),
            onSurfaceVariant = Color(0xFF43474E)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ==========================================
// AUTH MODULE
// ==========================================
@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Boolean,
    onGoogleLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var forgotPasswordMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp)
            .testTag("auth_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // LOGO
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (forgotPasswordMode) "Reset Password" else "Welcome to StudentHub",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Empower your college progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (successMessage != null) {
                    Text(
                        text = successMessage!!,
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // EMAIL Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("University Email ID") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!forgotPasswordMode) {
                    // PASSWORD Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { forgotPasswordMode = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot password?")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SIGN IN Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.length < 6) {
                                errorMessage = "Password must be at least 6 characters & email must be filled."
                            } else {
                                errorMessage = null
                                onLoginSuccess(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "OR SIGN IN WITH",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // GOOGLE Login Simulation
                    OutlinedButton(
                        onClick = {
                            onGoogleLogin("Pratyush Nayak", "kumarnayakpratyush660@gmail.com")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_login_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Fast Google Authentication")
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))

                    // PASSWORD RESET Button
                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                errorMessage = "Please enter your valid college email ID."
                            } else {
                                errorMessage = null
                                successMessage = "A verification reset link has been dispatched to $email."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send Reset Link", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { forgotPasswordMode = false }) {
                        Text("Back to Sign In")
                    }
                }
            }
        }
    }
}

// ==========================================
// HOME DASHBOARD MODULE
// ==========================================
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val classes by viewModel.classSessions.collectAsStateWithLifecycle()
    val attendance by viewModel.attendanceSubjects.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()

    val currentDay = remember {
        val calendar = Calendar.getInstance()
        SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
    }

    val todayClasses = remember(classes, currentDay) {
        classes.filter { it.dayOfWeek.equals(currentDay, ignoreCase = true) }
    }

    val upcomingTasks = remember(assignments) {
        assignments.filter { !it.isCompleted }.take(3)
    }

    // Calculations
    val totalAttendancePercentage = remember(attendance) {
        if (attendance.isEmpty()) {
            100.0
        } else {
            val totalP = attendance.sumOf { it.presentCount }
            val totalA = attendance.sumOf { it.absentCount }
            val totalCount = totalP + totalA
            if (totalCount == 0) 100.0 else (totalP.toDouble() / totalCount) * 100.0
        }
    }

    var editProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("dashboard_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hi, ${profile.name} 👋",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = profile.college,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = { editProfileDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    ) {
                        Text("Profile Settings", color = MaterialTheme.colorScheme.primaryContainer)
                    }
                }
            }
        }

        // Stats Row Widget
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // GPA Status
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Grade, null, tint = Color(0xFFFFC107))
                        Text("Expected CGPA", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top=4.dp))
                        Text(String.format(Locale.US, "%.2f", profile.expectedCgpa), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    }
                }

                // Attendance Average Widget
                val dropsBelow = totalAttendancePercentage < 75.0
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dropsBelow) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Percent,
                            contentDescription = null,
                            tint = if (dropsBelow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Text("Average Attendance", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top=4.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f%%", totalAttendancePercentage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (dropsBelow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Today's classes List
        item {
            Column {
                Text(
                    text = "Today's Timetable ($currentDay)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (todayClasses.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No classes scheduled for today! 🎉 Enjoy your day off.", textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        todayClasses.forEach { cls ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(android.graphics.Color.parseColor(cls.colorHex)).copy(alpha = 0.15f))
                                    .border(1.dp, Color(android.graphics.Color.parseColor(cls.colorHex)), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp, 40.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(cls.colorHex)))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cls.subject, fontWeight = FontWeight.Bold)
                                    Text("${cls.startTime} - ${cls.endTime} | ${cls.room}", style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(cls.instructor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Assignments List
        item {
            Column {
                Text(
                    text = "Upcoming Deadlines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (upcomingTasks.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("All assignments complete! 🥳 Awesome work.")
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            upcomingTasks.forEachIndexed { idx, assignment ->
                                val colorPr = when(assignment.priority) {
                                    "High" -> Color(0xFFE91E63)
                                    "Medium" -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AssignmentLate,
                                        contentDescription = null,
                                        tint = colorPr
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(assignment.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${assignment.subject} • Due ${assignment.dueDate}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(assignment.priority) }
                                    )
                                }
                                if (idx < upcomingTasks.size - 1) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }

        // Profile Goals / Achievements
        item {
            Column {
                Text(
                    text = "Badges & Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Achievements Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        BadgeItem(
                            title = "DSA Solver",
                            description = "Solved Tree and Graph questions",
                            icon = Icons.Default.ElectricBolt,
                            color = Color(0xFF00E676)
                        )
                    }
                    item {
                        BadgeItem(
                            title = "Budget Guru",
                            description = "Saved personal travel budgets",
                            icon = Icons.Default.Savings,
                            color = Color(0xFFFF9100)
                        )
                    }
                    item {
                        BadgeItem(
                            title = "Perfect Attendee",
                            description = "Maintained excellent Attendance",
                            icon = Icons.Default.Verified,
                            color = Color(0xFF2979FF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Student Goals
                goals.take(3).forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = goal.isCompleted,
                            onCheckedChange = { viewModel.toggleGoal(goal) }
                        )
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (goal.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                            color = if (goal.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (editProfileDialog) {
        var editName by remember { mutableStateOf(profile.name) }
        var editCollege by remember { mutableStateOf(profile.college) }
        var editExpectedCgpa by remember { mutableStateOf(profile.expectedCgpa.toString()) }

        Dialog(onDismissRequest = { editProfileDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Edit Student Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editCollege,
                        onValueChange = { editCollege = it },
                        label = { Text("College Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editExpectedCgpa,
                        onValueChange = { editExpectedCgpa = it },
                        label = { Text("Expected Target CGPA") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { editProfileDialog = false }) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val cgpaVal = editExpectedCgpa.toDoubleOrNull() ?: 9.0
                                viewModel.updateProfile(editName, editCollege, cgpaVal)
                                editProfileDialog = false
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItem(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(180.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            Text(description, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
    }
}

// ==========================================
// ACADEMICS MODULE (Classes, Attendance, CGPA)
// ==========================================
@Composable
fun AcademicsModule(viewModel: MainViewModel) {
    var subTab by remember { mutableStateOf("timetable") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = if (subTab == "timetable") 0 else if (subTab == "attendance") 1 else 2) {
            Tab(selected = subTab == "timetable", onClick = { subTab = "timetable" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Timetable")
                }
            }
            Tab(selected = subTab == "attendance", onClick = { subTab = "attendance" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Percent, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Attendance")
                }
            }
            Tab(selected = subTab == "cgpa", onClick = { subTab = "cgpa" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Grade, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CGPA")
                }
            }
        }

        when (subTab) {
            "timetable" -> TimetableSubScreen(viewModel)
            "attendance" -> AttendanceSubScreen(viewModel)
            "cgpa" -> CgpaSubScreen(viewModel)
        }
    }
}

@Composable
fun TimetableSubScreen(viewModel: MainViewModel) {
    val classes by viewModel.classSessions.collectAsStateWithLifecycle()
    var selectedDay by remember { mutableStateOf("Monday") }
    var showDialog by remember { mutableStateOf(false) }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Toggle Day Row
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { d ->
                FilterChip(
                    selected = selectedDay == d,
                    onClick = { selectedDay = d },
                    label = { Text(d) }
                )
            }
        }

        val filteredClasses = classes.filter { it.dayOfWeek == selectedDay }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedDay's Classes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Class")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredClasses.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No classes scheduled for $selectedDay. ✨ Enjoy study time!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("timetable_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredClasses) { cls ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(android.graphics.Color.parseColor(cls.colorHex)).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(cls.colorHex)))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cls.subject, fontWeight = FontWeight.Bold)
                                Text("${cls.startTime} - ${cls.endTime} | ${cls.room}", style = MaterialTheme.typography.bodySmall)
                                Text("Instr: ${cls.instructor}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            IconButton(onClick = { viewModel.removeClassSession(cls) }) {
                                Icon(Icons.Default.Delete, "Remove class", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var subject by remember { mutableStateOf("") }
        var start by remember { mutableStateOf("09:00 AM") }
        var end by remember { mutableStateOf("10:30 AM") }
        var room by remember { mutableStateOf("") }
        var inst by remember { mutableStateOf("") }
        var selColor by remember { mutableStateOf("#3F51B5") }

        val colors = listOf("#3F51B5", "#4CAF50", "#FF9800", "#E91E63", "#00BCD4", "#9C27B0")

        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Add Timetable Class", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = start,
                            onValueChange = { start = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = end,
                            onValueChange = { end = it },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Room No") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inst,
                        onValueChange = { inst = it },
                        label = { Text("Professor/Instructor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Label Color", style = MaterialTheme.typography.labelSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        colors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(c)))
                                    .border(
                                        width = if (selColor == c) 3.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { selColor = c }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (subject.isNotBlank()) {
                                    viewModel.addClassSession(subject, selectedDay, start, end, room, inst, selColor)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSubScreen(viewModel: MainViewModel) {
    val attendance by viewModel.attendanceSubjects.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Attendance Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Maintain at least 75% for exam eligibility", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Button(onClick = { showDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Subject")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (attendance.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No subjects registered for attendance tracker.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(attendance) { sub ->
                    val percentage = sub.getPercentage()
                    val dropsBelow = percentage < 75.0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dropsBelow) MaterialTheme.colorScheme.errorContainer.copy(alpha=0.3f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sub.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = "Present: ${sub.presentCount} | Absent: ${sub.absentCount} of ${sub.presentCount + sub.absentCount} Total classes",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    text = String.format(Locale.US, "%.1f%%", percentage),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = if (dropsBelow) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Attendance bar indicator
                            LinearProgressIndicator(
                                progress = (percentage / 100.0).toFloat(),
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = if (dropsBelow) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f)
                            )

                            if (dropsBelow) {
                                Row(
                                    modifier = Modifier.padding(top = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Critical Alert: Drops below 75% system warning!", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Button(
                                        onClick = { viewModel.markAttendance(sub, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Check, null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Present")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.markAttendance(sub, false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Absent")
                                    }
                                }

                                IconButton(onClick = { viewModel.removeAttendanceSubject(sub) }) {
                                    Icon(Icons.Default.Delete, "Remove subject", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var subName by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Track New Course Attendance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject / Course Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (subName.isNotBlank()) {
                                    viewModel.addAttendanceSubject(subName)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Track")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CgpaSubScreen(viewModel: MainViewModel) {
    val semesters by viewModel.semesterGpas.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    // Calc CGPA
    val cumulativeCgpa = remember(semesters) {
        val totalCredits = semesters.sumOf { it.credits }
        if (totalCredits == 0) 0.0 else {
            val totalPoints = semesters.sumOf { it.gpa * it.credits }
            totalPoints / totalCredits
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // CGPA Header Display Widget
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cumulative CGPA", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.US, "%.2f", cumulativeCgpa),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                // Quick projection message
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Based on ${semesters.size} saved academic semesters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha=0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Semester Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { showDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Semester")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (semesters.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No semester logs found. Tap 'Add Semester' to calculate.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(semesters) { sem ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(sem.semesterName, fontWeight = FontWeight.Bold)
                                Text("Credits: ${sem.credits}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("GPA: ${sem.gpa}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.removeSemesterRecord(sem) }) {
                                    Icon(Icons.Default.Delete, "Remove semester record", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var semName by remember { mutableStateOf("") }
        var gpaVal by remember { mutableStateOf("") }
        var creditsVal by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Add Semester Academic Record", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = semName,
                        onValueChange = { semName = it },
                        label = { Text("Semester / Term Name (e.g. Sem 4)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gpaVal,
                        onValueChange = { gpaVal = it },
                        label = { Text("SGPA / GPA (0.00 - 10.00)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = creditsVal,
                        onValueChange = { creditsVal = it },
                        label = { Text("Semester Credits") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                val gpa = gpaVal.toDoubleOrNull()
                                val credits = creditsVal.toIntOrNull()
                                if (semName.isNotBlank() && gpa != null && credits != null) {
                                    viewModel.addSemesterRecord(semName, gpa, credits)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Save Record")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TASKS & NOTES VAULT MODULE
// ==========================================
@Composable
fun TasksModule(viewModel: MainViewModel) {
    var subTab by remember { mutableStateOf("assignments") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = if (subTab == "assignments") 0 else 1) {
            Tab(selected = subTab == "assignments", onClick = { subTab = "assignments" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Assignment, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Assignments")
                }
            }
            Tab(selected = subTab == "notes", onClick = { subTab = "notes" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FolderZip, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Notes Vault")
                }
            }
        }

        when (subTab) {
            "assignments" -> AssignmentsSubScreen(viewModel)
            "notes" -> NotesVaultSubScreen(viewModel)
        }
    }
}

@Composable
fun AssignmentsSubScreen(viewModel: MainViewModel) {
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Assignment Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Monitor university deadlines and project deliverables", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Button(onClick = { showDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Task")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (assignments.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No university assignments added yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("assignment_list_container"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(assignments) { task ->
                    val borderCl = when(task.priority) {
                        "High" -> Color(0xFFE91E63)
                        "Medium" -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (task.isCompleted) Color.Gray.copy(alpha=0.3f) else borderCl, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleAssignmentCompleted(task) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("${task.subject} • Due ${task.dueDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }

                                Badge(
                                    containerColor = borderCl.copy(alpha=0.15f),
                                    contentColor = borderCl,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(task.priority, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }

                                IconButton(onClick = { viewModel.removeAssignment(task) }) {
                                    Icon(Icons.Default.Delete, "Delete Assignment", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            if (task.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.8f),
                                    modifier = Modifier.padding(start = 48.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var title by remember { mutableStateOf("") }
        var subject by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("Medium") }
        var desc by remember { mutableStateOf("") }

        val priorities = listOf("Low", "Medium", "High")

        Dialog(onDismissRequest = { showDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text("Add Academic Assignment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Course") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (e.g. Jun 15, 2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Priority Level", style = MaterialTheme.typography.labelSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        priorities.forEach { pr ->
                            FilterChip(
                                selected = priority == pr,
                                onClick = { priority = pr },
                                label = { Text(pr) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Optional Notes / Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (title.isNotBlank() && subject.isNotBlank()) {
                                    viewModel.addAssignment(title, subject, dueDate, priority, desc)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Create Task")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesVaultScreen(viewModel: MainViewModel) {
    // Deprecated shortcut mapping, using correct delegated binding below
}

@Composable
fun NotesVaultSubScreen(viewModel: MainViewModel) {
    val notes by viewModel.studyNotes.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredNotes = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) notes else {
            notes.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.subject.contains(searchQuery, ignoreCase = true) ||
                        it.content.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notes Vault", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Organize course materials, PDFs & research logs", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Button(onClick = { showDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.CloudUpload, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Upload Document")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by subject or keyword...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No vault documents match your criteria.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("notes_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotes) { doc ->
                    val isPdf = doc.fileType == "PDF"
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isPdf) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPdf) Icons.Default.PictureAsPdf else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isPdf) Color(0xFFD32F2F) else Color(0xFF388E3C)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.title, fontWeight = FontWeight.Bold)
                                Text(doc.subject, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(doc.content, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(onClick = { viewModel.removeStudyNote(doc) }) {
                                    Icon(Icons.Default.Delete, "Delete Notes", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteSubject by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }
        var isPdfType by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text("Secure Cloud Document Upload", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Simulates local storage of academic resources", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Document Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noteSubject,
                        onValueChange = { noteSubject = it },
                        label = { Text("Course / Subject Module") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("File Format: ")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(selected = !isPdfType, onClick = { isPdfType = false })
                        Text("Plain Text")
                        Spacer(modifier = Modifier.width(12.dp))
                        RadioButton(selected = isPdfType, onClick = { isPdfType = true })
                        Text("PDF Meta")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Note content or document summary outline") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (noteTitle.isNotBlank() && noteSubject.isNotBlank()) {
                                    val typeStr = if (isPdfType) "PDF" else "Note"
                                    viewModel.addStudyNote(noteTitle, noteSubject, noteContent, typeStr)
                                    showDialog = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.CloudUpload, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STUDENT COMMUNITY (PEERS REDDIT FORUM)
// ==========================================
@Composable
fun CommunityModule(viewModel: MainViewModel) {
    val posts by viewModel.communityPosts.collectAsStateWithLifecycle()
    var activeCategory by remember { mutableStateOf("All") }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "General", "Placements", "Doubts", "Resources")

    val filteredPosts = remember(posts, activeCategory) {
        if (activeCategory == "All") posts else posts.filter { it.category == activeCategory }
    }

    var selectedPostForCommentDialog by remember { mutableStateOf<CommunityPost?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Feed Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Peer Community Feed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Like mini-Reddit for college peers", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Button(
                onClick = { showCreatePostDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Post Doubt")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Community Categories selector row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = activeCategory == cat,
                    onClick = { activeCategory = cat },
                    label = { Text(cat) }
                )
            }
        }

        if (filteredPosts.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No posts in this peer sub-forum yet. Start the chat!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("community_forum_feed"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPosts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header Meta
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = post.authorName.take(1).uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${post.authorName} • sub/${post.category}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Text(
                                    text = SimpleDateFormat("MMM dd, h:mm a", Locale.US).format(Date(post.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = post.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.9f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive voting row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 6.dp)
                                ) {
                                    IconButton(onClick = { viewModel.upvotePost(post) }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = "Upvote",
                                            tint = if (post.isUpvoted) Color(0xFFFF5722) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = post.upvotes.toString(),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        color = if (post.isUpvoted) Color(0xFFFF5722) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { selectedPostForCommentDialog = post },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Comment,
                                        contentDescription = "Comments",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Comments / Discuss", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreatePostDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("General") }

        Dialog(onDismissRequest = { showCreatePostDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text("Post to Peer Community", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("What is your query or contribution?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Sub-Category", style = MaterialTheme.typography.labelSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        val subCats = listOf("General", "Placements", "Doubts", "Resources")
                        subCats.forEach { sCat ->
                            FilterChip(
                                selected = sCat == category,
                                onClick = { category = sCat },
                                label = { Text(sCat) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Explanation details / resources links...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showCreatePostDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    viewModel.createCommunityPost(title, content, category)
                                    showCreatePostDialog = false
                                }
                            }
                        ) {
                            Text("Post Now")
                        }
                    }
                }
            }
        }
    }

    selectedPostForCommentDialog?.let { post ->
        CommentsPostDialog(
            post = post,
            commentsFlow = viewModel.getCommentsForPost(post.id),
            onAddComment = { viewModel.addCommentToPost(post.id, it) },
            onDismiss = { selectedPostForCommentDialog = null }
        )
    }
}

@Composable
fun CommentsPostDialog(
    post: CommunityPost,
    commentsFlow: kotlinx.coroutines.flow.Flow<List<CommunityComment>>,
    onAddComment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val comments by commentsFlow.collectAsStateWithLifecycle(emptyList())
    var commentInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Discussion Room", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                // Post header snippet
                Text(post.title, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Text(post.content, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top=4.dp, bottom = 12.dp))

                HorizontalDivider()

                // List comments
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (comments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No peer comments yet. Start the conversation!", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
                        ) {
                            items(comments) { comment ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.6f))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(comment.authorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = SimpleDateFormat("h:mm a", Locale.US).format(Date(comment.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(comment.content, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                // Writing comments row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Write high-value reply...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                onAddComment(commentInput)
                                commentInput = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, null)
                    }
                }
            }
        }
    }
}

// ==========================================
// EXPENSE TRACKER & DSA PLACEMENT PREPARATION
// ==========================================
@Composable
fun FinancesAndPrepModule(viewModel: MainViewModel) {
    var subTab by remember { mutableStateOf("prep") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = if (subTab == "prep") 0 else 1) {
            Tab(selected = subTab == "prep", onClick = { subTab = "prep" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Placement Prep")
                }
            }
            Tab(selected = subTab == "expenses", onClick = { subTab = "expenses" }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Expense Tracker")
                }
            }
        }

        when (subTab) {
            "prep" -> PlacementPrepSubScreen(viewModel)
            "expenses" -> ExpensesSubScreen(viewModel)
        }
    }
}

@Composable
fun PlacementPrepSubScreen(viewModel: MainViewModel) {
    val dsaItems by viewModel.placementDsa.collectAsStateWithLifecycle()
    var selectedTopic by remember { mutableStateOf("Arrays") }
    var showDialog by remember { mutableStateOf(false) }

    val topics = listOf("Arrays", "Strings", "Linked List", "Trees/Graphs", "Dynamic Programming")

    val totalDsaCount = dsaItems.size
    val completedDsaCount = dsaItems.count { it.isCompleted }
    val progressPercent = if (totalDsaCount == 0) 100f else (completedDsaCount.toFloat() / totalDsaCount)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // DSA Overall Progress Gauge widget
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(18.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("DSA Track Milestone", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text("Solved: $completedDsaCount of $totalDsaCount standard interview questions", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = progressPercent,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF00E676),
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Topic select chips list
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            items(topics) { t ->
                FilterChip(
                    selected = selectedTopic == t,
                    onClick = { selectedTopic = t },
                    label = { Text(t) }
                )
            }
        }

        // Action add problem
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$selectedTopic Challenges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Problem")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val filteredDsa = dsaItems.filter { it.topic == selectedTopic }

        if (filteredDsa.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No problems mapped to $selectedTopic in database.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("dsa_checklist"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDsa) { dsa ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = dsa.isCompleted,
                                    onCheckedChange = { viewModel.toggleDsaCompleted(dsa) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dsa.problemName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = if (dsa.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    color = if (dsa.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(onClick = { viewModel.removeDsaProblem(dsa) }) {
                                Icon(Icons.Default.Delete, "Delete problem", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var problemNameInput by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Add Placement Challenge problem", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = problemNameInput,
                        onValueChange = { problemNameInput = it },
                        label = { Text("Problem Name (e.g. Reverse Linked List)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (problemNameInput.isNotBlank()) {
                                    viewModel.addDsaProblem(selectedTopic, problemNameInput)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Track Problem")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpensesSubScreen(viewModel: MainViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    val totalSpent = remember(expenses) { expenses.sumOf { it.amount } }

    // Grouping by categories
    val categoryTotals = remember(expenses) {
        val groups = expenses.groupBy { it.category }
        listOf("Food", "Hostel", "Transport", "Personal").map { cat ->
            cat to (groups[cat]?.sumOf { it.amount } ?: 0.0)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Overall spent widget
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total Spending This Month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.US, "INR %.2f", totalSpent),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simple inline category spending bar chart visual
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Spending Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(10.dp))
                categoryTotals.forEach { (cat, amt) ->
                    val proportion = if (totalSpent == 0.0) 0f else (amt / totalSpent).toFloat()
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cat, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(String.format(Locale.US, "INR %.1f", amt), style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = proportion,
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                            color = when (cat) {
                                "Food" -> Color(0xFFE91E63)
                                "Hostel" -> Color(0xFF3F51B5)
                                "Transport" -> Color(0xFF00BCD4)
                                else -> Color(0xFF8BC34A)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Historic Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Expense")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No finance expense items in database.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().testTag("expense_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses) { exp ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (exp.category) {
                                                "Food" -> Color(0xFFFFEBEE)
                                                "Hostel" -> Color(0xFFE8EAF6)
                                                "Transport" -> Color(0xFFE0F7FA)
                                                else -> Color(0xFFF1F8E9)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when(exp.category) {
                                            "Food" -> Icons.Default.Restaurant
                                            "Hostel" -> Icons.Default.Home
                                            "Transport" -> Icons.Default.DirectionsBus
                                            else -> Icons.Default.LocalMall
                                        },
                                        contentDescription = null,
                                        tint = when(exp.category) {
                                            "Food" -> Color(0xFFD32F2F)
                                            "Hostel" -> Color(0xFF303F9F)
                                            "Transport" -> Color(0xFF0097A7)
                                            else -> Color(0xFF689F38)
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(exp.description, fontWeight = FontWeight.SemiBold)
                                    Text(exp.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format(Locale.US, "INR %.2f", exp.amount),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.deleteExpense(exp) }) {
                                    Icon(Icons.Default.Delete, "Delete expense", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var amountInput by remember { mutableStateOf("") }
        var descInput by remember { mutableStateOf("") }
        var categorySelected by remember { mutableStateOf("Food") }

        val categories = listOf("Food", "Hostel", "Transport", "Personal")

        Dialog(onDismissRequest = { showDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text("Add Budget Spend Item", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount (INR)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Spent Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Budget Category", style = MaterialTheme.typography.labelSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = categorySelected == cat,
                                onClick = { categorySelected = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                val amtNum = amountInput.toDoubleOrNull()
                                if (amtNum != null && descInput.isNotBlank()) {
                                    viewModel.addExpense(amtNum, categorySelected, descInput)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Track Spent")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// GEMINI API AI STUDY ASSISTANT SHEET SCREEN
// ==========================================
@Composable
fun AiStudyAssistantSheet(
    chatHistory: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onStartQuiz: (String) -> Unit,
    onGeneratePlan: (String) -> Unit,
    onClose: () -> Unit,
    onClearChat: () -> Unit
) {
    var textMessage by remember { mutableStateOf("") }
    var selectedSubjectPrompt by remember { mutableStateOf("Data Structures") }

    val shortcutSubjects = listOf("Data Structures", "Database Systems", "Software Eng", "Theory of Comp")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(top = 40.dp)
            .testTag("ai_assistant_dialog_curtain"),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Sheet Header Controls
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Campus Co-Pilot", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                Row {
                    IconButton(onClick = onClearChat) {
                        Icon(Icons.Default.Refresh, "Clear chat threads", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onClose, modifier = Modifier.testTag("close_ai_sheet_button")) {
                        Icon(Icons.Default.Close, "Dismiss AI model helper")
                    }
                }
            }

            HorizontalDivider()

            // Subject helper triggers row
            Text(
                text = "Select Course Assistant Module",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(shortcutSubjects) { sj ->
                    FilterChip(
                        selected = selectedSubjectPrompt == sj,
                        onClick = { selectedSubjectPrompt = sj },
                        label = { Text(sj) }
                    )
                }
            }

            // Quick command actions row
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onStartQuiz(selectedSubjectPrompt) },
                    modifier = Modifier.weight(1f).testTag("quick_quiz_trigger_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Quiz, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Interactive Quiz", fontSize = 11.sp, maxLines = 1)
                }

                OutlinedButton(
                    onClick = { onGeneratePlan(selectedSubjectPrompt) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("7-Day Study Plan", fontSize = 11.sp, maxLines = 1)
                }
            }

            HorizontalDivider()

            // Main chat logs view
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().testTag("ai_chat_history_list"),
                    reverseLayout = false,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatHistory) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                            bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                        )
                                    )
                                    .background(
                                        if (msg.isUser) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (msg.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thinking of answers...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Text inputs bar
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textMessage,
                    onValueChange = { textMessage = it },
                    placeholder = { Text("Ask academic questions...") },
                    maxLines = 2,
                    modifier = Modifier.weight(1f).testTag("ai_command_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (textMessage.isNotBlank()) {
                            onSendMessage(textMessage)
                            textMessage = ""
                        }
                    },
                    modifier = Modifier.testTag("submit_ai_command_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send request")
                }
            }
        }
    }
}
