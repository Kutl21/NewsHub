# 📰 NewsHub — News & Weather Aggregator

An Android news aggregator app built with Kotlin, Firebase, and REST APIs for the IIE Open-Source Coding module (Part 2).

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blueviolet)
![Android](https://img.shields.io/badge/Android-SDK%2034-green)
![Firebase](https://img.shields.io/badge/Backend-Firebase-orange)
![Gradle](https://img.shields.io/badge/Gradle-8.13-blue)

---

## 📖 Table of Contents

- [About the App](#-about-the-app)
- [Features](#-features)
- [Demo Video](#-demo-video)
- [Tech Stack](#-tech-stack)
- [APIs Used](#-apis-used)
- [Screenshots](#-screenshots)
- [Setup Instructions](#-setup-instructions)
- [Project Structure](#-project-structure)
- [GitHub Actions](#-github-actions)
- [Testing](#-testing)
- [AI Tools Usage](#-ai-tools-usage)
- [Authors](#-authors)
- [References](#-references)

---

## 📱 About the App

**NewsHub** is a fully-functional Android mobile application designed to give users convenient access to current South African news, local weather for the Free State province, and offline reading capabilities — all in one modern, user-friendly interface.

The app was developed as part of the **IIE Open-Source Coding (OPSC)** module and demonstrates practical application of:
- RESTful API integration
- Firebase Authentication and Cloud Firestore
- Local offline storage with Room database
- Modern Android architecture patterns (MVVM)
- Material Design 3 principles

The core innovation is a **Free State geolocation weather feature**, which displays live weather for users located within the Free State province using Android's location services and the Open-Meteo weather API.

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
| 12 | 📊 **Crashlytics & Analytics** | Firebase monitoring for stability |

---

## 🎬 Demo Video

**Watch the full app demonstration on YouTube:**

👉 **[NewsHub Demo Video] https://youtu.be/Qa4WQm8mwEE


The video demonstrates:
- User registration with email/password
- Google Sign-In flow
- Real-time news feed from GNews API
- Search functionality
- Category filtering
- Bookmark management
- Free State weather integration
- Google Maps directions
- Settings and dark mode
- Firebase Console showing encrypted user storage

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Kotlin 1.9.22 | Primary programming language |
| **UI** | Material Design 3 | Modern, accessible interface |
| **Layout Binding** | ViewBinding | Type-safe view access |
| **Architecture** | MVVM + Repository | Clean separation of concerns |
| **Async** | Coroutines + Flow | Non-blocking operations |
| **Network** | Retrofit 2.9 + OkHttp | RESTful API communication |
| **JSON** | Gson | Serialization/deserialization |
| **Local DB** | Room 2.7.0 | SQLite wrapper with KSP |
| **Auth** | Firebase Authentication | Secure user management |
| **Cloud DB** | Firebase Firestore | User profile storage |
| **Push** | Firebase Cloud Messaging | Notification support |
| **Monitoring** | Firebase Crashlytics | Crash reporting |
| **Images** | Glide 4.16 | Efficient image loading |
| **Background** | WorkManager | Persistent background tasks |
| **Maps** | Google Maps Intent | Location directions |

---

## 🌐 APIs Used

### 1. GNews API
- **Base URL:** `https://gnews.io/`
- **Endpoints:**
  - `/api/v4/top-headlines` — Top South African headlines
  - `/api/v4/search` — Keyword search
- **Purpose:** Primary news source
- **Free tier:** 100 requests/day
- **Documentation:** https://gnews.io/docs/v4

### 2. Open-Meteo Weather API
- **Base URL:** `https://api.open-meteo.com/`
- **Endpoint:** `/v1/forecast`
- **Purpose:** Free State weather data
- **Free tier:** No API key required
- **Documentation:** https://open-meteo.com/en/docs

### 3. Firebase Authentication
- **Purpose:** User registration, login, Google Sign-In
- **Encryption:** Industry-standard password hashing
- **Documentation:** https://firebase.google.com/docs/auth

### 4. Firebase Firestore
- **Purpose:** Store user profiles and preferences
- **Security:** Rule-based access control
- **Documentation:** https://firebase.google.com/docs/firestore

### 5. Google Maps (Intent-based)
- **Purpose:** Turn-by-turn navigation
- **Method:** `google.navigation:` URI intent
- **No API key required** for basic directions


## 🚀 Setup Instructions

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** installed
- **Android SDK 34** (API level 34)
- **Firebase project** with Authentication + Firestore enabled
- **GNews API key** (free from https://gnews.io)

### Step 1: Clone the Repository

```bash
git clone https://github.com/Kutl21/NewsHub.git
cd NewsHub
