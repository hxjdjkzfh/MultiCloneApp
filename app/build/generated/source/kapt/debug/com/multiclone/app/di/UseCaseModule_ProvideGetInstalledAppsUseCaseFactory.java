package com.multiclone.app.di;

import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase;
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
public final class UseCaseModule_ProvideGetInstalledAppsUseCaseFactory implements Factory<GetInstalledAppsUseCase> {
  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public UseCaseModule_ProvideGetInstalledAppsUseCaseFactory(
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public GetInstalledAppsUseCase get() {
    return provideGetInstalledAppsUseCase(virtualAppEngineProvider.get());
  }

  public static UseCaseModule_ProvideGetInstalledAppsUseCaseFactory create(
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new UseCaseModule_ProvideGetInstalledAppsUseCaseFactory(virtualAppEngineProvider);
  }

  public static GetInstalledAppsUseCase provideGetInstalledAppsUseCase(
      VirtualAppEngine virtualAppEngine) {
    return Preconditions.checkNotNullFromProvides(UseCaseModule.INSTANCE.provideGetInstalledAppsUseCase(virtualAppEngine));
  }
}
