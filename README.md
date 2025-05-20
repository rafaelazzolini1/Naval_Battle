# Naval Battle

Aplicació Android multiusuari inspirada en el clàssic joc de batalla naval, desenvolupada amb **Jetpack Compose** i **Firebase**. Inclou funcionalitats avançades com **mines interactives**, **control de temps**, **temes dinàmics** i **suport multilingüe**.

## 📱 Plataforma

- Android amb Jetpack Compose
- Firebase Authentication, Realtime Database i Firestore

## 🎯 Objectiu del Projecte

Durant la fase de **Construcció**, l’objectiu principal ha estat lliurar una aplicació funcional amb una experiència d’usuari optimitzada, suportant:
- Temes dinàmics (llum/fosc segons sensors de llum)
- Interacció sensorial (so i vibració)
- Navegació clara i escalable
- Traducció completa al **castellà** i **anglès**

---

## 🧩 Decisions Tècniques

### Frontend

#### 🔀 Navegació
- Gestionada amb `NavHost` i `NavController`
- Integració amb `ViewModels`: `LoginViewModel`, `MenuViewModel`, `GameViewModel`

#### 🧱 Components
- Components reutilitzables (`CustomButton`, `CustomTextField`, `GameBoard`)
- Suport a temes dinàmics (`isLightTheme`), animacions i responsivitat

#### 🖼️ Pantalles
- `LoginScreen`: inici de sessió, registre, restabliment, feedback d’errors
- `MenuScreen`: configuració de mines (1-10) i temps (1-10s), tutorial
- `GameScreen`: tauler, temporitzador (`LaunchedEffect`), so, vibració, fi de partida

#### 🌗 Tema Dinàmic
- `NavalBattleTheme` + `LightSensorManager` per detectar lux ambientals
- Canvi automàtic o manual entre mode clar/fosc

---

### Backend (Firebase)

#### 🔐 Autenticació
- Inici de sessió amb email/contrasenya i Google
- Verificació per correu i restabliment de contrasenya

#### 💾 Emmagatzematge
- Jugades en `Realtime Database`
- Resultats resumits a `Firestore` amb `UUID` per a cada partida

---

## 🔧 Millores Realitzades

- ⏱️ Control de temps ajustable entre torns
- 💣 Mines integrades amb efectes visuals i sonors
- 🎨 UX enriquida amb sons, vibració i adaptació temàtica dinàmica
- 🌍 Traducció completa de totes les cadenes (**strings**) a **anglès** i **castellà**

---

## 🏁 Estat Actual

✔️ Fase de Construcció completada  
📦 Preparat per a integració i proves finals

---

## 📍 Desenvolupadors

- Rafael Azzolini  
- Raphael Augusto Santos

---

## 📅 Localització

Lleida, Catalunya  
2025
