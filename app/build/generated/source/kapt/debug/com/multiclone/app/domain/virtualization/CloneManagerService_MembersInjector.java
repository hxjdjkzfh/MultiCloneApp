package com.multiclone.app.domain.virtualization;

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
public final class CloneManagerService_MembersInjector implements MembersInjector<CloneManagerService> {
  private final Provider<CloneRepository> cloneRepositoryProvider;

  private final Provider<VirtualAppEngine> virtualAppEngineProvider;

  public CloneManagerService_MembersInjector(Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    this.cloneRepositoryProvider = cloneRepositoryProvider;
    this.virtualAppEngineProvider = virtualAppEngineProvider;
  }

  public static MembersInjector<CloneManagerService> create(
      Provider<CloneRepository> cloneRepositoryProvider,
      Provider<VirtualAppEngine> virtualAppEngineProvider) {
    return new CloneManagerService_MembersInjector(cloneRepositoryProvider, virtualAppEngineProvider);
  }

  @Override
  public void injectMembers(CloneManagerService instance) {
    injectCloneRepository(instance, cloneRepositoryProvider.get());
    injectVirtualAppEngine(instance, virtualAppEngineProvider.get());
  }

  @InjectedFieldSignature("com.multiclone.app.domain.virtualization.CloneManagerService.cloneRepository")
  public static void injectCloneRepository(CloneManagerService instance,
      CloneRepository cloneRepository) {
    instance.cloneRepository = cloneRepository;
  }

  @InjectedFieldSignature("com.multiclone.app.domain.virtualization.CloneManagerService.virtualAppEngine")
  public static void injectVirtualAppEngine(CloneManagerService instance,
      VirtualAppEngine virtualAppEngine) {
    instance.virtualAppEngine = virtualAppEngine;
  }
}
