package com.example.lab2.fragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab2.R
import android.os.CountDownTimer
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab2.MainActivity
import com.example.lab2.data.Sequence
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentTimerBinding
import java.security.AccessController.getContext

class TimerFragment : Fragment() {

    private lateinit var timer: CountDownTimer
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel
    private var currentPhase: Phases = Phases.WarmUp
    private lateinit var currentItem: Sequence
    private var timerIsPaused: Boolean = false
    private var currentPos: Int = 0

    private enum class Phases(val phaseName: Int) {
        WarmUp(R.string.warm_up),
        Workout(R.string.workout),
        Rest(R.string.rest),
        CoolDown(R.string.cooldown),;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        currentItem = arguments?.getParcelable<Sequence>("currItem")!!

        val sets = this.currentItem.Sets
        val cycles = this.currentItem.Cycles
        val timeList: MutableList<Long> = mutableListOf()
        val namesList: MutableList<String> = mutableListOf()
        for (i in 0..cycles) {
            for (j in 0..sets) {
                timeList.add(getPhaseTime())
                namesList.add(getString(currentPhase.phaseName))
                this.currentPhase = getNextPhase()
            }
        }
        binding.textTime.text = timeList[0].toString()
        binding.textPhaseName.text = namesList[0]
        setList(timeList, namesList)
        binding.btnStart.setOnClickListener {
            setTimer(timeList, namesList)
        }
        binding.btnPause.setOnClickListener {
            pauseTimer()
        }
        binding.btnStop.setOnClickListener {
            pauseTimer()
            findNavController().navigate(R.id.action_timerFragment_to_listFragment)
        }
        binding.btnNext.setOnClickListener {
            pauseTimer()
            if (currentPos < timeList.size - 1)
                currentPos += 1
            setTimer(timeList, namesList)
        }
        binding.btnPrev.setOnClickListener {
            pauseTimer()
            if (currentPos > 0)
                currentPos -= 1
            setTimer(timeList, namesList)
        }

        return binding.root
    }

    private fun setTimer(timeList: MutableList<Long>, namesList: MutableList<String>) {
        if (currentPos > timeList.size)
            return

        binding.textPhaseName.text = namesList[currentPos]
        timer = object : CountDownTimer(timeList[currentPos] * 1000, 1000) {

            override fun onFinish() {
                currentPos += 1
                setTimer(timeList, namesList)
            }

            override fun onTick(millisUntilFinished: Long) {
                timeList[currentPos] = millisUntilFinished / 1000 + 1
                updateUI(millisUntilFinished)
            }
        }.start()
        timerIsPaused = false

    }
    private fun pauseTimer() {
        if (!timerIsPaused && this::timer.isInitialized) {
            timer.cancel()
            timerIsPaused = true
        }
    }

    private fun getNextPhase(): Phases {
        return when (currentPhase) {
            Phases.WarmUp -> return Phases.Workout
            Phases.Workout -> return Phases.Rest
            Phases.Rest -> return Phases.CoolDown
            Phases.CoolDown -> return Phases.WarmUp
        }
    }

    private fun getPhaseTime(): Long {
        return when (currentPhase) {
            Phases.WarmUp -> currentItem.WarmUp.toLong()
            Phases.Workout -> currentItem.Workout.toLong()
            Phases.Rest -> currentItem.Rest.toLong()
            Phases.CoolDown -> currentItem.CoolDown.toLong()
        }
    }

    private fun updateUI(remainingTime: Long) {
        binding.textTime.text = (remainingTime / 1000).toString()
    }

    private fun setList(timeList: MutableList<Long>, namesList: MutableList<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            namesList.zip(timeList) {a, b -> "$a: $b"}
        )
        binding.listView.adapter = adapter
    }
}