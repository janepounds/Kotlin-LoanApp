package com.kabbodev.emaishapay.ui.adapters.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kabbodev.emaishapay.data.models.screen.ScreenItem
import com.kabbodev.emaishapay.databinding.LayoutIntroBinding

class IntroAdapter(private val screenItems: List<ScreenItem>) : RecyclerView.Adapter<IntroAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            screenItem = screenItems[position]
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int = screenItems.size

    inner class MyViewHolder(val viewBinding: LayoutIntroBinding) : RecyclerView.ViewHolder(viewBinding.root)

}