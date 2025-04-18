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
public final class CloneEnvironment_Factory implements Factory<CloneEnvironment> {
  private final Provider<Context> contextProvider;

  public CloneEnvironment_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CloneEnvironment get() {
    return newInstance(contextProvider.get());
  }

  public static CloneEnvironment_Factory create(Provider<Context> contextProvider) {
    return new CloneEnvironment_Factory(contextProvider);
  }

  public static CloneEnvironment newInstance(Context context) {
    return new CloneEnvironment(context);
  }
}
