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
public final class CreateCloneUseCase_Factory implements Factory<CreateCloneUseCase> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  public CreateCloneUseCase_Factory(Provider<CloneRepository> cloneRepositoryProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
  }

  @Override
  public CreateCloneUseCase get() {
    return newInstance(cloneRepositoryProvider.get());
  }

  public static CreateCloneUseCase_Factory create(
      Provider<CloneRepository> cloneRepositoryProvider) {
    return new CreateCloneUseCase_Factory(cloneRepositoryProvider);
  }

  public static CreateCloneUseCase newInstance(CloneRepository cloneRepository) {
    return new CreateCloneUseCase(cloneRepository);
  }
}
