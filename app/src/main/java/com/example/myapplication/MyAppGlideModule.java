package com.example.myapplication;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

// New since Glide v4
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    // Disabling Manifest parsing is recommended for performance and to avoid conflicts
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
