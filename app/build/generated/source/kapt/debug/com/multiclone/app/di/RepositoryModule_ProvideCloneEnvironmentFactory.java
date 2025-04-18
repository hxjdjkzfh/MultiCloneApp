package com.multiclone.app.di;

import android.content.Context;
import com.multiclone.app.domain.virtualization.CloneEnvironment;
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
public final class RepositoryModule_ProvideCloneEnvironmentFactory implements Factory<CloneEnvironment> {
  private final Provider<Context> contextProvider;

  public RepositoryModule_ProvideCloneEnvironmentFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CloneEnvironment get() {
    return provideCloneEnvironment(contextProvider.get());
  }

  public static RepositoryModule_ProvideCloneEnvironmentFactory create(
      Provider<Context> contextProvider) {
    return new RepositoryModule_ProvideCloneEnvironmentFactory(contextProvider);
  }

  public static CloneEnvironment provideCloneEnvironment(Context context) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideCloneEnvironment(context));
  }
}
