package com.fit2081.yeeun34752110.databases.nutricoachtips

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NutriCoachTipsRepository(
    private val nutriCoachTipsDao: NutriCoachTipsDao
) {
    suspend fun insertTip(tip: NutriCoachTips) = withContext(Dispatchers.IO) {
        nutriCoachTipsDao.insertTip(tip)
    }

    suspend fun getTipsForPatient(patientId: Int): List<NutriCoachTips> = withContext(Dispatchers.IO) {
        nutriCoachTipsDao.getTipsForPatient(patientId)
    }
}
