package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.repository.AppRepository;
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
public final class GetInstalledAppsUseCase_Factory implements Factory<GetInstalledAppsUseCase> {
  private final Provider<AppRepository> appRepositoryProvider;

  public GetInstalledAppsUseCase_Factory(Provider<AppRepository> appRepositoryProvider) {
    this.appRepositoryProvider = appRepositoryProvider;
  }

  @Override
  public GetInstalledAppsUseCase get() {
    return newInstance(appRepositoryProvider.get());
  }

  public static GetInstalledAppsUseCase_Factory create(
      Provider<AppRepository> appRepositoryProvider) {
    return new GetInstalledAppsUseCase_Factory(appRepositoryProvider);
  }

  public static GetInstalledAppsUseCase newInstance(AppRepository appRepository) {
    return new GetInstalledAppsUseCase(appRepository);
  }
}
