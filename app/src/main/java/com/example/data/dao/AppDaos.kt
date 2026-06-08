package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Timetable / Class Sessions ---
    @Query("SELECT * FROM class_sessions ORDER BY dayOfWeek, startTime ASC")
    fun getAllClassSessions(): Flow<List<ClassSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSession(session: ClassSession)

    @Delete
    suspend fun deleteClassSession(session: ClassSession)

    // --- Attendance Subjects ---
    @Query("SELECT * FROM attendance_subjects ORDER BY name ASC")
    fun getAllAttendanceSubjects(): Flow<List<AttendanceSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceSubject(subject: AttendanceSubject)

    @Update
    suspend fun updateAttendanceSubject(subject: AttendanceSubject)

    @Delete
    suspend fun deleteAttendanceSubject(subject: AttendanceSubject)

    // --- Assignments ---
    @Query("SELECT * FROM assignments ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllAssignments(): Flow<List<Assignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment)

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- Community Forum ---
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllCommunityPosts(): Flow<List<CommunityPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityPost(post: CommunityPost)

    @Update
    suspend fun updateCommunityPost(post: CommunityPost)

    @Query("SELECT * FROM community_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<CommunityComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityComment(comment: CommunityComment)

    // --- Notes Vault ---
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllStudyNotes(): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyNote(note: StudyNote)

    @Delete
    suspend fun deleteStudyNote(note: StudyNote)

    // --- Placement Prep (DSA) ---
    @Query("SELECT * FROM placement_dsa ORDER BY topic ASC")
    fun getAllPlacementDsa(): Flow<List<PlacementDsa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacementDsa(dsa: PlacementDsa)

    @Update
    suspend fun updatePlacementDsa(dsa: PlacementDsa)

    @Delete
    suspend fun deletePlacementDsa(dsa: PlacementDsa)

    // --- CGPA / Semester GPA ---
    @Query("SELECT * FROM semester_gpas ORDER BY semesterName ASC")
    fun getAllSemesterGpas(): Flow<List<SemesterGpa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterGpa(gpa: SemesterGpa)

    @Delete
    suspend fun deleteSemesterGpa(gpa: SemesterGpa)

    // --- Profile Goals ---
    @Query("SELECT * FROM student_goals ORDER BY isCompleted ASC")
    fun getAllGoals(): Flow<List<StudentGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: StudentGoal)

    @Update
    suspend fun updateGoal(goal: StudentGoal)

    @Delete
    suspend fun deleteGoal(goal: StudentGoal)
}
