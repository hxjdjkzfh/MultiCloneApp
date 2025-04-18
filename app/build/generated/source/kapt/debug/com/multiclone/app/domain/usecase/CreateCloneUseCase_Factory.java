package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.repository.AppRepository;
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
public final class CreateCloneUseCase_Factory implements Factory<CreateCloneUseCase> {
  private final Provider<AppRepository> appRepositoryProvider;

  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public CreateCloneUseCase_Factory(Provider<AppRepository> appRepositoryProvider,
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.appRepositoryProvider = appRepositoryProvider;
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public CreateCloneUseCase get() {
    return newInstance(appRepositoryProvider.get(), cloneRepositoryProvider.get(), virtualAppEngineProvider.get());
  }

  public static CreateCloneUseCase_Factory create(Provider<AppRepository> appRepositoryProvider,
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new CreateCloneUseCase_Factory(appRepositoryProvider, cloneRepositoryProvider, virtualAppEngineProvider);
  }

  public static CreateCloneUseCase newInstance(AppRepository appRepository,
      CloneRepository cloneRepository, VirtualAppEngine virtualAppEngine) {
    return new CreateCloneUseCase(appRepository, cloneRepository, virtualAppEngine);
  }
}
