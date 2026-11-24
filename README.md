# Minecraft Addons Hub

A free, ad-free Android app for discovering Minecraft Bedrock community add-ons.

## Implemented foundation
- Native Kotlin + Jetpack Compose app module
- Home, Explore, Downloads, Updates, and Settings navigation
- Remote JSON catalog loading from this repository
- Add-on search and detail dialog
- Android Download Manager downloads, Wi-Fi-only by default
- GitHub Actions debug APK build workflow

## Catalog format
The catalog lives at `catalog/addons.json`. Each published entry must point to a real HTTPS package and include a valid SHA-256 value, size, version, and Bedrock compatibility. The app currently validates the catalog entry format; **package checksum verification and the in-app Minecraft handoff are not implemented yet**.

Do not publish placeholder entries as downloadable add-ons. Only add original or properly licensed, real, tested package files.

## Current status
The Android foundation is committed. No APK has been verified yet, and the catalog is intentionally empty until actual add-on packages are hosted and tested on a physical device.
