package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.repository.CloneRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class LaunchCloneUseCase_Factory implements Factory<LaunchCloneUseCase> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  public LaunchCloneUseCase_Factory(Provider<CloneRepository> cloneRepositoryProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
  }

  @Override
  public LaunchCloneUseCase get() {
    return newInstance(cloneRepositoryProvider.get());
  }

  public static LaunchCloneUseCase_Factory create(
      Provider<CloneRepository> cloneRepositoryProvider) {
    return new LaunchCloneUseCase_Factory(cloneRepositoryProvider);
  }

  public static LaunchCloneUseCase newInstance(CloneRepository cloneRepository) {
    return new LaunchCloneUseCase(cloneRepository);
  }
}
