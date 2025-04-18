package com.multiclone.app.di;

import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.usecase.CreateShortcutUseCase;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class UseCaseModule_ProvideCreateShortcutUseCaseFactory implements Factory<CreateShortcutUseCase> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public UseCaseModule_ProvideCreateShortcutUseCaseFactory(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public CreateShortcutUseCase get() {
    return provideCreateShortcutUseCase(cloneRepositoryProvider.get(), virtualAppEngineProvider.get());
  }

  public static UseCaseModule_ProvideCreateShortcutUseCaseFactory create(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new UseCaseModule_ProvideCreateShortcutUseCaseFactory(cloneRepositoryProvider, virtualAppEngineProvider);
  }

  public static CreateShortcutUseCase provideCreateShortcutUseCase(CloneRepository cloneRepository,
      VirtualAppEngine virtualAppEngine) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideCreateShortcutUseCase(cloneRepository, virtualAppEngine));
  }
}
