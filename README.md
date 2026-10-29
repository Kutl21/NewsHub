
---

## 📦 Release Notes - Final PoE Submission

**Version:** 1.0.0 (Final)
**Release Date:** October 2026

This final version of NewsHub includes several major updates and innovative features implemented since the initial Part 2 prototype. The app now meets all requirements for the Final Portfolio of Evidence (PoE).

### ✨ Innovative Features Added in This Version:
*   **Free State Geolocation Weather:** Implemented a custom feature that detects the user's location and fetches live weather data (temperature, condition, wind speed) exclusively for Free State locations using the Open-Meteo API.
*   **Interactive Map & Directions:** Integrated a Free State map allowing users to select major cities (Bloemfontein, Welkom, etc.) and launch Google Maps turn-by-turn navigation via Android Intents.
*   **Real-time Search:** Added a search bar to the Headlines screen, allowing users to query the GNews API for specific keywords in real-time.
*   **Offline Mode & Synchronization:** Implemented a Room Database to cache news articles. Users can now read saved bookmarks and previously loaded articles without an internet connection.
*   **Multi-Language Support:** Added support for English, isiZulu, Sesotho, and Afrikaans via Android string resources.
*   **Firebase Complete Listener:** Upgraded authentication error handling to use robust `addOnCompleteListener` and `addOnFailureListener` calls, translating Firebase exceptions into user-friendly messages.

### 🛠️ Updates & Fixes Since Prototype:
*   **Fixed Registration Flow:** Users are now automatically logged out and redirected to the Login screen after registering, forcing a manual login to confirm password accuracy.
*   **Migrated News API:** Switched from NewsAPI to GNews API to ensure reliable, real-time South African news retrieval.
*   **Database Schema Update:** Upgraded the Room Database version and added TypeConverters to fix schema mismatch crashes and properly store complex objects.
*   **GitHub Actions CI/CD:** Implemented an automated build workflow that tests and builds the APK on every push using a Base64 encoded Firebase configuration.
