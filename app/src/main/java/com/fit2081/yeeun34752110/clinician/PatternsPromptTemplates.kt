package com.fit2081.yeeun34752110.clinician

import com.fit2081.yeeun34752110.databases.patientdb.Patient

object PatternsPromptTemplates {

    private val templates: List<(List<Patient>) -> String> = listOf(

        // 1. Analyse fat, water, fruit intake, and discretionary food overuse
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
            Highlight only the top 3 most meaningful and actionable insights.
            If no clear correlation is found, suggest the next most relevant or interesting clinical insight based on the available data.
            Keep each insight concise (max 20 words) and clinically focused.

            """.trimIndent()
        },

        // 2. Assess sugar, alcohol, sodium risks and gender patterns
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
            Highlight only the top 3 most meaningful and keep each insight concise (max 20 words) and clinically focused.
            If no clear correlation is found, suggest the next most relevant or interesting clinical insight based on the available data.
            """.trimIndent()
        },

        // 3. Detect nutrient imbalance
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

            Keep each insight concise (max 20 words) and clinically focused.
            If no clear correlation is found, suggest the next most relevant or interesting clinical insight based on the available data.
            
            """.trimIndent()
        },

        // 4. Correlate water/fruit intake with overall diet score
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

            Format as 3 short, numbered paragraphs. And, keep each insight concise (max 20 words) and clinically focused.
            If no clear correlation is found, suggest the next most relevant or interesting clinical insight based on the available data.
            """.trimIndent()
        },

        // 5. Evaluate protein source diversity
        { patients ->
            val summary = patients.take(9).joinToString("\n") {
                "Patient ${it.patientId} (${it.patientSex}): MeatAlt=${it.meatAndAlternatives}/10, Dairy=${it.dairyAndAlternatives}/10, WholeGrains=${it.wholeGrains}/5, TotalScore=${it.totalScore}"
            }

            """
            You are investigating how patients obtain protein and whether they achieve a diverse balance of sources.
        
            Sample data:
            $summary
        
            Identify 3 insights based on:
            - Meat vs dairy protein balance
            - Role of whole grains in protein contribution
            - Gender-related variation, if any
        
            Present your findings as 3 numbered insights. 
            Each should be concise (max 20 words), clinically meaningful, and suggest improvements if needed.
            If no strong pattern is observed, mention another notable dietary imbalance found.
            """.trimIndent()
        },

        // 6. Identify common traits in low total score patients
        { patients ->
            val sorted = patients.sortedBy { it.totalScore }.take(9)
            val summary = sorted.joinToString("\n") {
                "Patient ${it.patientId}: TotalScore=${it.totalScore}, Veg=${it.vegetables}/10, Grains=${it.grainsAndCereals}/5, Discretionary=${it.discretionaryFoods}/10, Water=${it.water}/5, Alcohol=${it.alcohol}/5, Sugars=${it.sugars}/10"
            }

            """
            You are identifying at-risk patients based on their overall nutrition score.
        
            Data below shows 9 patients with the lowest HEIFA scores:
            $summary
        
            Derive 3 clinically meaningful insights by analyzing:
            - Common deficiencies among low-score patients
            - Over-consumed food types
            - Any distinct patterns across the data
        
            Present your insights as:
            1. ...
            2. ...
            3. ...
            Keep each insight concise (max 20 words) and clinically focused.
            If no strong pattern is observed, describe any other notable dietary concern from the data.
            """.trimIndent()
        },

        // 7. Compare gender-based(Grouping) dietary patterns
        { patients ->
            val genderGroups = patients.groupBy {
                it.patientSex?.trim()?.replaceFirstChar { c -> c.uppercaseChar() } ?: "Unknown"
            }

            val genderSummary = genderGroups.entries.joinToString("\n\n") { (sex, group) ->
                val sample = group.take(5).joinToString("\n") { // Using 5 sample
                    "Patient ${it.patientId} (${it.patientSex}): " +
                            "Discretionary=${it.discretionaryFoods}/10, Vegetables=${it.vegetables}/10, Grains=${it.grainsAndCereals}/5, " +
                            "WholeGrains=${it.wholeGrains}/5, MeatAlt=${it.meatAndAlternatives}/10, Dairy=${it.dairyAndAlternatives}/10, " +
                            "Fruit=${it.fruits}/10, Water=${it.water}/5, SatFat=${it.saturatedFats}/5, UnsatFat=${it.unsaturatedFats}/5, " +
                            "Sodium=${it.sodium}/10, Sugars=${it.sugars}/10, Alcohol=${it.alcohol}/5, Score=${it.totalScore}"
                }
                "Group: $sex (${group.size} patients)\n$sample"
            }

            """
            You are a clinical data analyst reviewing nutrition data segmented by gender.
        
            Grouped sample data:
            $genderSummary
        
            Using the above data, derive 3 clinically relevant gender-based nutritional patterns.
        
            Focus areas may include:
            - Discretionary foods, fat, sodium, and alcohol intake
            - Fruit, vegetable, whole grain, and dairy consumption
            - Hydration and total diet score
        
            Present your response in this format:
            1. [Insight]
            2. [Insight]
            3. [Insight]
        
            Use a clear, focus on key 3 patterns only and keep each insight concise (max 20 words) and clinically focused.
            
            """.trimIndent()
        }
    )

    fun getRandomPrompt(patients: List<Patient>): String {
        return templates.random().invoke(patients)
    }
}
