package com.example.recyclesample

import android.content.Context

class ScorePreference(context : Context) {
    val PREFERENCE_KEY_SCORES = "Change_Scores_Key"
    val PREFERENCE_KEY = "Scores"

    val preference = context.getSharedPreferences(PREFERENCE_KEY_SCORES, Context.MODE_PRIVATE)

    fun getScores() : String? {
        return preference.getString(PREFERENCE_KEY_SCORES, "")
    }

    fun setScores(newScoreJSON : String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_KEY_SCORES, newScoreJSON)
        editor.apply()
    }
}