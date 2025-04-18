package com.multiclone.app.viewmodel;

import com.multiclone.app.domain.usecase.DeleteCloneUseCase;
import com.multiclone.app.domain.usecase.GetClonesUseCase;
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
  private final Provider<GetClonesUseCase> getClonesUseCaseProvider;

  private final Provider<LaunchCloneUseCase> launchCloneUseCaseProvider;

  private final Provider<DeleteCloneUseCase> deleteCloneUseCaseProvider;

  public ClonesListViewModel_Factory(Provider<GetClonesUseCase> getClonesUseCaseProvider,
      Provider<LaunchCloneUseCase> launchCloneUseCaseProvider,
      Provider<DeleteCloneUseCase> deleteCloneUseCaseProvider) {
    this.getClonesUseCaseProvider = getClonesUseCaseProvider;
    this.launchCloneUseCaseProvider = launchCloneUseCaseProvider;
    this.deleteCloneUseCaseProvider = deleteCloneUseCaseProvider;
  }

  @Override
  public ClonesListViewModel get() {
    return newInstance(getClonesUseCaseProvider.get(), launchCloneUseCaseProvider.get(), deleteCloneUseCaseProvider.get());
  }

  public static ClonesListViewModel_Factory create(
      Provider<GetClonesUseCase> getClonesUseCaseProvider,
      Provider<LaunchCloneUseCase> launchCloneUseCaseProvider,
      Provider<DeleteCloneUseCase> deleteCloneUseCaseProvider) {
    return new ClonesListViewModel_Factory(getClonesUseCaseProvider, launchCloneUseCaseProvider, deleteCloneUseCaseProvider);
  }

  public static ClonesListViewModel newInstance(GetClonesUseCase getClonesUseCase,
      LaunchCloneUseCase launchCloneUseCase, DeleteCloneUseCase deleteCloneUseCase) {
    return new ClonesListViewModel(getClonesUseCase, launchCloneUseCase, deleteCloneUseCase);
  }
}
