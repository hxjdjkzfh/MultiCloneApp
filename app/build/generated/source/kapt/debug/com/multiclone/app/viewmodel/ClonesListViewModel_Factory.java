package com.multiclone.app.viewmodel;

import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.usecase.LaunchCloneUseCase;
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
public final class ClonesListViewModel_Factory implements Factory<ClonesListViewModel> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<LaunchCloneUseCase> launchCloneUseCaseProvider;

  public ClonesListViewModel_Factory(Provider<CloneRepository> cloneRepositoryProvider,
      Provider<LaunchCloneUseCase> launchCloneUseCaseProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.launchCloneUseCaseProvider = launchCloneUseCaseProvider;
  }

  @Override
  public ClonesListViewModel get() {
    return newInstance(cloneRepositoryProvider.get(), launchCloneUseCaseProvider.get());
  }

  public static ClonesListViewModel_Factory create(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<LaunchCloneUseCase> launchCloneUseCaseProvider) {
    return new ClonesListViewModel_Factory(cloneRepositoryProvider, launchCloneUseCaseProvider);
  }

  public static ClonesListViewModel newInstance(CloneRepository cloneRepository,
      LaunchCloneUseCase launchCloneUseCase) {
    return new ClonesListViewModel(cloneRepository, launchCloneUseCase);
  }
}
