package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.domain.virtualization.CloneEnvironment
import com.multiclone.app.domain.virtualization.ClonedAppInstaller
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCloneEnvironment(
        @ApplicationContext context: Context
    ): CloneEnvironment {
        return CloneEnvironment(context)
    }
    
    @Provides
    @Singleton
    fun provideClonedAppInstaller(
        @ApplicationContext context: Context
    ): ClonedAppInstaller {
        return ClonedAppInstaller(context)
    }
    
    @Provides
    @Singleton
    fun provideVirtualAppEngine(
        @ApplicationContext context: Context,
        clonedAppInstaller: ClonedAppInstaller,
        cloneEnvironment: CloneEnvironment
    ): VirtualAppEngine {
        return VirtualAppEngine(context, clonedAppInstaller, cloneEnvironment)
    }
}
