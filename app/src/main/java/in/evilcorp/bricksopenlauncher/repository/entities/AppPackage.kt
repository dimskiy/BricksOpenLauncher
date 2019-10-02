package `in`.evilcorp.bricksopenlauncher.repository.entities

import android.content.Intent
import android.graphics.drawable.Drawable

data class AppPackage(
        val pkgId: String,
        val title: String,
        val icon: Drawable,
        val launchIntent: Intent
)