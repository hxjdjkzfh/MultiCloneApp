package com.multiclone.app.data.repository;

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
public final class CloneRepository_Factory implements Factory<CloneRepository> {
  private final Provider<Context> contextProvider;

  public CloneRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CloneRepository get() {
    return newInstance(contextProvider.get());
  }

  public static CloneRepository_Factory create(Provider<Context> contextProvider) {
    return new CloneRepository_Factory(contextProvider);
  }

  public static CloneRepository newInstance(Context context) {
    return new CloneRepository(context);
  }
}
