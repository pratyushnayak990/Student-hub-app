package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserProfile(
    val name: String = "Pratyush Nayak",
    val email: String = "kumarnayakpratyush660@gmail.com",
    val regNo: String = "CS20268841",
    val college: String = "State Technical Institute of Technology",
    val avatarUrl: String = "",
    val expectedCgpa: Double = 9.0
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    // --- Authentication State ---
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // --- Reactive flows from Database ---
    val classSessions: StateFlow<List<ClassSession>>
    val attendanceSubjects: StateFlow<List<AttendanceSubject>>
    val assignments: StateFlow<List<Assignment>>
    val expenses: StateFlow<List<Expense>>
    val communityPosts: StateFlow<List<CommunityPost>>
    val studyNotes: StateFlow<List<StudyNote>>
    val placementDsa: StateFlow<List<PlacementDsa>>
    val semesterGpas: StateFlow<List<SemesterGpa>>
    val goals: StateFlow<List<StudentGoal>>

    // --- AI Chat Helper State ---
    private val _aiChatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Hi! I'm your StudentHub AI assistant. Ask me questions about your curriculum, upload notes text, or let's generate quizzes and flashcards!", false)
        )
    )
    val aiChatHistory: StateFlow<List<ChatMessage>> = _aiChatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())

        // Wire database streams to stateflows
        val scope = viewModelScope

        classSessions = repository.allClassSessions.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        attendanceSubjects = repository.allAttendanceSubjects.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        assignments = repository.allAssignments.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        expenses = repository.allExpenses.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        communityPosts = repository.allCommunityPosts.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        studyNotes = repository.allStudyNotes.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        placementDsa = repository.allPlacementDsa.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        semesterGpas = repository.allSemesterGpas.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        goals = repository.allGoals.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Populate with rich template data if database is fresh
        seedDefaultDataIfEmpty()
    }

    // --- Authentication ---
    fun login(email: String, password: String): Boolean {
        if (email.isNotBlank() && password.length >= 6) {
            _userProfile.update { it.copy(email = email) }
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun googleSignIn(name: String, email: String) {
        _userProfile.update { it.copy(name = name, email = email) }
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun updateProfile(name: String, college: String, expectedCgpa: Double) {
        _userProfile.update { it.copy(name = name, college = college, expectedCgpa = expectedCgpa) }
    }

    // --- Seed Data Helper ---
    private fun seedDefaultDataIfEmpty() {
        viewModelScope.launch {
            // Wait brief moment for flow inspection or just check database synchronously if possible
            // Using a flat delay or check if records are empty
            classSessions.firstOrNull()?.let { list ->
                if (list.isNotEmpty()) return@launch
            }

            // If empty, insert rich starting academic values
            launch {
                repository.insertClassSession(ClassSession(0, "Data Structures & Algorithms", "Monday", "09:00 AM", "10:30 AM", "Room 403", "Prof. Sharma", "#3F51B5"))
                repository.insertClassSession(ClassSession(0, "Database Systems & SQL", "Monday", "11:00 AM", "12:30 PM", "Lab 2", "Dr. Mehta", "#4CAF50"))
                repository.insertClassSession(ClassSession(0, "Software Engineering", "Tuesday", "10:00 AM", "11:30 AM", "Audi-A", "Prof. Davis", "#FF9800"))
                repository.insertClassSession(ClassSession(0, "Theory of Computation", "Wednesday", "09:00 AM", "10:30 AM", "Room 101", "Dr. Roy", "#E91E63"))
                repository.insertClassSession(ClassSession(0, "Web Development", "Thursday", "01:00 PM", "02:30 PM", "Lab 5", "Prof. Gupta", "#00BCD4"))
            }

            launch {
                repository.insertAttendanceSubject(AttendanceSubject(0, "Data Structures & Algorithms", 15, 3)) // 83%
                repository.insertAttendanceSubject(AttendanceSubject(0, "Database Systems & SQL", 12, 4)) // 75%
                repository.insertAttendanceSubject(AttendanceSubject(0, "Software Engineering", 10, 1)) // 90%
                repository.insertAttendanceSubject(AttendanceSubject(0, "Theory of Computation", 8, 3)) // 72% (warning < 75!)
                repository.insertAttendanceSubject(AttendanceSubject(0, "Web Development", 14, 0)) // 100%
            }

            launch {
                repository.insertAssignment(Assignment(0, "Implement AVL Tree Red-Black Ops", "Data Structures & Algorithms", "Jun 12, 2026", "High", false, "Read chapter 4 and write working Kotlin source for self-balancing search trees."))
                repository.insertAssignment(Assignment(0, "Normalization Worksheet #3", "Database Systems & SQL", "Jun 16, 2026", "Medium", false, "Decompose schemas from 1NF down to BCNF and query key metrics."))
                repository.insertAssignment(Assignment(0, "Draft Software SRS Document", "Software Engineering", "Jun 20, 2026", "Low", true, "Compose detailed SRS diagrams for the project proposal."))
            }

            launch {
                repository.insertExpense(Expense(0, 450.0, "Hostel", "Monthly room maintenance fee"))
                repository.insertExpense(Expense(0, 120.0, "Food", "Spicy Ramen & drinks at Cafeteria"))
                repository.insertExpense(Expense(0, 50.0, "Transport", "Metro smart card recharge"))
                repository.insertExpense(Expense(0, 80.0, "Personal", "Bought College branded journal & pen"))
            }

            launch {
                repository.insertCommunityPost(CommunityPost(0, "How to balance DSA practice and semester exams?", "Hey guys, finding it tough to maintain standard 5-6 problems/day on LeetCode while tracking midterms. Any weekly schedules that worked for you?", "General", "Aman Verma", upvotes = 12))
                repository.insertCommunityPost(CommunityPost(0, "FAANG Placement Syllabus & Cheat Sheet", "I compiled the essential recursion, tree DP and graph patterns frequently asked in interviews. Attached a downloadable doc with working templates!", "Placements", "Anjali Sen_CS", upvotes = 28))
                repository.insertCommunityPost(CommunityPost(0, "Stuck in 2NF to 3NF database design", "Can someone explain why transient functional dependency triggers 3NF violations with a small example?", "Doubts", "Pratyush Nayak", upvotes = 5))
            }

            // Insert initial comments on community posts
            launch {
                repository.insertCommunityComment(CommunityComment(0, 1, "Highly recommend prioritizing DSA in the morning before class, and keeping regular labs for evenings!", "Sanjay_SE"))
                repository.insertCommunityComment(CommunityComment(0, 1, "Agreed. Consistency, even small daily chunks, always beats weekend cramming.", "Rohit Gupta"))
                repository.insertCommunityComment(CommunityComment(0, 2, "Wow! These recursion templates are highly optimal. Bookmarked!", "Neha J"))
            }

            launch {
                repository.insertStudyNote(StudyNote(0, "BCNF vs 3NF Cheatsheet", "Database Systems", "3NF allows prime attribute dependency on non-keys, whereas BCNF strictly forbids it. If X -> Y, then X must be a super key in BCNF.", "Note"))
                repository.insertStudyNote(StudyNote(0, "Big O Cheat Sheet.pdf", "Data Structures", "Quick reference list for standard array, search tree, heap, sorting algorithms log boundaries.", "PDF"))
            }

            launch {
                repository.insertPlacementDsa(PlacementDsa(0, "Arrays", "Two Sum", true))
                repository.insertPlacementDsa(PlacementDsa(0, "Arrays", "Container With Most Water", false))
                repository.insertPlacementDsa(PlacementDsa(0, "Linked List", "Reverse Linked List", true))
                repository.insertPlacementDsa(PlacementDsa(0, "Trees/Graphs", "Inorder Traversal", true))
                repository.insertPlacementDsa(PlacementDsa(0, "Dynamic Programming", "Climbing Stairs", false))
            }

            launch {
                repository.insertSemesterGpa(SemesterGpa(0, "Semester 1", 8.8, 22))
                repository.insertSemesterGpa(SemesterGpa(0, "Semester 2", 9.2, 20))
                repository.insertSemesterGpa(SemesterGpa(0, "Semester 3", 8.9, 24))
            }

            launch {
                repository.insertGoal(StudentGoal(0, "Score > 9 CGPA this semester", false))
                repository.insertGoal(StudentGoal(0, "Complete 200 DSA Problems", true))
                repository.insertGoal(StudentGoal(0, "Achieve 95% attendance profile", false))
            }
        }
    }

    // --- Action Methods ---

    // Classes / Timetable
    fun addClassSession(subject: String, day: String, start: String, end: String, room: String, inst: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertClassSession(ClassSession(0, subject, day, start, end, room, inst, colorHex))
        }
    }

    fun removeClassSession(session: ClassSession) {
        viewModelScope.launch {
            repository.deleteClassSession(session)
        }
    }

    // Attendance
    fun addAttendanceSubject(name: String) {
        viewModelScope.launch {
            repository.insertAttendanceSubject(AttendanceSubject(0, name, 0, 0))
        }
    }

    fun markAttendance(subject: AttendanceSubject, isPresent: Boolean) {
        viewModelScope.launch {
            val updated = if (isPresent) {
                subject.copy(presentCount = subject.presentCount + 1)
            } else {
                subject.copy(absentCount = subject.absentCount + 1)
            }
            repository.updateAttendanceSubject(updated)
        }
    }

    fun removeAttendanceSubject(subject: AttendanceSubject) {
        viewModelScope.launch {
            repository.deleteAttendanceSubject(subject)
        }
    }

    // Assignments
    fun addAssignment(title: String, subject: String, dueDate: String, priority: String, desc: String) {
        viewModelScope.launch {
            repository.insertAssignment(Assignment(0, title, subject, dueDate, priority, false, desc))
        }
    }

    fun toggleAssignmentCompleted(assignment: Assignment) {
        viewModelScope.launch {
            val updated = assignment.copy(isCompleted = !assignment.isCompleted)
            repository.updateAssignment(updated)
        }
    }

    fun removeAssignment(assignment: Assignment) {
        viewModelScope.launch {
            repository.deleteAssignment(assignment)
        }
    }

    // Expenses
    fun addExpense(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            repository.insertExpense(Expense(0, amount, category, description))
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Community Forum Posts
    fun createCommunityPost(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.insertCommunityPost(
                CommunityPost(
                    title = title,
                    content = content,
                    category = category,
                    authorName = _userProfile.value.name
                )
            )
        }
    }

    fun upvotePost(post: CommunityPost) {
        viewModelScope.launch {
            val isAlreadyUpvoted = post.isUpvoted
            val updated = post.copy(
                upvotes = if (isAlreadyUpvoted) post.upvotes - 1 else post.upvotes + 1,
                isUpvoted = !isAlreadyUpvoted
            )
            repository.updateCommunityPost(updated)
        }
    }

    fun addCommentToPost(postId: Int, content: String) {
        viewModelScope.launch {
            repository.insertCommunityComment(
                CommunityComment(
                    postId = postId,
                    content = content,
                    authorName = _userProfile.value.name
                )
            )
        }
    }

    fun getCommentsForPost(postId: Int): Flow<List<CommunityComment>> {
        return repository.getCommentsForPost(postId)
    }

    // Notes Vault
    fun addStudyNote(title: String, subject: String, content: String, type: String) {
        viewModelScope.launch {
            repository.insertStudyNote(StudyNote(0, title, subject, content, type))
        }
    }

    fun removeStudyNote(note: StudyNote) {
        viewModelScope.launch {
            repository.deleteStudyNote(note)
        }
    }

    // Placement Prep (DSA / Aptitude)
    fun addDsaProblem(topic: String, name: String) {
        viewModelScope.launch {
            repository.insertPlacementDsa(PlacementDsa(0, topic, name, false))
        }
    }

    fun toggleDsaCompleted(dsa: PlacementDsa) {
        viewModelScope.launch {
            val updated = dsa.copy(isCompleted = !dsa.isCompleted)
            repository.updatePlacementDsa(updated)
        }
    }

    fun removeDsaProblem(dsa: PlacementDsa) {
        viewModelScope.launch {
            repository.deletePlacementDsa(dsa)
        }
    }

    // CGPA Calculation / Semester GPA
    fun addSemesterRecord(name: String, gpa: Double, credits: Int) {
        viewModelScope.launch {
            repository.insertSemesterGpa(SemesterGpa(0, name, gpa, credits))
        }
    }

    fun removeSemesterRecord(semesterGpa: SemesterGpa) {
        viewModelScope.launch {
            repository.deleteSemesterGpa(semesterGpa)
        }
    }

    // Profile Goals
    fun addGoal(desc: String) {
        viewModelScope.launch {
            repository.insertGoal(StudentGoal(0, desc, false))
        }
    }

    fun toggleGoal(goal: StudentGoal) {
        viewModelScope.launch {
            val updated = goal.copy(isCompleted = !goal.isCompleted)
            repository.updateGoal(updated)
        }
    }

    fun removeGoal(goal: StudentGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // --- AI Chat Study Helper Calls ---
    fun askAiStudyHelper(prompt: String) {
        if (prompt.isBlank()) return

        _aiChatHistory.update { it + ChatMessage(prompt, true) }
        _isAiLoading.value = true

        viewModelScope.launch {
            val systemSystemPrompt = """
                You are StudentHub AI Assistant, an elite academic advisor, study coach, and tutor for college students.
                Provide extremely clear, structured, well-formatted answers to students.
                Use formatting, lists, and clear definitions. Keep responses helpful, positive, and accurate.
                If they request a quiz, structure it as 3 multiple-choice questions with answers clearly shown at the end.
                If they request flashcards, format them as clean front/back pairs.
                If they ask for a study plan, structure it as a daily/weekly schedule.
            """.trimIndent()

            val aiResultText = GeminiClient.askGemini(prompt, systemSystemPrompt)
            _aiChatHistory.update { it + ChatMessage(aiResultText, false) }
            _isAiLoading.value = false
        }
    }

    // Quick helpers for pre-baked triggers (quizzes, study plans, etc.)
    fun triggerAiStudyPlan(subject: String) {
        askAiStudyHelper("Generate a high-efficiency 7-day study plan to master $subject for college semester exams.")
    }

    fun triggerAiQuiz(subject: String) {
        askAiStudyHelper("Create a 3-question multiple choice quiz with answers on $subject to help me test my knowledge.")
    }

    fun triggerAiFlashcards(topic: String) {
        askAiStudyHelper("Generate 5 essential interactive study flashcards covering key terms in $topic.")
    }

    fun clearChat() {
        _aiChatHistory.value = listOf(
            ChatMessage("Hi! I'm your StudentHub AI assistant. Ask me questions about your curriculum, upload notes text, or let's generate quizzes and flashcards!", false)
        )
    }
}
