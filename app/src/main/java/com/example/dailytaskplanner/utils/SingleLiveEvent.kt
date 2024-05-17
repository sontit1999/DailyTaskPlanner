package com.example.dailytaskplanner.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> : MutableLiveData<T>() {
	private val mPending: AtomicBoolean = AtomicBoolean(false)


	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(owner) {
			if (mPending.compareAndSet(true, false)) {
				observer.onChanged(it)
			}
		}
	}

	override fun observeForever(observer: Observer<in T>) {
		super.observeForever {
			if (mPending.compareAndSet(true, false)) {
				observer.onChanged(it)
			}
		}
	}

	@MainThread
	override fun setValue(t: T?) {
		mPending.set(true)
		super.setValue(t)
	}

	override fun postValue(value: T) {
		mPending.set(true)
		super.postValue(value)
	}

	/**
	 * Used for cases where T is Void, to make calls cleaner.
	 */
	@MainThread
	fun call() {
		value = null
	}

	companion object {
		private const val TAG = "SingleLiveEvent"
	}
}