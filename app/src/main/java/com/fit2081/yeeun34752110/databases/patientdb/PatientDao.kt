package com.fit2081.yeeun34752110.databases.patientdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    // Insert a new patient into the database
    @Insert
    suspend fun insertPatient(patient: Patient)

    // Update an existing patient's data
    @Update
    suspend fun updatePatient(patient: Patient)

    // Delete a patient from the database
    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun findPatientById(id: Int): Patient?

    // Get all patients from the database
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>> // No suspend modifier here

    // Get a patient by ID
    @Query("SELECT * FROM patients WHERE patientId = :id")
    fun getPatientById(id: Int): Flow<Patient?> // No suspend modifier here

    // Get a patient by phone number
    @Query("SELECT * FROM patients WHERE patientPhoneNumber = :phoneNumber")
    fun getPatientByPhoneNumber(phoneNumber: String): Flow<Patient?> // No suspend modifier here

    // Get patients based on their sex
    @Query("SELECT * FROM patients WHERE patientSex = :sex")
    fun getPatientsBySex(sex: String): Flow<List<Patient>> // No suspend modifier here

    // Get only all patient IDs
    @Query("SELECT patientId FROM patients")
    fun getAllPatientIds(): Flow<List<Int>> // No suspend modifier here

    @Query("DELETE FROM patients")
    suspend fun deleteAllPatients()
}
