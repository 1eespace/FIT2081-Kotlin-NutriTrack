package com.fit2081.yeeun34752110.databases.patientdb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PatientRepository(private val patientDao: PatientDao) {

    // Fetch all patient IDs as a Flow
    fun allPatientIds(): Flow<List<Int>> {
        return patientDao.getAllPatientIds()
    }

    suspend fun getPatientById(id: Int): Patient? {
        return patientDao.findPatientById(id)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun dataInsert(patient: Patient) {
        val existing = getPatientById(patient.patientId)
        if (existing == null) {
            // No existing patient found, insert new record
            patientDao.insertPatient(patient)
        } else {
            // Patient already exists, update existing record
            patientDao.updatePatient(patient)
        }
    }

    suspend fun getAllPatientsOnce(): List<Patient> {
        return patientDao.getAllPatients().first()
    }

    // Update fruit score (manually)
    suspend fun updateFruitsScore(patientId: Int, newFruitsScore: Float) {
        patientDao.updateFruitsScore(patientId, newFruitsScore)
    }

}