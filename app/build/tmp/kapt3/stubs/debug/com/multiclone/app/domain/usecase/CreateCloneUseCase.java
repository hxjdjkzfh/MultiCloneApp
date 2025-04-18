package com.multiclone.app.domain.usecase;

import android.graphics.Bitmap;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.data.repository.AppRepository;
import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

/**
 * Use case for creating a new clone of an app
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJB\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0086B\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000f\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b\u0019\u00a8\u0006\u0013"}, d2 = {"Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;", "", "appRepository", "Lcom/multiclone/app/data/repository/AppRepository;", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "virtualAppEngine", "Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "(Lcom/multiclone/app/data/repository/AppRepository;Lcom/multiclone/app/data/repository/CloneRepository;Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;)V", "invoke", "Lkotlin/Result;", "Lcom/multiclone/app/data/model/CloneInfo;", "packageName", "", "customName", "customIcon", "Landroid/graphics/Bitmap;", "invoke-BWLJW6A", "(Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class CreateCloneUseCase {
    private final com.multiclone.app.data.repository.AppRepository appRepository = null;
    private final com.multiclone.app.data.repository.CloneRepository cloneRepository = null;
    private final com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine = null;
    
    @javax.inject.Inject()
    public CreateCloneUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.AppRepository appRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine) {
        super();
    }
}