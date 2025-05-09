package com.fit2081.yeeun34752110.databases.patientdb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PatientRepository(private val patientDao: PatientDao) {

    // Fetch all patient IDs as a Flow
    fun allPatientIds(): Flow<List<Int>> {
        return patientDao.getAllPatientIds() // Return the Flow directly
    }

    suspend fun getPatientById(id: Int): Patient? {
        return patientDao.findPatientById(id)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun safeInsert(patient: Patient){
        val existing = getPatientById(patient.patientId)
        if (existing == null) {
            patientDao.insertPatient(patient)
        } else {
            patientDao.updatePatient(patient)
        }
    }

    suspend fun getAllPatientsOnce(): List<Patient> {
        return patientDao.getAllPatients().first()
    }

}