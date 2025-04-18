package com.multiclone.app.domain.usecase;

import android.graphics.Bitmap;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.data.repository.CloneRepository;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

/**
 * Use case for creating cloned applications
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J@\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\rH\u0086B\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000f\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b\u0019\u00a8\u0006\u0010"}, d2 = {"Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;", "", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "(Lcom/multiclone/app/data/repository/CloneRepository;)V", "invoke", "Lkotlin/Result;", "Lcom/multiclone/app/data/model/CloneInfo;", "appInfo", "Lcom/multiclone/app/data/model/AppInfo;", "displayName", "", "customIcon", "Landroid/graphics/Bitmap;", "invoke-BWLJW6A", "(Lcom/multiclone/app/data/model/AppInfo;Ljava/lang/String;Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class CreateCloneUseCase {
    private final com.multiclone.app.data.repository.CloneRepository cloneRepository = null;
    
    @javax.inject.Inject()
    public CreateCloneUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository) {
        super();
    }
}