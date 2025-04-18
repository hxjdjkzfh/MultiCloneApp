package com.multiclone.app.ui.screens;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.material3.TopAppBarDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.tooling.preview.Preview;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.ui.theme.MultiCloneTheme;
import com.multiclone.app.viewmodel.ClonesListViewModel;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001aN\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\nH\u0003\u001a.\u0010\r\u001a\u00020\u00012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\u001a\b\u0010\u0013\u001a\u00020\u0001H\u0003\u001a\u0012\u0010\u0014\u001a\u00020\u00012\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u0003\u00a8\u0006\u0017"}, d2 = {"ClonesListContent", "", "paddingValues", "Landroidx/compose/foundation/layout/PaddingValues;", "isLoading", "", "clones", "", "Lcom/multiclone/app/data/model/CloneInfo;", "onCloneClick", "Lkotlin/Function1;", "", "onCreateShortcut", "ClonesListScreen", "onBackPressed", "Lkotlin/Function0;", "navigateToCreateClone", "viewModel", "Lcom/multiclone/app/viewmodel/ClonesListViewModel;", "ClonesListScreenPreview", "EmptyClonesList", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class ClonesListScreenKt {
    
    /**
     * Screen that displays a list of all cloned apps
     */
    @androidx.compose.runtime.Composable()
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    public static final void ClonesListScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBackPressed, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> navigateToCreateClone, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.viewmodel.ClonesListViewModel viewModel) {
    }
    
    /**
     * Content for the clones list screen
     */
    @androidx.compose.runtime.Composable()
    private static final void ClonesListContent(androidx.compose.foundation.layout.PaddingValues paddingValues, boolean isLoading, java.util.List<com.multiclone.app.data.model.CloneInfo> clones, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCloneClick, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCreateShortcut) {
    }
    
    /**
     * Empty state when no clones are available
     */
    @androidx.compose.runtime.Composable()
    private static final void EmptyClonesList(androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Preview for the clones list screen
     */
    @androidx.compose.runtime.Composable()
    @androidx.compose.ui.tooling.preview.Preview(showBackground = true)
    private static final void ClonesListScreenPreview() {
    }
}