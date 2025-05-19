package com.fit2081.yeeun34752110.clinician

import com.fit2081.yeeun34752110.databases.patientdb.Patient

object PatternsPromptTemplates {
    private val templates: List<(List<Patient>) -> String> = listOf(

        { patients ->
            val summary = patients.take(9).joinToString("\n") {
                "Patient ${it.patientId} (${it.patientSex}): SatFat=${it.saturatedFats}/5, UnsatFat=${it.unsaturatedFats}/5, Water=${it.water}/5, Discretionary=${it.discretionaryFoods}/10, Fruit=${it.fruits}/10"
            }

            """
            You are a healthcare data analyst.

            Based on the patient nutrition summary below:
            $summary

            Identify 3 clear patterns that relate to:
            - Saturated and unsaturated fat intake
            - Water and fruit consumption trends
            - Overconsumption of discretionary foods

            Write your answer as a numbered list (1–3), in a clinical tone.
            Each insight should be 1-2 sentences long.
            """.trimIndent()
        },

        { patients ->
            val summary = patients.take(9).joinToString("\n") {
                "User ${it.patientId} (${it.patientSex}): Sugar=${it.sugars}/10, Alcohol=${it.alcohol}/5, Sodium=${it.sodium}/10, Fruit=${it.fruits}/10, Score=${it.totalScore}"
            }

            """
            You are analyzing nutrition risks based on sugar, alcohol, and sodium intake.

            Dataset:
            $summary

            Provide 3 professional insights related to:
            - High sugar/alcohol/sodium intake
            - Fruit consumption and total score correlation
            - Gender-based consumption patterns (if applicable)

            Format your response as:
            1. ...
            2. ...
            3. ...
            """.trimIndent()
        },

        { patients ->
            val summary = patients.take(9).joinToString("\n") {
                "ID ${it.patientId} (${it.patientSex}): WholeGrains=${it.wholeGrains}/5, Dairy=${it.dairyAndAlternatives}/10, MeatAlt=${it.meatAndAlternatives}/10, Vegetables=${it.vegetables}/10, Fruit=${it.fruits}/10"
            }

            """
            You are analyzing nutrient imbalance in dietary data.

            Based on:
            $summary

            Derive 3 insights regarding:
            - Whole grain and dairy intake
            - Balance between meat and plant-based foods
            - Areas of improvement for fruit and vegetable diversity

            Use a formal and evidence-based tone suitable for clinicians.
            """.trimIndent()
        },

        { patients ->
            val summary = patients.take(9).joinToString("\n") {
                "Patient ${it.patientId}: Water=${it.water}/5, Fruit=${it.fruits}/10, TotalScore=${it.totalScore}"
            }

            """
            You are investigating the correlation between hydration, fruit intake, and overall diet quality.

            Sample data:
            $summary

            Identify 3 interesting insights such as:
            - Water and fruit intake’s influence on overall score
            - Outliers (e.g., high score with low hydration/fruit)
            - Suggestions for hydration- and fruit-linked interventions

            Format as 3 short, numbered paragraphs.
            """.trimIndent()
        },

        { patients ->
            val maleCount = patients.count { it.patientSex.equals("Male", ignoreCase = true) }
            val femaleCount = patients.count { it.patientSex.equals("Female", ignoreCase = true) }

            val summary = patients.take(9).joinToString("\n") {
                "Patient ${it.patientId} (${it.patientSex}): " +
                        "Discretionary=${it.discretionaryFoods}/10, Vegetables=${it.vegetables}/10, Grains=${it.grainsAndCereals}/5, " +
                        "WholeGrains=${it.wholeGrains}/5, MeatAlt=${it.meatAndAlternatives}/10, Dairy=${it.dairyAndAlternatives}/10, " +
                        "Fruit=${it.fruits}/10, Water=${it.water}/5, SatFat=${it.saturatedFats}/5, UnsatFat=${it.unsaturatedFats}/5, " +
                        "Sodium=${it.sodium}/10, Sugars=${it.sugars}/10, Alcohol=${it.alcohol}/5, Score=${it.totalScore}"
            }

            """
            You are a clinical data analyst reviewing nutrition data by gender.
        
            Sample size:
            - Males: $maleCount
            - Females: $femaleCount
        
            Sample data:
            $summary
        
            Based on the data above, extract 3 significant gender-based dietary patterns or differences.
        
            Focus areas may include:
            - Discretionary foods, saturated/unsaturated fat, sodium, alcohol
            - Fruit, vegetable, whole grain, dairy intake
            - Water consumption and overall score
        
            Present your insights in the following format:
            1. [Insight]
            2. [Insight]
            3. [Insight]
        
            Use a clear, clinical tone appropriate for a clinician dashboard. Each insight should be 1-2 sentences.
            """.trimIndent()
        }
    )

    fun getRandomPrompt(patients: List<Patient>): String {
        return templates.random().invoke(patients)
    }
}
