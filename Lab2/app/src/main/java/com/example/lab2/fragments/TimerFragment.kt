package com.example.lab2.fragments

import android.content.*
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab2.R
import com.example.lab2.data.Sequence
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentTimerBinding


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel
    private lateinit var currentItem: Sequence
    private lateinit var mBroadcastService: BroadcastService
    private var isServiceStarted: Boolean = false

    private enum class Phases(val phaseName: Int) {
        WarmUp(R.string.warm_up),
        Workout(R.string.workout),
        Rest(R.string.rest),
        CoolDown(R.string.cooldown), ;
    }

    val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val millisUntilFinished = intent?.extras?.getLong("countdown", 0)!!
            val phase = intent.getStringExtra("phase")
            println(millisUntilFinished)
            binding.textTime.text = millisUntilFinished.toString()
            binding.textPhaseName.text = phase
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        currentItem = arguments?.getParcelable<Sequence>("currItem")!!
        val timeList: MutableList<Int> = mutableListOf()
        val phaseList: MutableList<String> = mutableListOf()

        for (i in 0..this.currentItem.Sets) {
            timeList.add(getPhaseTime(Phases.WarmUp))
            phaseList.add(getString(Phases.WarmUp.phaseName))

            for (j in 0..this.currentItem.Cycles) {
                timeList.add(getPhaseTime(Phases.Workout))
                phaseList.add(getString(Phases.Workout.phaseName))

                timeList.add(getPhaseTime(Phases.Rest))
                phaseList.add(getString(Phases.Rest.phaseName))
            }

            timeList.add(getPhaseTime(Phases.CoolDown))
            phaseList.add(getString(Phases.CoolDown.phaseName))
        }

        setListToView(timeList, phaseList)
        binding.textTime.text = timeList[0].toString()
        binding.textPhaseName.text = phaseList[0]

        val intent = Intent(requireContext(), BroadcastService::class.java)
        intent.putExtra("timeList", timeList as ArrayList<Int>)
        intent.putExtra("phaseList", phaseList as ArrayList<String>)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mBroadcastService = (service as BroadcastService.LocalBinder).getService()!!
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }

        }

        activity?.registerReceiver(br, IntentFilter("BroadcastService"))

        binding.btnStart.setOnClickListener {
            if (!isServiceStarted) {
                context?.bindService(intent, serviceConnection, 0)
                context?.startService(intent)
                isServiceStarted = true
            }
            else {
                mBroadcastService.resumeTimer()
            }
        }
        binding.btnPause.setOnClickListener {
            mBroadcastService.stopTimer()
        }
        binding.btnStop.setOnClickListener {
            context?.stopService(Intent(requireContext(), BroadcastService::class.java))
            isServiceStarted = false
            findNavController().navigate(R.id.action_timerFragment_to_listFragment)
        }

        binding.btnNext.setOnClickListener {
            mBroadcastService.nextStep()
        }
        binding.btnPrev.setOnClickListener {
            mBroadcastService.prevStep()
        }

        return binding.root
    }


    private fun getPhaseTime(phase: Phases): Int {
        return when (phase) {
            Phases.WarmUp -> currentItem.WarmUp
            Phases.Workout -> currentItem.Workout
            Phases.Rest -> currentItem.Rest
            Phases.CoolDown -> currentItem.CoolDown
        }
    }

    private fun setListToView(timeList: MutableList<Int>, namesList: MutableList<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            namesList.zip(timeList) { a, b -> "$a: $b" }
        )
        binding.listView.adapter = adapter
    }
}