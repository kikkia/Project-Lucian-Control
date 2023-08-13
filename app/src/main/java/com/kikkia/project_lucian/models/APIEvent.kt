package com.kikkia.project_lucian.models

open class APIEvent<out T>(private val content: T) {

    var handled = false
        private set

    fun getContent(): T? {
        return if (handled) {
            null
        }
        else {
            handled = true
            content
        }
    }

    fun peekContent(): T = content
}