# 🏠 Real Estate Android App

A full-featured Android application developed for a real estate agency, enabling users to browse, reserve, and favorite properties using either a local database or a RESTful API.

## 📱 Features

- **Welcome Screen**
  - Connect to external REST API for property categories.
  
- **Login & Registration**
  - Secure login with email/password.
  - "Remember Me" functionality using `SharedPreferences`.
  - Validated registration with fields like name, email, phone, country, and city.

- **Property Listings**
  - Browse properties by type (apartments, villas, lands).
  - Filter by location, price, or category.
  - Add to favorites or reserve directly.

- **Favorites & Reservations**
  - View and manage your favorite properties.
  - List of your current and past reservations.

- **Profile Management**
  - Update profile information and change password.
  - Upload profile pictures.

- **Admin Dashboard**
  - View and delete users.
  - View reservation statistics.
  - Add new admins.
  - Feature selected properties in special offers.

- **Contact Options**
  - Call, email, or locate the agency via Google Maps.

## 🧰 Technologies Used

- **Language**: Java / Kotlin  
- **Local Storage**: SQLite  
- **State Management**: SharedPreferences  
- **Network**: RESTful API Integration  
- **Architecture**: Modular with Fragments  
- **UI Components**: Navigation Drawer, Spinners, RecyclerView  
- **Animations**: Tween and frame animations for UI responsiveness  
- **Exception Handling**: Robust error and data validation  

## 🎨 UI/UX

- Clean and structured layout with responsive design.
- Compatible with **Pixel 3a XL (API Level 26)**.
- Interactive animations (e.g., favorites heart bounce, fade-ins).

## 📂 Project Structure

├── Welcome Screen
├── Login / Register
├── Home with Navigation Drawer
│ ├── Properties
│ ├── Reservations
│ ├── Favorites
│ ├── Profile
│ ├── Contact Us
├── Admin Panel
│ ├── User Management
│ ├── Statistics
│ ├── Feature Control


## 🧪 Testing & Compatibility

- Tested on **Pixel 3a XL** emulator.
- Targeted API: **Level 26 (Android 8.0)**.

## 🚀 Getting Started

1. Clone the repository.
2. Open in Android Studio.
3. Run on an emulator or physical device (API 26).
4. Use the static admin account:  
   **Email**: `admin@admin.com`  
   **Password**: `Admin123!`

## 📄 License

This project is academic and intended for educational use only.
