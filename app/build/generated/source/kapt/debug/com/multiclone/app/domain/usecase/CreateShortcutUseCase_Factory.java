package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class CreateShortcutUseCase_Factory implements Factory<CreateShortcutUseCase> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public CreateShortcutUseCase_Factory(Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public CreateShortcutUseCase get() {
    return newInstance(cloneRepositoryProvider.get(), virtualAppEngineProvider.get());
  }

  public static CreateShortcutUseCase_Factory create(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new CreateShortcutUseCase_Factory(cloneRepositoryProvider, virtualAppEngineProvider);
  }

  public static CreateShortcutUseCase newInstance(CloneRepository cloneRepository,
      VirtualAppEngine virtualAppEngine) {
    return new CreateShortcutUseCase(cloneRepository, virtualAppEngine);
  }
}
