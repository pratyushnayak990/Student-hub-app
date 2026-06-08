package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_sessions")
data class ClassSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val startTime: String, // e.g. "09:00 AM"
    val endTime: String,   // e.g. "10:00 AM"
    val room: String,
    val instructor: String,
    val colorHex: String   // e.g. "#FF5722"
)

@Entity(tableName = "attendance_subjects")
data class AttendanceSubject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val presentCount: Int = 0,
    val absentCount: Int = 0
) {
    fun getPercentage(): Double {
        val total = presentCount + absentCount
        if (total == 0) return 100.0 // Avoid 0-division, default empty is 100%
        return (presentCount.toDouble() / total) * 100.0
    }
}

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val dueDate: String, // e.g., "Jun 15, 2026"
    val priority: String, // "Low", "Medium", "High"
    val isCompleted: Boolean = false,
    val description: String = ""
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String, // "Food", "Hostel", "Transport", "Personal"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // "General", "Placements", "Doubts", "Resources"
    val authorName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val isUpvoted: Boolean = false
)

@Entity(tableName = "community_comments")
data class CommunityComment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val content: String,
    val authorName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val content: String, // Actual note content or summary
    val fileType: String, // "PDF", "Note"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "placement_dsa")
data class PlacementDsa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String, // "Arrays", "Strings", "Linked List", "Dynamic Programming", "Trees/Graphs"
    val problemName: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "semester_gpas")
data class SemesterGpa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val semesterName: String, // e.g. "Semester 1", "Semester 2"
    val gpa: Double,
    val credits: Int
)

@Entity(tableName = "student_goals")
data class StudentGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val isCompleted: Boolean = false
)
