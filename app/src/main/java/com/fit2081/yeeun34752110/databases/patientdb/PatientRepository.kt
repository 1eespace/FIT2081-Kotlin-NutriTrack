package com.fit2081.yeeun34752110.databases.patientdb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PatientRepository(private val patientDao: PatientDao) {

    // Fetch all unregister patient IDs
    fun unregisteredPatientIds(): Flow<List<Int>> {
        return patientDao.getAllPatients().map { patients ->
            patients.filter { it.patientName.isBlank() && it.patientPassword.isBlank() }
                .map { it.patientId }
        }
    }

    fun registeredPatientIds(): Flow<List<Int>> {
        return patientDao.getRegisteredPatientIds()
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
}