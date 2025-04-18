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
public final class ClonedAppInstaller_Factory implements Factory<ClonedAppInstaller> {
  private final Provider<Context> contextProvider;

  public ClonedAppInstaller_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ClonedAppInstaller get() {
    return newInstance(contextProvider.get());
  }

  public static ClonedAppInstaller_Factory create(Provider<Context> contextProvider) {
    return new ClonedAppInstaller_Factory(contextProvider);
  }

  public static ClonedAppInstaller newInstance(Context context) {
    return new ClonedAppInstaller(context);
  }
}
