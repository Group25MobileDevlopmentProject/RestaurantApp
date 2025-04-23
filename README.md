# 🍽️ The Grand Spot Restaurant App

A modern mobile restaurant app with real-time menu management using Firebase Firestore. This project enables restaurant staff to **Create, Read, Update, and Delete (CRUD)** menu items through an admin panel, with changes immediately reflected in the customer-facing app.

---

## 🚀 Features

- **📋 Create** – Add new menu items with name, description, category, price, dietary info, and image
- **🔍 Read** – Fetch and display all menu items, categorized and filterable
- **✏️ Update** – Modify item details in real-time
- **🗑️ Delete** – Remove items no longer offered

---

## 🔧 Setup & Configuration

### 1. Firebase Setup
- Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
- Register your Android app with your package name (e.g., `com.example.restaurantapp`)
- Download `google-services.json` and place it in your `app/` directory

### 2. Dependencies

#### Project-level `build.gradle`
```kotlin
id("com.google.gms.google-services") version "4.4.2" apply false
```

#### App-level `build.gradle`
```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
}
```
