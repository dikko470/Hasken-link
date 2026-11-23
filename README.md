# HASKEN LINK — Offline Android messaging starter (v0.1)

This starter app demonstrates a basic Bluetooth Classic RFCOMM text chat between **already paired Android devices**. It does not use mobile data, internet, or airtime once Bluetooth is enabled.

## Build
1. Install Android Studio and open this folder (`HaskenLink`).
2. Allow Gradle sync; install SDK Platform 35 if prompted.
3. Build > Build APK(s), or run on two Android phones.
4. Pair the phones in Android Bluetooth settings first. Install/open the app on both, grant Bluetooth permissions, and tap the paired device to connect. One phone can accept the incoming connection while the other initiates it.

## Current limitations
- Prototype: direct connection only; not yet mesh/relay.
- Bluetooth pairing must be completed in Android Settings.
- No image/audio transfer, discovery, group chat, background service, message persistence, or end-to-end encryption yet.
- Connection/listener behavior varies by Android vendor and power-management settings.

## Roadmap
1. Improve discovery and connection UX.
2. Message persistence and identity/crypto.
3. Voice-note and image/file transfer using a framed transfer protocol.
4. BLE discovery plus Wi-Fi Direct transport.
5. Multi-hop store-and-forward mesh relay, hop limits, deduplication and expiry.
6. Emergency broadcast, group chats, accessibility and battery-aware operation.
