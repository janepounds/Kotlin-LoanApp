package com.cabral.emaishapay.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.models.Transaction
import com.cabral.emaishapay.databinding.ItemTransactionBinding
import com.cabral.emaishapay.singleton.MyApplication

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.MyViewHolder>() {

    private val items: ArrayList<Transaction> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            transaction = items[position]
            amt.text = String.format(root.context.getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(transaction!!.txnAmt))
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(updated: List<Transaction>) {
        items.clear()
        items.addAll(updated)
        notifyDataSetChanged()
    }

    fun addNewItems(newItems: List<Transaction>) {
        val positionStart = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(positionStart, newItems.size)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val viewBinding: ItemTransactionBinding) : RecyclerView.ViewHolder(viewBinding.root)

}