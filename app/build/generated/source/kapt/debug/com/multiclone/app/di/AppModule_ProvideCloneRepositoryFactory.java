package com.multiclone.app.di;

import android.content.Context;
import com.multiclone.app.data.repository.CloneRepository;
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
public final class AppModule_ProvideCloneRepositoryFactory implements Factory<CloneRepository> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideCloneRepositoryFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CloneRepository get() {
    return provideCloneRepository(contextProvider.get());
  }

  public static AppModule_ProvideCloneRepositoryFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideCloneRepositoryFactory(contextProvider);
  }

  public static CloneRepository provideCloneRepository(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCloneRepository(context));
  }
}
