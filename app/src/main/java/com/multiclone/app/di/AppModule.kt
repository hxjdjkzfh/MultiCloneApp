package com.multiclone.app.di

import android.content.Context
import com.multiclone.app.utils.IconUtils
import com.multiclone.app.utils.PermissionUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideIconUtils(@ApplicationContext context: Context): IconUtils {
        return IconUtils(context)
    }
    
    @Provides
    @Singleton
    fun providePermissionUtils(@ApplicationContext context: Context): PermissionUtils {
        return PermissionUtils(context)
    }
}