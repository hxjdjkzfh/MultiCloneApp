package com.multiclone.app.di;

import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.usecase.LaunchCloneUseCase;
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
public final class UseCaseModule_ProvideLaunchCloneUseCaseFactory implements Factory<LaunchCloneUseCase> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public UseCaseModule_ProvideLaunchCloneUseCaseFactory(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public LaunchCloneUseCase get() {
    return provideLaunchCloneUseCase(cloneRepositoryProvider.get(), virtualAppEngineProvider.get());
  }

  public static UseCaseModule_ProvideLaunchCloneUseCaseFactory create(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new UseCaseModule_ProvideLaunchCloneUseCaseFactory(cloneRepositoryProvider, virtualAppEngineProvider);
  }

  public static LaunchCloneUseCase provideLaunchCloneUseCase(CloneRepository cloneRepository,
      VirtualAppEngine virtualAppEngine) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideLaunchCloneUseCase(cloneRepository, virtualAppEngine));
  }
}
