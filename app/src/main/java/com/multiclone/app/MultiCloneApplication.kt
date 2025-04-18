package com.multiclone.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the MultiClone app
 * The @HiltAndroidApp annotation triggers Hilt's code generation
 */
@HiltAndroidApp
class MultiCloneApplication : Application()