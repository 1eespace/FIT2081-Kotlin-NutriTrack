package com.fit2081.yeeun34752110.databases.patientdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    // Insert
    @Insert
    suspend fun insertPatient(patient: Patient)

    // Update
    @Update
    suspend fun updatePatient(patient: Patient)

    // Delete
    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun findPatientById(id: Int): Patient?

    // Get all patients from the database
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    // Get a patient by ID
    @Query("SELECT * FROM patients WHERE patientId = :id")
    fun getPatientById(id: Int): Flow<Patient?>

    // Get a patient by phone number
    @Query("SELECT * FROM patients WHERE patientPhoneNumber = :phoneNumber")
    fun getPatientByPhoneNumber(phoneNumber: String): Flow<Patient?>

    // Get patients based on their sex
    @Query("SELECT * FROM patients WHERE patientSex = :sex")
    fun getPatientsBySex(sex: String): Flow<List<Patient>>

    // Get only all patient IDs
    @Query("SELECT patientId FROM patients")
    fun getAllPatientIds(): Flow<List<Int>>

    // Update the patient's fruit score for making optimal
    @Query("UPDATE patients SET fruits = :newFruitsScore WHERE patientId = :id")
    suspend fun updateFruitsScore(id: Int, newFruitsScore: Float)

    // Delete all
    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()
}
