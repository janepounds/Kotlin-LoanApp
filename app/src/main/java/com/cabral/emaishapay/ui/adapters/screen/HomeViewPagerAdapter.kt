package com.cabral.emaishapay.ui.adapters.screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.data.models.screen.ScreenItem
import com.cabral.emaishapay.databinding.LayoutHomeViewpagerBinding
import com.cabral.emaishapay.utils.spannedFromHtml

class HomeViewPagerAdapter(private val screenItems: List<ScreenItem>, private val listener: HomeViewPagerListener) : RecyclerView.Adapter<HomeViewPagerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutHomeViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.viewBinding.apply {
            screenItem = screenItems[position]
            subtitle.text = spannedFromHtml(screenItem!!.subtitle!!)
            getNowBtn.setOnClickListener { listener.onViewClick(screenItem!!) }
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int = screenItems.size

    inner class MyViewHolder(val viewBinding: LayoutHomeViewpagerBinding) : RecyclerView.ViewHolder(viewBinding.root)

    fun interface HomeViewPagerListener {
        fun onViewClick(screenItem: ScreenItem)
    }

}