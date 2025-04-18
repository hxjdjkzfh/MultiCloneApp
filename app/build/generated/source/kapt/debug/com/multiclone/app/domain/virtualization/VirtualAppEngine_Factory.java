package com.multiclone.app.domain.virtualization;

import android.content.Context;
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
public final class VirtualAppEngine_Factory implements Factory<VirtualAppEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<CloneEnvironment> cloneEnvironmentProvider;

  private final Provider<ClonedAppInstaller> clonedAppInstallerProvider;

  public VirtualAppEngine_Factory(Provider<Context> contextProvider,
      Provider<CloneEnvironment> cloneEnvironmentProvider,
      Provider<ClonedAppInstaller> clonedAppInstallerProvider) {
    this.contextProvider = contextProvider;
    this.cloneEnvironmentProvider = cloneEnvironmentProvider;
    this.clonedAppInstallerProvider = clonedAppInstallerProvider;
  }

  @Override
  public VirtualAppEngine get() {
    return newInstance(contextProvider.get(), cloneEnvironmentProvider.get(), clonedAppInstallerProvider.get());
  }

  public static VirtualAppEngine_Factory create(Provider<Context> contextProvider,
      Provider<CloneEnvironment> cloneEnvironmentProvider,
      Provider<ClonedAppInstaller> clonedAppInstallerProvider) {
    return new VirtualAppEngine_Factory(contextProvider, cloneEnvironmentProvider, clonedAppInstallerProvider);
  }

  public static VirtualAppEngine newInstance(Context context, CloneEnvironment cloneEnvironment,
      ClonedAppInstaller clonedAppInstaller) {
    return new VirtualAppEngine(context, cloneEnvironment, clonedAppInstaller);
  }
}
