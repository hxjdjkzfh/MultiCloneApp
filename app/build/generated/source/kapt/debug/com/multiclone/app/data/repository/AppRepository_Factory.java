package com.multiclone.app.data.repository;

import com.multiclone.app.core.virtualization.VirtualAppEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppRepository_Factory implements Factory<AppRepository> {
  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public AppRepository_Factory(Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public AppRepository get() {
    return newInstance(virtualAppEngineProvider.get());
  }

  public static AppRepository_Factory create(Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new AppRepository_Factory(virtualAppEngineProvider);
  }

  public static AppRepository newInstance(VirtualAppEngine virtualAppEngine) {
    return new AppRepository(virtualAppEngine);
  }
}
