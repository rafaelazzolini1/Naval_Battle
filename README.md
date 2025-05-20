# Naval Battle

An Android multiplayer application inspired by the classic battleship game, developed using **Jetpack Compose** and **Firebase**. Features include **interactive mines**, **turn timer**, **dynamic themes**, and **multilingual support**.

## 📱 Platform

- Android with Jetpack Compose
- Firebase Authentication, Realtime Database, and Firestore

## 🎯 Project Objective

During the **Construction** phase, the main goal was to deliver a functional application with an optimized user experience, supporting:
- Dynamic themes (light/dark based on ambient light sensor)
- Sensory interaction (sound and vibration)
- Scalable navigation
- Full translation to **Spanish** and **English**

---

## 🧩 Technical Decisions

### Frontend

#### 🔀 Navigation
- Managed with `NavHost` and `NavController`
- Integrated with `ViewModels`: `LoginViewModel`, `MenuViewModel`, `GameViewModel`

#### 🧱 Components
- Reusable components (`CustomButton`, `CustomTextField`, `GameBoard`)
- Support for dynamic themes (`isLightTheme`), animations, and responsive layouts

#### 🖼️ Screens
- `LoginScreen`: login, registration, password reset, error feedback
- `MenuScreen`: mine (1-10) and time (1-10s) configuration, tutorial
- `GameScreen`: game board, turn timer (`LaunchedEffect`), sound, vibration, end game dialog

#### 🌗 Dynamic Theme
- `NavalBattleTheme` + `LightSensorManager` to detect ambient lux
- Auto and manual switching between light and dark modes

---

### Backend (Firebase)

#### 🔐 Authentication
- Login with email/password and Google
- Email verification and password reset

#### 💾 Storage
- Moves stored in `Realtime Database`
- Game summaries stored in `Firestore` using a unique `UUID` match ID

---

## 🔧 Improvements Implemented

- ⏱️ Adjustable turn timer
- 💣 Mines integrated into game logic with visual and sound effects
- 🎨 Enhanced UX through sound, vibration, and theme adaptation
- 🌍 Full string translation to **English** and **Spanish**

---

## 🏁 Current Status

✔️ Construction phase completed  
📦 Ready for integration and final testing

---

## 📍 Developers

- Rafael Azzolini  
- Raphael Augusto Santos

---

## 📅 Location

Lleida, Catalonia  
2025
