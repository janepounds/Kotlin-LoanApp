package com.kabbodev.emaishapay.ui.fragments.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentRegisterBinding
import com.kabbodev.emaishapay.ui.adapters.screen.ViewPagerAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.fragments.register.login.LoginFragment
import com.kabbodev.emaishapay.ui.fragments.register.signup.SignUpFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        val showLoginFirst = requireArguments().getBoolean(KEY_SHOW_LOGIN_FIRST)
        setupViewPager()
        if (!showLoginFirst) updateCurrentItem(1, false)
    }

    override fun setupClickListeners() {

    }

    private fun setupViewPager() {
        val fragments: ArrayList<Fragment> = ArrayList()
        fragments.add(LoginFragment())
        fragments.add(SignUpFragment())

        val adapter = ViewPagerAdapter(this, fragments)
        binding.loginPager.adapter = adapter
        binding.loginPager.offscreenPageLimit = 3

        val tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = getString(R.string.login)
                1 -> tab.text = getString(R.string.sign_up)
            }
        }
        TabLayoutMediator(binding.loginTabs, binding.loginPager, true, tabConfigurationStrategy).attach()
        loginPager = binding.loginPager
    }

    companion object {
        const val KEY_SHOW_LOGIN_FIRST = "show_login_first"
        var loginPager: ViewPager2? = null

        fun updateCurrentItem(forSelectItem: Int, smoothScroll: Boolean) {
            loginPager?.setCurrentItem(forSelectItem, smoothScroll)
        }
    }

}