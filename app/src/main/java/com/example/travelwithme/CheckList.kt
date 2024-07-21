package com.example.travelwithme

import ChecklistAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
            showAddItemDialog()
        }
    }

    private fun showAddItemDialog() {
        val input = EditText(context)
        AlertDialog.Builder(requireContext())
            .setTitle("Add Checklist Item")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val newItemText = input.text.toString()
                if (newItemText.isNotBlank()) {
                    addChecklistItem(newItemText)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addChecklistItem(itemText: String) {
        val userEmail = UserSession.getCurrentUserEmail()
        if (userEmail != null) {
            val userDao = TravelDatabase.getInstance(requireContext()).userDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val newItem = ChecklistItem(
                    id = System.currentTimeMillis(), // Using timestamp as a simple unique ID
                    text = itemText,
                    isChecked = false
                )
                userDao.addChecklistItem(userEmail, newItem)
                loadChecklist() // Reload the checklist to show the new item
            }
        }
    }

    private fun setupRecyclerView() {
        checklistAdapter = ChecklistAdapter(
            onItemCheckedChange = { item, isChecked ->
                updateChecklistItem(item, isChecked)
            },
            onItemDelete = { item ->
                deleteChecklistItem(item)
            }
        )
        binding.checklistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = checklistAdapter
        }
    }

    private fun updateChecklistItem(item: ChecklistItem, isChecked: Boolean) {
        val userEmail = UserSession.getCurrentUserEmail()
        if (userEmail != null) {
            val userDao = TravelDatabase.getInstance(requireContext()).userDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val updatedItem = item.copy(isChecked = isChecked)
                userDao.updateChecklistItem(userEmail, updatedItem)
                withContext(Dispatchers.Main) {
                    // Update the item in the adapter
                    val currentList = checklistAdapter.currentList.toMutableList()
                    val index = currentList.indexOfFirst { it.id == item.id }
                    if (index != -1) {
                        currentList[index] = updatedItem
                        checklistAdapter.submitList(currentList)
                    }
                }
            }
        }
    }

    private fun deleteChecklistItem(item: ChecklistItem) {
        val userEmail = UserSession.getCurrentUserEmail()
        if (userEmail != null) {
            val userDao = TravelDatabase.getInstance(requireContext()).userDao()
            lifecycleScope.launch(Dispatchers.IO) {
                userDao.deleteChecklistItem(userEmail, item)
                withContext(Dispatchers.Main) {
                    loadChecklist()
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}