package io.github.jakubherr.gitfit.presentation

// KMP is missing an official way to translate string resources as far as i know
// this solution is from https://www.youtube.com/watch?v=BrSnxbfxNOQ
expect class Localization {
    fun applyLanguage(iso: String)
}