package com.multiclone.app.viewmodel;

import com.multiclone.app.domain.usecase.CreateCloneUseCase;
import com.multiclone.app.domain.usecase.CreateShortcutUseCase;
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
public final class CloneConfigViewModel_Factory implements Factory<CloneConfigViewModel> {
  private final Provider<CreateCloneUseCase> createCloneUseCaseProvider;

  private final Provider<CreateShortcutUseCase> createShortcutUseCaseProvider;

  private final Provider<LaunchCloneUseCase> launchCloneUseCaseProvider;

  public CloneConfigViewModel_Factory(Provider<CreateCloneUseCase> createCloneUseCaseProvider,
      Provider<CreateShortcutUseCase> createShortcutUseCaseProvider,
      Provider<LaunchCloneUseCase> launchCloneUseCaseProvider) {
    this.createCloneUseCaseProvider = createCloneUseCaseProvider;
    this.createShortcutUseCaseProvider = createShortcutUseCaseProvider;
    this.launchCloneUseCaseProvider = launchCloneUseCaseProvider;
  }

  @Override
  public CloneConfigViewModel get() {
    return newInstance(createCloneUseCaseProvider.get(), createShortcutUseCaseProvider.get(), launchCloneUseCaseProvider.get());
  }

  public static CloneConfigViewModel_Factory create(
      Provider<CreateCloneUseCase> createCloneUseCaseProvider,
      Provider<CreateShortcutUseCase> createShortcutUseCaseProvider,
      Provider<LaunchCloneUseCase> launchCloneUseCaseProvider) {
    return new CloneConfigViewModel_Factory(createCloneUseCaseProvider, createShortcutUseCaseProvider, launchCloneUseCaseProvider);
  }

  public static CloneConfigViewModel newInstance(CreateCloneUseCase createCloneUseCase,
      CreateShortcutUseCase createShortcutUseCase, LaunchCloneUseCase launchCloneUseCase) {
    return new CloneConfigViewModel(createCloneUseCase, createShortcutUseCase, launchCloneUseCase);
  }
}
