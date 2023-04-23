package com.android.muslimAssistant.utils

import androidx.lifecycle.MutableLiveData

class CustomList<T> : ArrayList<T>() {

    val sizeLiveData = MutableLiveData<Int>()

    override fun removeAt(index: Int): T {
        val isAdded = super.removeAt(index)
        sizeLiveData.value = size
        return isAdded
    }

    override fun add(element: T): Boolean {
        val isAdded = super.add(element)
        sizeLiveData.value = size
        return isAdded
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val isAdded = super.addAll(elements)
        sizeLiveData.value = size
        return isAdded
    }

    override fun clear() {
        super.clear()
        sizeLiveData.value = size
    }
}