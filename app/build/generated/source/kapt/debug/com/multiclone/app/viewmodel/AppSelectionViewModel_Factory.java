package com.multiclone.app.viewmodel;

import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase;
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
public final class AppSelectionViewModel_Factory implements Factory<AppSelectionViewModel> {
  private final Provider<GetInstalledAppsUseCase> getInstalledAppsUseCaseProvider;

  public AppSelectionViewModel_Factory(
      Provider<GetInstalledAppsUseCase> getInstalledAppsUseCaseProvider) {
    this.getInstalledAppsUseCaseProvider = getInstalledAppsUseCaseProvider;
  }

  @Override
  public AppSelectionViewModel get() {
    return newInstance(getInstalledAppsUseCaseProvider.get());
  }

  public static AppSelectionViewModel_Factory create(
      Provider<GetInstalledAppsUseCase> getInstalledAppsUseCaseProvider) {
    return new AppSelectionViewModel_Factory(getInstalledAppsUseCaseProvider);
  }

  public static AppSelectionViewModel newInstance(GetInstalledAppsUseCase getInstalledAppsUseCase) {
    return new AppSelectionViewModel(getInstalledAppsUseCase);
  }
}
