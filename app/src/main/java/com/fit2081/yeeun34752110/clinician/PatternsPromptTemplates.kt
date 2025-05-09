package com.fit2081.yeeun34752110.clinician

import com.fit2081.yeeun34752110.databases.patientdb.Patient

object PatternsPromptTemplates {
    private val templates: List<(List<Patient>) -> String> = listOf(

        { patients ->
            val summary = patients.take(10).joinToString("\n") {
                "Patient ${it.patientId} (${it.patientSex}): TotalScore=${it.totalScore}, Water=${it.water}, Sugar=${it.sugars}, SatFat=${it.saturatedFats}, Veg=${it.vegetables}, Fruit=${it.fruits}, WholeGrains=${it.wholeGrains}"
            }

            """
            You are a nutrition analyst. Based on the sample dataset below:

            $summary

            Identify 3 clear dietary patterns related to:
            - High or low consumption of water, sugar, saturated fat
            - Any gender-based differences (if relevant)
            - Low-scoring food categories like whole grains or vegetables

            Format your response as a numbered list using:
            1. [Insight 1]
            2. [Insight 2]
            3. [Insight 3]

            Each item should be 2–3 sentences long, and written in a professional tone suitable for a clinician dashboard.
            """.trimIndent()
        },

        { patients ->
            val summary = patients.take(10).joinToString("\n") {
                "ID ${it.patientId} (${it.patientSex}): Fruit=${it.fruits}/10, Veg=${it.vegetables}/10, Water=${it.water}/5, SatFat=${it.saturatedFats}/5, Score=${it.totalScore}"
            }

            """
            You're helping a health team spot trends in nutrition data.

            Sample data:
            $summary

            Please return 3 concise bullet points that highlight:
            - Outliers or significant patterns
            - Possible gender-linked differences
            - Areas of concern like low fruit or high fat intake

            Format your answer as:
            1. ...
            2. ...
            3. ...

            Be clear and direct, suitable for a clinician dashboard.
            """.trimIndent()
        }

    )

    fun getRandomPrompt(patients: List<Patient>): String {
        return templates.random().invoke(patients)
    }
}
