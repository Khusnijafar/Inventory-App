# Inventory App Test

Aplikasi Android untuk manajemen inventory yang menggunakan Kotlin, Jetpack Compose, dan mengikuti arsitektur MVVM.

## Fitur

- Login dengan autentikasi API
- Tampilan daftar item inventory
- Penyimpanan data lokal menggunakan Room Database
- Integrasi dengan API eksternal menggunakan Retrofit
- UI modern menggunakan Jetpack Compose
- Arsitektur MVVM untuk manajemen state dan business logic

## Teknologi yang Digunakan

- Kotlin
- Jetpack Compose
- MVVM Architecture
- Room Database
- Retrofit
- Coroutines
- LiveData
- Material Design 3

## Persyaratan Sistem

- Android Studio Hedgehog | 2023.1.1 atau lebih baru
- JDK 11 atau lebih baru
- Android SDK 24 (Android 7.0) atau lebih baru
- Gradle 8.10.2 atau lebih baru

## Setup Project

1. Clone repository ini
2. Buka project di Android Studio
3. Sync project dengan Gradle
4. Jalankan aplikasi di emulator atau device fisik

## Struktur Project

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/inventoryapptest/
│   │   │   ├── data/
│   │   │   │   ├── api/         # API related classes
│   │   │   │   ├── local/       # Room Database classes
│   │   │   │   ├── model/       # Data models
│   │   │   │   └── repository/  # Repository classes
│   │   │   └── ui/
│   │   │       ├── login/       # Login screen
│   │   │       └── main/        # Main screen
│   │   └── res/                 # Resources
│   └── test/                    # Unit tests
└── build.gradle                 # App level build config
```

## API Endpoints

- Login: `POST /api/dev/login`
- Get Items: `GET /api/dev/list-items`

## Konfigurasi

Aplikasi menggunakan Retrofit untuk komunikasi dengan API. Base URL dapat diatur di `RetrofitClient.kt`.

## Lisensi

MIT License 