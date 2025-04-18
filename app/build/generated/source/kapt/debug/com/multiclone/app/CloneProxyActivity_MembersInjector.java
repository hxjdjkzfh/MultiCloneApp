package com.multiclone.app;

import com.multiclone.app.data.repository.CloneRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CloneProxyActivity_MembersInjector implements MembersInjector<CloneProxyActivity> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  public CloneProxyActivity_MembersInjector(Provider<CloneRepository> cloneRepositoryProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
  }

  public static MembersInjector<CloneProxyActivity> create(
      Provider<CloneRepository> cloneRepositoryProvider) {
    return new CloneProxyActivity_MembersInjector(cloneRepositoryProvider);
  }

  @Override
  public void injectMembers(CloneProxyActivity instance) {
    injectCloneRepository(instance, cloneRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.multiclone.app.CloneProxyActivity.cloneRepository")
  public static void injectCloneRepository(CloneProxyActivity instance,
      CloneRepository cloneRepository) {
    instance.cloneRepository = cloneRepository;
  }
}
