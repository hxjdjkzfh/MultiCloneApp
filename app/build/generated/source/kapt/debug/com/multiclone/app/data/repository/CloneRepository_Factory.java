package com.multiclone.app.data.repository;

import android.content.Context;
import com.multiclone.app.core.virtualization.VirtualAppEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CloneRepository_Factory implements Factory<CloneRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public CloneRepository_Factory(Provider<Context> contextProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.contextProvider = contextProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  @Override
  public CloneRepository get() {
    return newInstance(contextProvider.get(), virtualAppEngineProvider.get());
  }

  public static CloneRepository_Factory create(Provider<Context> contextProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new CloneRepository_Factory(contextProvider, virtualAppEngineProvider);
  }

  public static CloneRepository newInstance(Context context, VirtualAppEngine virtualAppEngine) {
    return new CloneRepository(context, virtualAppEngine);
  }
}
