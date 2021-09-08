package com.cabral.emaishapay.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.models.screen.ScreenItem
import com.cabral.emaishapay.databinding.FragmentIntroBinding
import com.cabral.emaishapay.ui.adapters.screen.IntroAdapter
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.utils.navigateUsingPopUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroFragment : BaseFragment<FragmentIntroBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentIntroBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        lifecycleScope.launch {
            userPreferences.saveShowIntro(false)
        }
        setupViewPager()
    }

    override fun setupClickListeners() {
        binding.skipTv.setOnClickListener {
            navController.navigateUsingPopUp(R.id.introFragment, R.id.action_global_welcomeFragment)
        }
        binding.nextBtn.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem + 1 < binding.viewPager.adapter?.itemCount!!) {
                binding.viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                navController.navigateUsingPopUp(R.id.introFragment, R.id.action_global_welcomeFragment)
            }
        }
    }

    private fun setupViewPager() {
        val screenItems: ArrayList<ScreenItem> = ArrayList()
        screenItems.add(ScreenItem(drawableId = R.drawable.illus_welcome_screen_one, title = getString(R.string.intro_screen_1)))
        screenItems.add(ScreenItem(drawableId = R.drawable.illus_welcome_screen_two, title = getString(R.string.intro_screen_2)))
        screenItems.add(ScreenItem(drawableId = R.drawable.illus_welcome_screen_three, title = getString(R.string.intro_screen_3)))

        val introAdapter = IntroAdapter(screenItems)
        binding.viewPager.adapter = introAdapter
        binding.viewPager.offscreenPageLimit = 3

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { _: TabLayout.Tab, _: Int -> }
        TabLayoutMediator(binding.viewpagerIndicator, binding.viewPager, true, tabConfigurationStrategy).attach()
    }


}