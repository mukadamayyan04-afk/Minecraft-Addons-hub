# Minecraft Addons Hub

A free, ad-free native Android app for Minecraft Bedrock community add-ons.

## Current app work
- Custom dark teal / mint interface and launcher icon
- Home, Explore, Downloads and Settings navigation
- Searchable collection of 15 planned add-on themes
- Remote JSON catalog support for verified package entries
- DownloadManager package downloads restricted to Wi-Fi by default
- SHA-256 verification after download
- Android notification handoff that opens a verified package through the system app chooser / Minecraft file handler
- No direct access to Minecraft private folders

## Catalog rules
The catalog lives at `catalog/addons.json`. An entry is downloadable only when it has a real HTTPS `.mcaddon` or `.mcpack` package URL and a valid 64-character SHA-256 digest. Package files must be genuine, tested, and original or properly licensed.

## Important current limitation
The 15 cards currently represent the planned collection; they are not all downloadable packages yet. The remote catalog is still empty until actual package files are sourced, checked for permission to redistribute, verified, and tested for Bedrock compatibility. Do not treat a planned card as a published add-on.

The Android build can validate app code, but successful import on a physical Redmi device still requires an actual device test with Minecraft installed.
