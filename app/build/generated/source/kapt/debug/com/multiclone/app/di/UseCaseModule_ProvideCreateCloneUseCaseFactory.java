package com.multiclone.app.di;

import com.multiclone.app.data.repository.AppRepository;
import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.usecase.CreateCloneUseCase;
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
public final class UseCaseModule_ProvideCreateCloneUseCaseFactory implements Factory<CreateCloneUseCase> {
  private final Provider<AppRepository> appRepositoryProvider;

  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public UseCaseModule_ProvideCreateCloneUseCaseFactory(
      Provider<AppRepository> appRepositoryProvider,
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.appRepositoryProvider = appRepositoryProvider;
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public CreateCloneUseCase get() {
    return provideCreateCloneUseCase(appRepositoryProvider.get(), cloneRepositoryProvider.get(), virtualAppEngineProvider.get());
  }

  public static UseCaseModule_ProvideCreateCloneUseCaseFactory create(
      Provider<AppRepository> appRepositoryProvider,
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new UseCaseModule_ProvideCreateCloneUseCaseFactory(appRepositoryProvider, cloneRepositoryProvider, virtualAppEngineProvider);
  }

  public static CreateCloneUseCase provideCreateCloneUseCase(AppRepository appRepository,
      CloneRepository cloneRepository, VirtualAppEngine virtualAppEngine) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideCreateCloneUseCase(appRepository, cloneRepository, virtualAppEngine));
  }
}
