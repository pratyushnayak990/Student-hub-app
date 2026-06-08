package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    // --- Timetable ---
    val allClassSessions: Flow<List<ClassSession>> = appDao.getAllClassSessions()
    suspend fun insertClassSession(session: ClassSession) = appDao.insertClassSession(session)
    suspend fun deleteClassSession(session: ClassSession) = appDao.deleteClassSession(session)

    // --- Attendance Tracker ---
    val allAttendanceSubjects: Flow<List<AttendanceSubject>> = appDao.getAllAttendanceSubjects()
    suspend fun insertAttendanceSubject(subject: AttendanceSubject) = appDao.insertAttendanceSubject(subject)
    suspend fun updateAttendanceSubject(subject: AttendanceSubject) = appDao.updateAttendanceSubject(subject)
    suspend fun deleteAttendanceSubject(subject: AttendanceSubject) = appDao.deleteAttendanceSubject(subject)

    // --- Assignments ---
    val allAssignments: Flow<List<Assignment>> = appDao.getAllAssignments()
    suspend fun insertAssignment(assignment: Assignment) = appDao.insertAssignment(assignment)
    suspend fun updateAssignment(assignment: Assignment) = appDao.updateAssignment(assignment)
    suspend fun deleteAssignment(assignment: Assignment) = appDao.deleteAssignment(assignment)

    // --- Expenses ---
    val allExpenses: Flow<List<Expense>> = appDao.getAllExpenses()
    suspend fun insertExpense(expense: Expense) = appDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = appDao.deleteExpense(expense)

    // --- Community Forum ---
    val allCommunityPosts: Flow<List<CommunityPost>> = appDao.getAllCommunityPosts()
    suspend fun insertCommunityPost(post: CommunityPost) = appDao.insertCommunityPost(post)
    suspend fun updateCommunityPost(post: CommunityPost) = appDao.updateCommunityPost(post)
    fun getCommentsForPost(postId: Int): Flow<List<CommunityComment>> = appDao.getCommentsForPost(postId)
    suspend fun insertCommunityComment(comment: CommunityComment) = appDao.insertCommunityComment(comment)

    // --- Notes Vault ---
    val allStudyNotes: Flow<List<StudyNote>> = appDao.getAllStudyNotes()
    suspend fun insertStudyNote(note: StudyNote) = appDao.insertStudyNote(note)
    suspend fun deleteStudyNote(note: StudyNote) = appDao.deleteStudyNote(note)

    // --- Placement Prep ---
    val allPlacementDsa: Flow<List<PlacementDsa>> = appDao.getAllPlacementDsa()
    suspend fun insertPlacementDsa(dsa: PlacementDsa) = appDao.insertPlacementDsa(dsa)
    suspend fun updatePlacementDsa(dsa: PlacementDsa) = appDao.updatePlacementDsa(dsa)
    suspend fun deletePlacementDsa(dsa: PlacementDsa) = appDao.deletePlacementDsa(dsa)

    // --- CGPA / Semester --
    val allSemesterGpas: Flow<List<SemesterGpa>> = appDao.getAllSemesterGpas()
    suspend fun insertSemesterGpa(gpa: SemesterGpa) = appDao.insertSemesterGpa(gpa)
    suspend fun deleteSemesterGpa(gpa: SemesterGpa) = appDao.deleteSemesterGpa(gpa)

    // --- Student Goals ---
    val allGoals: Flow<List<StudentGoal>> = appDao.getAllGoals()
    suspend fun insertGoal(goal: StudentGoal) = appDao.insertGoal(goal)
    suspend fun updateGoal(goal: StudentGoal) = appDao.updateGoal(goal)
    suspend fun deleteGoal(goal: StudentGoal) = appDao.deleteGoal(goal)
}
