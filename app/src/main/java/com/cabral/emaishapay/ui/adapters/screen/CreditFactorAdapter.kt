package com.cabral.emaishapay.ui.adapters.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.data.models.screen.CreditFactorItem
import com.cabral.emaishapay.databinding.ItemCreditFactorBinding

class CreditFactorAdapter(private val items: List<CreditFactorItem>) : RecyclerView.Adapter<CreditFactorAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(ItemCreditFactorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            creditFactorItem = items[position]
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int = items.size

    inner class MyViewHolder(val viewBinding: ItemCreditFactorBinding) : RecyclerView.ViewHolder(viewBinding.root)

}