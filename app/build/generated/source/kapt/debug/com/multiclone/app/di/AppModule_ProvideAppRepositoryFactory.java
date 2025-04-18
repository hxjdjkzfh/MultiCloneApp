package com.multiclone.app.di;

import android.content.Context;
import com.multiclone.app.data.repository.AppRepository;
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
public final class AppModule_ProvideAppRepositoryFactory implements Factory<AppRepository> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideAppRepositoryFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppRepository get() {
    return provideAppRepository(contextProvider.get());
  }

  public static AppModule_ProvideAppRepositoryFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideAppRepositoryFactory(contextProvider);
  }

  public static AppRepository provideAppRepository(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAppRepository(context));
  }
}
