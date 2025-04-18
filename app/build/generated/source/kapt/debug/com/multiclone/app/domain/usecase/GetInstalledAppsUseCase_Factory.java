package com.multiclone.app.domain.usecase;

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
public final class GetInstalledAppsUseCase_Factory implements Factory<GetInstalledAppsUseCase> {
  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public GetInstalledAppsUseCase_Factory(Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public GetInstalledAppsUseCase get() {
    return newInstance(virtualAppEngineProvider.get());
  }

  public static GetInstalledAppsUseCase_Factory create(
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new GetInstalledAppsUseCase_Factory(virtualAppEngineProvider);
  }

  public static GetInstalledAppsUseCase newInstance(VirtualAppEngine virtualAppEngine) {
    return new GetInstalledAppsUseCase(virtualAppEngine);
  }
}
