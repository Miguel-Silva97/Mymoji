package com.example.mymoji.feature.googlerepos.presentation

import androidx.annotation.StringRes
import com.example.mymoji.feature.googlerepos.R
import retrofit2.HttpException

@StringRes
internal fun Throwable.toMessageRes(): Int = when ((this as? HttpException)?.code()) {
    403 -> R.string.error_rate_limited
    else -> R.string.error_repos_unknown
}
