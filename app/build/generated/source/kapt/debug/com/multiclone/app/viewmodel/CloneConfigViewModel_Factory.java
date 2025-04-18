package com.multiclone.app.viewmodel;

import androidx.lifecycle.SavedStateHandle;
import com.multiclone.app.data.repository.AppRepository;
import com.multiclone.app.domain.usecase.CreateCloneUseCase;
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
public final class CloneConfigViewModel_Factory implements Factory<CloneConfigViewModel> {
  private final Provider<AppRepository> appRepositoryProvider;

  private final Provider<CreateCloneUseCase> createCloneUseCaseProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public CloneConfigViewModel_Factory(Provider<AppRepository> appRepositoryProvider,
      Provider<CreateCloneUseCase> createCloneUseCaseProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.appRepositoryProvider = appRepositoryProvider;
    this.createCloneUseCaseProvider = createCloneUseCaseProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public CloneConfigViewModel get() {
    return newInstance(appRepositoryProvider.get(), createCloneUseCaseProvider.get(), savedStateHandleProvider.get());
  }

  public static CloneConfigViewModel_Factory create(Provider<AppRepository> appRepositoryProvider,
      Provider<CreateCloneUseCase> createCloneUseCaseProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new CloneConfigViewModel_Factory(appRepositoryProvider, createCloneUseCaseProvider, savedStateHandleProvider);
  }

  public static CloneConfigViewModel newInstance(AppRepository appRepository,
      CreateCloneUseCase createCloneUseCase, SavedStateHandle savedStateHandle) {
    return new CloneConfigViewModel(appRepository, createCloneUseCase, savedStateHandle);
  }
}
