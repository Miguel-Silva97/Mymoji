package com.example.mymoji.core.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastDisplayedItemStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(item: LastDisplayedItem) {
        val editor = prefs.edit()
        when (item) {
            is LastDisplayedItem.Emoji -> {
                editor.putString(KEY_TYPE, TYPE_EMOJI)
                editor.putString(KEY_VALUE, item.name)
            }
            is LastDisplayedItem.GitHubUser -> {
                editor.putString(KEY_TYPE, TYPE_GITHUB_USER)
                editor.putString(KEY_VALUE, item.login)
            }
        }
        editor.apply()
    }

    fun load(): LastDisplayedItem? {
        val value = prefs.getString(KEY_VALUE, null) ?: return null
        return when (prefs.getString(KEY_TYPE, null)) {
            TYPE_EMOJI -> LastDisplayedItem.Emoji(value)
            TYPE_GITHUB_USER -> LastDisplayedItem.GitHubUser(value)
            else -> null
        }
    }

    private companion object {
        const val PREFS_NAME = "last_displayed_item"
        const val KEY_TYPE = "type"
        const val KEY_VALUE = "value"
        const val TYPE_EMOJI = "emoji"
        const val TYPE_GITHUB_USER = "github_user"
    }
}
