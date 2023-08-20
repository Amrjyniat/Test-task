package com.example.testtask.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.testtask.databinding.DialogAddItemBinding
import com.example.testtask.databinding.FragmentListBinding
import com.example.testtask.utils.bind
import com.example.testtask.utils.bindChecked
import com.example.testtask.utils.launchAndRepeatInLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()
    private val itemsAdapter = ItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)

        binding.apply {
            switchFirestoreSource.bindChecked(viewLifecycleOwner, viewModel.isFirestoreSource)
//            switchFirestoreSource.setOnCheckedChangeListener { _, isChecked ->
//
//            }

            rvItems.adapter = itemsAdapter
            rvItems.addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )

            tvProfile.setOnClickListener {
                findNavController().navigate(ListFragmentDirections.goToProfile())
            }
            tvBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            fabAddItem.setOnClickListener {
                showAddItemDialog()
            }
        }

        viewModel.isFirestoreSource.launchAndRepeatInLifecycle(viewLifecycleOwner) { isFire ->
            Log.i("TestCheck", "isFirestoreSource observe: $isFire")
        }

        viewModel.items.launchAndRepeatInLifecycle(viewLifecycleOwner) { items ->
            Log.i("TestCheck", "items : $items")
            itemsAdapter.submitList(items)
        }

        viewModel.message.launchAndRepeatInLifecycle(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        initSwipeToDeleteItems()


        return binding.root
    }

    private fun initSwipeToDeleteItems() {
        val simpleItemTouchCallback: ItemTouchHelper.Callback =
            object : ItemTouchHelper.Callback() {

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val item = itemsAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.deleteItem(item.id)
                }

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return makeMovementFlags(0, ItemTouchHelper.LEFT)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

            }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvItems)

    }

    private fun showAddItemDialog() {
        val dialogBinding = DialogAddItemBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.edAddItem.bind(viewLifecycleOwner, viewModel.itemInput)

        dialogBinding.btnSave.setOnClickListener {
            viewModel.saveItem()
            dialog.dismiss()
        }

        dialog.show()
    }

}