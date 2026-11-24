# Minecraft Addons Hub

A free, ad-free Android app for discovering and importing Minecraft Bedrock add-ons.

## App structure
- Native Kotlin + Jetpack Compose
- Home, Explore, Downloads, Updates, and Settings
- Remote JSON catalog, independent of APK updates
- Download progress, retry, SHA-256 verification, and Android open/share import flow
- No direct access to Minecraft's private app storage
- No ads, subscriptions, or hidden paywalls

## Catalog integrity
An add-on must not be shown as downloadable until its real package files have been uploaded and verified. Each catalog entry must include package URLs, SHA-256, file size, Bedrock compatibility, screenshots, and a clear description. Use only original or properly licensed assets.

## Build status
Initial repository setup. App implementation and device import testing are still in progress.