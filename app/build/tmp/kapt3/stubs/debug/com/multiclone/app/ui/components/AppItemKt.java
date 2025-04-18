package com.multiclone.app.ui.components;

import androidx.compose.material3.CardDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.style.TextOverflow;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.utils.IconUtils;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u001a:\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0010\b\u0002\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u00a8\u0006\f"}, d2 = {"AppItem", "", "appInfo", "Lcom/multiclone/app/data/model/AppInfo;", "onClick", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "CloneItem", "cloneInfo", "Lcom/multiclone/app/data/model/CloneInfo;", "onLongClick", "app_debug"})
public final class AppItemKt {
    
    /**
     * Reusable component for displaying an app item in a list
     */
    @androidx.compose.runtime.Composable()
    public static final void AppItem(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.AppInfo appInfo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Reusable component for displaying a cloned app item in a list
     */
    @androidx.compose.runtime.Composable()
    public static final void CloneItem(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.CloneInfo cloneInfo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLongClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}