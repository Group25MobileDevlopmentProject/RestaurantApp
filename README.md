# 🍽️ The Grand Spot – Restaurant App

A modern restaurant/pub app built with Jetpack Compose and Firebase, featuring real-time table ordering, admin-powered menu management, and a recipe marketplace.

---

## 📱 Features

### 🧾 Customer Interface
- Browse and filter a dynamic menu
- Real-time order placement from the table
- View upcoming events and RSVP
- Purchase chef-created recipes (coming soon)

### 🔐 Admin Panel
- 🔄 **Firebase CRUD** for managing menu items in real-time
- Add, edit, and remove items instantly without redeploying the app
- Clean and organized UI with categories, tags, and dietary filters

---

## 🔧 Tech Stack

- **Kotlin + Jetpack Compose** – Modern Android UI toolkit
- **Firebase Firestore** – Real-time database for menu & events
- **Firebase Auth** – Secure login for admins
- **Material3 Design** – Sleek and consistent UI/UX
- **Coil** – Image loading from URLs
- **GitHub Projects** – Agile Kanban boards & team planning

---

## 🚀 Advanced Feature: Firebase CRUD – Menu Management

This feature allows real-time creation, retrieval, update, and deletion of menu items via an admin interface. All changes reflect immediately on the customer-facing interface.

### ✨ Key Functionalities

| Action   | Description |
|----------|-------------|
| **Create** | Add menu items with name, description, price, tags, and dietary info |
| **Read**   | Fetch and display all menu items with filters |
| **Update** | Modify item details live |
| **Delete** | Remove items from the menu instantly |

---

## ⚙️ Setup Guide

### 1. Firebase Configuration
- Go to [Firebase Console](https://console.firebase.google.com)
- Create a new project and add your Android app
- Download `google-services.json` and place it in `/app/`

### 2. Add Dependencies

**Project-level `build.gradle`:**
```kotlin
id("com.google.gms.google-services") version "4.4.2" apply false
```

**App-level `build.gradle`:**
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
