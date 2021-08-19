package com.kabbodev.emaishapay.di

import com.kabbodev.emaishapay.data.repositories.LoginRepository
import com.kabbodev.emaishapay.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLoginRepository(): LoginRepository = LoginRepository()

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository = UserRepository()


}