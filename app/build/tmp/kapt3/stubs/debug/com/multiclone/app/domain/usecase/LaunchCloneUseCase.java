package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.repository.CloneRepository;
import javax.inject.Inject;

/**
 * Use case for launching a cloned application
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;", "", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "(Lcom/multiclone/app/data/repository/CloneRepository;)V", "invoke", "", "cloneId", "", "app_debug"})
public final class LaunchCloneUseCase {
    private final com.multiclone.app.data.repository.CloneRepository cloneRepository = null;
    
    @javax.inject.Inject()
    public LaunchCloneUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository) {
        super();
    }
    
    /**
     * Launch a cloned app
     */
    public final void invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
    }
}