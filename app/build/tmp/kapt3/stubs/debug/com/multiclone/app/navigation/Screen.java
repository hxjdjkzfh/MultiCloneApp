package com.multiclone.app.navigation;

import androidx.compose.runtime.Composable;
import androidx.navigation.NavHostController;
import androidx.navigation.NavType;

/**
 * Main navigation routes for the app
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0004\u0007\b\t\nB\u000f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\u0004\u000b\f\r\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/multiclone/app/navigation/Screen;", "", "route", "", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "AppSelection", "CloneConfig", "ClonesList", "Home", "Lcom/multiclone/app/navigation/Screen$AppSelection;", "Lcom/multiclone/app/navigation/Screen$CloneConfig;", "Lcom/multiclone/app/navigation/Screen$ClonesList;", "Lcom/multiclone/app/navigation/Screen$Home;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    
    private Screen(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/multiclone/app/navigation/Screen$Home;", "Lcom/multiclone/app/navigation/Screen;", "()V", "app_debug"})
    public static final class Home extends com.multiclone.app.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.multiclone.app.navigation.Screen.Home INSTANCE = null;
        
        private Home() {
            super(null);
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/multiclone/app/navigation/Screen$ClonesList;", "Lcom/multiclone/app/navigation/Screen;", "()V", "app_debug"})
    public static final class ClonesList extends com.multiclone.app.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.multiclone.app.navigation.Screen.ClonesList INSTANCE = null;
        
        private ClonesList() {
            super(null);
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/multiclone/app/navigation/Screen$AppSelection;", "Lcom/multiclone/app/navigation/Screen;", "()V", "app_debug"})
    public static final class AppSelection extends com.multiclone.app.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.multiclone.app.navigation.Screen.AppSelection INSTANCE = null;
        
        private AppSelection() {
            super(null);
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004\u00a8\u0006\u0006"}, d2 = {"Lcom/multiclone/app/navigation/Screen$CloneConfig;", "Lcom/multiclone/app/navigation/Screen;", "()V", "createRoute", "", "packageName", "app_debug"})
    public static final class CloneConfig extends com.multiclone.app.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.multiclone.app.navigation.Screen.CloneConfig INSTANCE = null;
        
        private CloneConfig() {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(@org.jetbrains.annotations.NotNull()
        java.lang.String packageName) {
            return null;
        }
    }
}