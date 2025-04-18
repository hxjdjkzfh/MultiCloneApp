package com.multiclone.app.di;

import com.multiclone.app.data.repository.AppRepository;
import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.usecase.CreateCloneUseCase;
import com.multiclone.app.domain.usecase.CreateShortcutUseCase;
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase;
import com.multiclone.app.domain.usecase.LaunchCloneUseCase;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Dagger/Hilt module that provides use case dependencies
 */
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\nH\u0007J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/multiclone/app/di/UseCaseModule;", "", "()V", "provideCreateCloneUseCase", "Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;", "appRepository", "Lcom/multiclone/app/data/repository/AppRepository;", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "virtualAppEngine", "Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "provideCreateShortcutUseCase", "Lcom/multiclone/app/domain/usecase/CreateShortcutUseCase;", "provideGetInstalledAppsUseCase", "Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;", "provideLaunchCloneUseCase", "Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;", "app_debug"})
@dagger.Module()
public final class UseCaseModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.multiclone.app.di.UseCaseModule INSTANCE = null;
    
    private UseCaseModule() {
        super();
    }
    
    /**
     * Provides the GetInstalledAppsUseCase instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    public final com.multiclone.app.domain.usecase.GetInstalledAppsUseCase provideGetInstalledAppsUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine) {
        return null;
    }
    
    /**
     * Provides the CreateCloneUseCase instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    public final com.multiclone.app.domain.usecase.CreateCloneUseCase provideCreateCloneUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.AppRepository appRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine) {
        return null;
    }
    
    /**
     * Provides the CreateShortcutUseCase instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    public final com.multiclone.app.domain.usecase.CreateShortcutUseCase provideCreateShortcutUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine) {
        return null;
    }
    
    /**
     * Provides the LaunchCloneUseCase instance
     */
    @org.jetbrains.annotations.NotNull()
    @dagger.Provides()
    public final com.multiclone.app.domain.usecase.LaunchCloneUseCase provideLaunchCloneUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine) {
        return null;
    }
}