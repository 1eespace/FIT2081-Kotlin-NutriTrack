package com.fit2081.yeeun34752110.nutricoach

import com.fit2081.yeeun34752110.databases.patientdb.Patient

object NutriCoachPromptTemplates {
    private val templates: List<(Patient) -> String> = listOf(

        { data ->
            """
            Generate a short, friendly and encouraging message to help someone improve their fruit intake.

            Patient nutrition scores:
            - Fruits: ${data.fruits}/10
            - Vegetables: ${data.vegetables}/10
            - Grains & Cereals: ${data.grainsAndCereals}/5
            - Whole Grains: ${data.wholeGrains}/5
            - Meat & Alternatives: ${data.meatAndAlternatives}/10
            - Dairy & Alternatives: ${data.dairyAndAlternatives}/10
            - Water: ${data.water}/5
            - Saturated Fat: ${data.saturatedFats}/5
            - Unsaturated Fat: ${data.unsaturatedFats}/5
            - Sodium: ${data.sodium}/10
            - Sugars: ${data.sugars}/10
            - Alcohol: ${data.alcohol}/5
            - Discretionary Foods: ${data.discretionaryFoods}/10

            Please limit the message to 30 words or fewer.
            """.trimIndent()
        },

        { p -> "You are a supportive health coach. A user scored ${p.fruits}/10 for fruit intake. Suggest a 30-word motivational message and include an emoji like 🍓 or 😊 to make it more engaging."},

        { p -> "A user scored ${p.fruits}/10 in fruit intake. Recommend a small change, like adding a banana to breakfast. Make it friendly and realistic in under 30 words and include an emoji to make it more engaging." },

        { p -> "User’s fruit score is ${p.fruits}/10. Encourage them to eat colorful fruits like mango, kiwi, and berries. Respond warmly, under 30 words." },

        { p -> "Someone with a fruit score of ${p.fruits}/10 is trying to improve. Acknowledge their effort and suggest one small fruit-related action. Keep it uplifting and concise." }
    )

    fun getRandomPrompt(patient: Patient): String {
        return templates.random().invoke(patient)
    }
}
