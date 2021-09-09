package com.cabral.emaishapay.ui.adapters.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.data.models.screen.CreditFactorItem
import com.cabral.emaishapay.databinding.ItemCreditFactorBinding
import com.cabral.emaishapay.utils.addToggleClickListeners

class CreditFactorAdapter(private val items: List<CreditFactorItem>) : RecyclerView.Adapter<CreditFactorAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(ItemCreditFactorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            creditFactorItem = items[position]
            imageAdd.setOnClickListener {
                if (layoutDescription.visibility == View.GONE) {
                    layoutDescription.visibility = View.VISIBLE; }
                else {
                    layoutDescription.visibility = View.GONE
                }
            }
            executePendingBindings()

        }

    }

    override fun getItemCount(): Int = items.size

    inner class MyViewHolder(val viewBinding: ItemCreditFactorBinding) : RecyclerView.ViewHolder(viewBinding.root)

}