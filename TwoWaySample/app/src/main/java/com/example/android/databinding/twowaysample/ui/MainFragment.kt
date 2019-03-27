package com.example.android.databinding.twowaysample.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableFloat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.databinding.twowaysample.BR
import com.example.android.databinding.twowaysample.R
import com.example.android.databinding.twowaysample.data.IntervalTimerViewModel
import com.example.android.databinding.twowaysample.data.IntervalTimerViewModelFactory
import com.example.android.databinding.twowaysample.databinding.IntervalTimerBinding

/**
 * This fragment only takes care of binding a ViewModel to the layout. All UI calls are delegated
 * to the Data Binding library or Binding Adapters ([BindingAdapters]).
 *
 * Note that not all calls to the framework are removed, fragments are still responsible for non-UI
 * interactions with the framework, like Shared Preferences or Navigation.
 */
class MainFragment : Fragment() {
    private val intervalTimerViewModel: IntervalTimerViewModel
            by lazy {
                ViewModelProviders.of(this, IntervalTimerViewModelFactory)
                        .get(IntervalTimerViewModel::class.java)
            }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: IntervalTimerBinding = DataBindingUtil.inflate(inflater, R.layout.interval_timer, container, false)
        binding.viewModel = intervalTimerViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Save the user settings whenever they change */
        observeAndSaveTimePerSet(
                intervalTimerViewModel.timePerWorkSet, R.string.prefs_timePerWorkSet)
        observeAndSaveTimePerSet(
                intervalTimerViewModel.timePerRestSet, R.string.prefs_timePerRestSet)

        /* Number of sets needs a different  */
        observeAndSaveNumberOfSets(intervalTimerViewModel)

        if (savedInstanceState == null) {
            /* If this is the first run, restore shared settings */
            restorePreferences(intervalTimerViewModel)
            observeAndSaveNumberOfSets(intervalTimerViewModel)
        }
    }

    private fun observeAndSaveTimePerSet(timePerWorkSet: ObservableFloat, prefsKey: Int) {
        timePerWorkSet.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    @SuppressLint("CommitPrefEdits")
                    override fun onPropertyChanged(observable: Observable?, propertyId: Int) {
                        Log.d("saveTimePerWorkSet", "Saving time-per-set preference")
                        val sharedPref =
                                activity!!.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) ?: return
                        sharedPref.edit().apply {
                            putFloat(getString(prefsKey), (observable as ObservableFloat).get())
                            apply()
                        }
                    }
                })
    }

    private fun restorePreferences(viewModel: IntervalTimerViewModel) {
        val sharedPref =
                activity!!.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) ?: return
        val timePerWorkSetKey = getString(R.string.prefs_timePerWorkSet)
        var wasAnythingRestored = false
        if (sharedPref.contains(timePerWorkSetKey)) {
            viewModel.timePerWorkSet.set(sharedPref.getFloat(timePerWorkSetKey, 100f))
            wasAnythingRestored = true
        }
        val timePerRestSetKey = getString(R.string.prefs_timePerRestSet)
        if (sharedPref.contains(timePerRestSetKey)) {
            viewModel.timePerRestSet.set(sharedPref.getFloat(timePerRestSetKey, 50f))
            wasAnythingRestored = true
        }
        val numberOfSetsKey = getString(R.string.prefs_numberOfSets)
        if (sharedPref.contains(numberOfSetsKey)) {
            viewModel.numberOfSets = arrayOf(0, sharedPref.getInt(numberOfSetsKey, 5))
            wasAnythingRestored = true
        }
        if (wasAnythingRestored) Log.d("saveTimePerWorkSet", "Preferences restored")
        viewModel.stopButtonClicked()
    }

    private fun observeAndSaveNumberOfSets(viewModel: IntervalTimerViewModel) {
        viewModel.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            @SuppressLint("CommitPrefEdits")
            override fun onPropertyChanged(observable: Observable?, propertyId: Int) {
                if (propertyId == BR.numberOfSets) {
                    Log.d("saveTimePerWorkSet", "Saving number of sets preference")
                    val sharedPref =
                            activity!!.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE) ?: return
                    sharedPref.edit().apply {
                        putInt(getString(R.string.prefs_numberOfSets), viewModel.numberOfSets[1])
                        apply()
                    }
                } else {
                    Log.d("Cash", "onPropertyChanged, id = $propertyId")
                }
            }
        })
    }
}