package com.multiclone.app.di;

import android.content.Context;
import com.multiclone.app.domain.virtualization.ClonedAppInstaller;
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
public final class RepositoryModule_ProvideClonedAppInstallerFactory implements Factory<ClonedAppInstaller> {
  private final Provider<Context> contextProvider;

  public RepositoryModule_ProvideClonedAppInstallerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ClonedAppInstaller get() {
    return provideClonedAppInstaller(contextProvider.get());
  }

  public static RepositoryModule_ProvideClonedAppInstallerFactory create(
      Provider<Context> contextProvider) {
    return new RepositoryModule_ProvideClonedAppInstallerFactory(contextProvider);
  }

  public static ClonedAppInstaller provideClonedAppInstaller(Context context) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideClonedAppInstaller(context));
  }
}
