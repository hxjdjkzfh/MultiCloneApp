package com.multiclone.app.di;

import android.content.Context;
import com.multiclone.app.domain.virtualization.CloneEnvironment;
import com.multiclone.app.domain.virtualization.ClonedAppInstaller;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class RepositoryModule_ProvideVirtualAppEngineFactory implements Factory<VirtualAppEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<CloneEnvironment> cloneEnvironmentProvider;

  private final Provider<ClonedAppInstaller> clonedAppInstallerProvider;

  public RepositoryModule_ProvideVirtualAppEngineFactory(Provider<Context> contextProvider,
      Provider<CloneEnvironment> cloneEnvironmentProvider,
      Provider<ClonedAppInstaller> clonedAppInstallerProvider) {
    this.contextProvider = contextProvider;
    this.cloneEnvironmentProvider = cloneEnvironmentProvider;
    this.clonedAppInstallerProvider = clonedAppInstallerProvider;
  }

  @Override
  public VirtualAppEngine get() {
    return provideVirtualAppEngine(contextProvider.get(), cloneEnvironmentProvider.get(), clonedAppInstallerProvider.get());
  }

  public static RepositoryModule_ProvideVirtualAppEngineFactory create(
      Provider<Context> contextProvider, Provider<CloneEnvironment> cloneEnvironmentProvider,
      Provider<ClonedAppInstaller> clonedAppInstallerProvider) {
    return new RepositoryModule_ProvideVirtualAppEngineFactory(contextProvider, cloneEnvironmentProvider, clonedAppInstallerProvider);
  }

  public static VirtualAppEngine provideVirtualAppEngine(Context context,
      CloneEnvironment cloneEnvironment, ClonedAppInstaller clonedAppInstaller) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideVirtualAppEngine(context, cloneEnvironment, clonedAppInstaller));
  }
}
