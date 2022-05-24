package com.example.and_projet.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.and_projet.R
import com.example.and_projet.models.ListRecord
import java.util.*

/**
 * Authors : Zwick Gaétan, Maziero Marco, Lamrani Soulaymane
 * Date : 10.05.2022
 */
class ListAdapter(_items: List<ListRecord> = listOf()) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    // Items in the list
    private var items = listOf<ListRecord>()
        set(value) {
            // Computes the differences between old and new list
            val diffCallback = NoteDiffCallback(items, value)
            val diffItems = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffItems.dispatchUpdatesTo(this)
        }

    init {
        items = _items
    }

    fun setNotes(newNotes: List<ListRecord>) {
        items = newNotes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    /**
     * Manages the cells inside the RecyclerView
     */
    inner class ViewHolder(_view: View) : RecyclerView.ViewHolder(_view) {
        private var view: View = _view

        // UI elements
        private val cellTitle = view.findViewById<TextView>(R.id.list_item_title)
        private val cellContent = view.findViewById<TextView>(R.id.list_item_content)

        fun bind(record: ListRecord) {
            cellTitle.text = record.title
            cellContent.text = record.content
        }
    }

    /**
     * Used to compute differences between an old list and a new list (to update the RecyclerView)
     */
    inner class NoteDiffCallback(
        private val oldList: List<ListRecord>,
        private val newList: List<ListRecord>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRecord = oldList[oldItemPosition]
            val newRecord = newList[newItemPosition]

            return oldRecord.title == newRecord.title && oldRecord.content == newRecord.content
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRecord = oldList[oldItemPosition]
            val newRecord = newList[newItemPosition]

            return oldRecord::class == newRecord::class && oldRecord.title == newRecord.title
        }
    }
}