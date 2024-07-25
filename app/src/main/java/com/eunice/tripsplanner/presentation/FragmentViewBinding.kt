package com.eunice.tripsplanner.presentation

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by {EUNICE BAKARE T.} on {9/29/22}
 * Email: {eunice@reach.africa}
 */

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : Lazy<T>, LifecycleEventObserver {
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        })
    }

//    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
//        val binding = binding
//        if (binding != null) {
//            return binding
//        }
//
//        val lifecycle = fragment.viewLifecycleOwner.lifecycle
//        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
//            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
//        }
//
//        return viewBindingFactory(thisRef.requireView()).also {
//            this@FragmentViewBindingDelegate.binding = it
//        }
//    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            binding = null
        }
    }

    override val value: T
        get() {
            return binding ?: run {
                val newValue = viewBindingFactory(fragment.requireView())
                binding = newValue
                fragment.viewLifecycleOwner.lifecycle.addObserver(this)
                newValue
            }
        }

    override fun isInitialized(): Boolean = binding != null
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(
        this,
        viewBindingFactory
    )