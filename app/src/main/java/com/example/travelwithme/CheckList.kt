package com.example.travelwithme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwithme.Data.ChecklistItem
import com.example.travelwithme.Data.TravelDatabase
import com.example.travelwithme.Data.UserSession
import com.example.travelwithme.databinding.CheckListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckList : Fragment() {

    private var _binding: CheckListBinding? = null
    private val binding get() = _binding!!
    private lateinit var checklistAdapter: ChecklistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CheckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadChecklist()

        binding.addItemButton.setOnClickListener {
            // TODO: Implement add item functionality
            Toast.makeText(context, "Add item functionality to be implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        checklistAdapter = ChecklistAdapter { item, isChecked ->
            updateChecklistItem(item, isChecked)
        }
        binding.checklistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checklistAdapter
        }
    }

    private fun loadChecklist() {
        val userEmail = UserSession.getCurrentUserEmail()
        if (userEmail != null) {
            val userDao = TravelDatabase.getInstance(requireContext()).userDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val user = userDao.getUserByEmail(userEmail)
                withContext(Dispatchers.Main) {
                    user?.let {
                        checklistAdapter.submitList(it.checklist)
                    }
                }
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChecklistItem(item: ChecklistItem, isChecked: Boolean) {
        val userEmail = UserSession.getCurrentUserEmail()
        if (userEmail != null) {
            val userDao = TravelDatabase.getInstance(requireContext()).userDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val updatedItem = item.copy(isChecked = isChecked)
                userDao.updateChecklistItem(userEmail, updatedItem)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}