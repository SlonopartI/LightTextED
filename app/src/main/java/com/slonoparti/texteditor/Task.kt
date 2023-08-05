package com.slonoparti.texteditor

import android.app.Activity
import java.io.File

open class Task {
    open lateinit var file: File
    open fun requirePermissions(context:Activity) {}
}