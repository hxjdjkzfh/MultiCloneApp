package com.multiclone.app.di;

import android.content.Context;
import com.multiclone.app.domain.virtualization.CloneEnvironment;
import com.multiclone.app.domain.virtualization.ClonedAppInstaller;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Dagger/Hilt module that provides repository and virtualization dependencies
 */
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\u0012\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\"\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\bH\u0007\u00a8\u0006\r"}, d2 = {"Lcom/multiclone/app/di/RepositoryModule;", "", "()V", "provideCloneEnvironment", "Lcom/multiclone/app/domain/virtualization/CloneEnvironment;", "context", "Landroid/content/Context;", "provideClonedAppInstaller", "Lcom/multiclone/app/domain/virtualization/ClonedAppInstaller;", "provideVirtualAppEngine", "Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "cloneEnvironment", "clonedAppInstaller", "app_debug"})
@dagger.Module()
public final class RepositoryModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.multiclone.app.di.RepositoryModule INSTANCE = null;
    
    private RepositoryModule() {
        super();
    }
    
    /**
     * Provides the CloneEnvironment instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    @javax.inject.Singleton()
    public final com.multiclone.app.domain.virtualization.CloneEnvironment provideCloneEnvironment(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Provides the ClonedAppInstaller instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    @javax.inject.Singleton()
    public final com.multiclone.app.domain.virtualization.ClonedAppInstaller provideClonedAppInstaller(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Provides the VirtualAppEngine instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    @javax.inject.Singleton()
    public final com.multiclone.app.domain.virtualization.VirtualAppEngine provideVirtualAppEngine(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.CloneEnvironment cloneEnvironment, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.ClonedAppInstaller clonedAppInstaller) {
        return null;
    }
}