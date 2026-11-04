# 📰 NewsHub — News & Weather Aggregator

An Android news aggregator app built with Kotlin, Firebase, and REST APIs for the IIE Open-Source Coding module (Part 3 Final PoE).

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blueviolet)
![Android](https://img.shields.io/badge/Android-SDK%2034-green)
![Firebase](https://img.shields.io/badge/Backend-Firebase-orange)

---

## 📖 About the App

**NewsHub** is a fully-functional Android mobile application designed to give users convenient access to current South African news, local weather for the Free State province, and offline reading capabilities — all in one modern, user-friendly interface.

The app was developed as part of the **IIE Open-Source Coding (OPSC)** module and demonstrates practical application of:
- RESTful API integration
- Firebase Authentication and Cloud Firestore
- Local offline storage with Room database
- Modern Android architecture patterns (MVVM)
- Material Design 3 principles

The core innovation is a **Free State geolocation weather feature**, which displays live weather for users located within the Free State province using Android's location services and the Open-Meteo weather API.

---

## 🎬 Demo Video

**Watch the full app demonstration on YouTube:**

👉 **[NewsHub Demo Video](PASTE_YOUR_YOUTUBE_LINK_HERE)** 👈

*(The video demonstrates the app running on a physical mobile phone as required.)*

---

## ✨ Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | 🔐 **User Authentication** | Email/password registration and login via Firebase Authentication |
| 2 | 🔵 **Google Sign-In** | One-tap login using Google account (OAuth 2.0) |
| 3 | 📰 **Real-Time News** | Live South African headlines fetched from GNews API |
| 4 | 🔍 **Search** | Search for articles by keyword |
| 5 | 📂 **Categories** | Browse news by General, Technology, Business, Sports, Science, Entertainment, Health |
| 6 | 📌 **Bookmarks** | Save articles for later reading (persists offline) |
| 7 | 🌤️ **Free State Weather** | Location-based weather for Bloemfontein, Welkom, Bethlehem, Kroonstad, Sasolburg |
| 8 | 🗺️ **Map & Directions** | Interactive map with Google Maps navigation to Free State cities |
| 9 | ⚙️ **Settings** | Dark mode, language selection, notification preferences |
| 10 | 💾 **Offline Mode** | Room database caches articles for offline reading |
| 11 | 🌍 **Multi-language** | English, isiZulu, Sesotho, Afrikaans |

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Kotlin | Primary programming language |
| **UI** | Material Design 3 | Modern, accessible interface |
| **Architecture** | MVVM + Repository | Clean separation of concerns |
| **Network** | Retrofit + OkHttp | RESTful API communication |
| **Local DB** | Room 2.7.0 (KSP) | SQLite wrapper |
| **Auth** | Firebase Authentication | Secure user management |
| **Cloud DB** | Firebase Firestore | User profile storage |
| **Images** | Glide | Efficient image loading |
| **Maps** | Google Maps Intent | Location directions |

---

## 📦 Release Notes - Final PoE Submission

**Version:** 1.0.0 (Final)
**Release Date:** October 2026

This final version of NewsHub includes several major updates and innovative features implemented since the initial Part 2 prototype.

### ✨ Innovative Features Added in This Version:
*   **Free State Geolocation Weather:** Implemented a custom feature that detects the user's location and fetches live weather data exclusively for Free State locations using the Open-Meteo API.
*   **Interactive Map & Directions:** Integrated a Free State map allowing users to select major cities and launch Google Maps turn-by-turn navigation.
*   **Real-time Search:** Added a search bar to the Headlines screen, allowing users to query the GNews API for specific keywords.
*   **Offline Mode & Synchronization:** Implemented a Room Database to cache news articles. Users can now read saved bookmarks and previously loaded articles without an internet connection.
*   **Multi-Language Support:** Added support for English, isiZulu, Sesotho, and Afrikaans.
*   **Firebase Complete Listener:** Upgraded authentication error handling to use robust `addOnCompleteListener` and `addOnFailureListener` calls.

### 🛠️ Updates & Fixes Since Prototype:
*   **Fixed Registration Flow:** Users are now automatically logged out and redirected to the Login screen after registering.
*   **UI Overhaul:** Fixed an issue on physical devices where Login/Register text fields were invisible due to dark mode conflicts.
*   **Article Reading:** Fixed the article click listener. Tapping an article now opens the URL in an Android Custom Tab.
*   **Migrated News API:** Switched from NewsAPI to GNews API to ensure reliable, real-time South African news retrieval.
*   **GitHub Actions CI/CD:** Implemented an automated build workflow that tests and builds the APK on every push.

---

## ⚙️ GitHub Actions

This project uses **GitHub Actions** to automatically build the APK on every push. The workflow file is located at `.github/workflows/build.yml`.

---

## 👥 Authors

- **Kutlwano Motlhale** (ST10448945)
- **Silindelo Thabani Ngwenya** (ST10451484)
- **Liyema Dakana** (ST10443706)

**Module:** Open-Source Coding (OPSC) — Final PoE
**Institution:** Independent Institute of Education (IIE)
