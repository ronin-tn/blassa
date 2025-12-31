# Android Application Plan: Blassa

This document outlines the architecture, technology stack, and design implementation plan for the Blassa Android application. The goal is to replicate the premium "Mediterranean Motion" design from the frontend using Jetpack Compose and Modern Android Development practices.

## 1. Technology Stack

*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose (Material Design 3)
*   **Architecture Pattern:** MVVM (Model-View-ViewModel) + Clean Architecture (Data, Domain, Presentation layers)
*   **Dependency Injection:** Hilt
*   **Asynchronous Processing:** Coroutines & Flow
*   **Networking:** Retrofit + OkHttp (with logging interceptor)
*   **JSON Parsing:** Moshi or Kotlin Serialization
*   **Navigation:** Jetpack Navigation Compose
*   **Image Loading:** Coil
*   **Local Storage (Optional for now):** DataStore (Preferences) / Room

## 2. Project Structure (Package Organization)

We will use a feature-centric package structure to ensure scalability.

```
com.blassa.app
├── core
│   ├── designsystem (Theme, Typography, Colors, Common Components)
│   ├── network (Retrofit modules, Interceptors)
│   ├── di (Common Hilt modules)
│   └── util (Extensions, Resource wrappers)
├── features
│   ├── auth
│   │   ├── data (Repository impl, API services, request/response models)
│   │   ├── domain (Repository interfaces, UseCases)
│   │   └── presentation (ViewModels, generic state classes)
│   │       ├── login (LoginScreen.kt)
│   │       └── register (RegisterScreen.kt)
│   ├── home
│   └── ...
├── MainActivity.kt
└── BlassaApplication.kt
```

## 3. Theme & Design System

We will replicate the "Mediterranean Motion" theme exactly as defined in the frontend's `globals.css` and `tailwind.config.js`.

### 3.1. Colors (`UI/theme/Color.kt`)

Based on the frontend design tokens:

**Brand Colors:**
*   `BlassaTeal` (Primary): `#006B8F`
*   `BlassaTealDark`: `#005673` (for pressed states)
*   `BlassaTealLight`: `#0A8F8F`
*   `BlassaAmber` (Accent/Action): `#FF9A3E`
*   `BlassaAmberDark`: `#E88A35`

**Neutral Colors:**
*   `Background`: `#F8FAFC` (Slate-50) - *App Background*
*   `Surface`: `#FFFFFF` (White) - *Cards/Modals*
*   `TextPrimary`: `#1E293B` (Slate-800)
*   `TextSecondary`: `#64748B` (Slate-500)
*   `Border`: `#E2E8F0` (Slate-200)
*   `InputBackground`: `#F8FAFC` (Slate-50)

**Semantic Colors:**
*   `Error`: `#EF4444` (Red-500)
*   `Success`: `#10B981` (Emerald-500)

### 3.2. Typography (`UI/theme/Type.kt`)

*   **Headings (Display/Title):** `Poppins` (Bold/SemiBold)
*   **Body:** `Inter` (Regular/Medium)

### 3.3. Shapes (`UI/theme/Shape.kt`)

*   **Cards/Modals:** `RoundedCornerShape(20.dp)`
*   **Buttons/Inputs:** `RoundedCornerShape(12.dp)` (Matches `rounded-xl` in Tailwind)

## 4. UI Implementation: Authentication Flow

The Authentication screens (Login/Register) feature a specific "Glass/Card" aesthetic with background gradients.

### 4.1. Common Layout Wrapper (`AuthScaffold`)
The layout consists of a background with two large blurred blobs (Teal & Amber) and a centered white card.

```kotlin
@Composable
fun AuthScaffold(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Slate-50
    ) {
        // Background Blobs
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Top-Left Teal Blob
            // Draw Bottom-Right Amber Blob
            // Apply blur effect
        }
        
        // Content Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.align(Alignment.Center).padding(16.dp)
        ) {
            content()
        }
    }
}
```

### 4.2. Components

**1. BlassaTextField:**
*   **Style:** Outlined but with a custom background fill.
*   **States:**
    *   *Default*: Border color `Slate-200`, Background `Slate-50`.
    *   *Focused*: Border color `BlassaTeal`, Ring effect (2px).
    *   *Error*: Border color `Red-500`.
*   **Icon:** Leading icon (Mail, Lock, User) in `Slate-400`.

**2. BlassaButton (Primary):**
*   **Color:** `BlassaTeal`.
*   **Shape:** `RoundedCornerShape(12.dp)`.
*   **Height:** `48.dp`.
*   **Content:** White text, generic loader support.

**3. SocialButton (Google):**
*   **Style:** Outlined.
*   **Border:** `Slate-200`.
*   **Icon:** Google 'G' logo.
*   **Text:** "Continuer avec Google".

### 4.3. Screens

#### Login Screen (`LoginScreen.kt`)
1.  **Header:** App Logo (Center), "Connexion" (Poppins Bold 22sp), Subtext "Connectez-vous à votre compte Blassa" (Slot-500).
2.  **Form:**
    *   Email Input.
    *   Password Input (with "Forgot Password" link in Amber aligned right).
    *   "Se connecter" Button.
3.  **Divider:** "OU" centered with lines.
4.  **Social:** Google Login Button.
5.  **Footer:** "Pas encore de compte?" -> Link to Register.

#### Register Screen (`RegisterScreen.kt`)
1.  **Header:** "Créer un compte", Subtext "Rejoignez la communauté Blassa".
2.  **Social:** Google Login Button (placed at top as per design).
3.  **Divider:** "OU INSCRIVEZ-VOUS PAR EMAIL".
4.  **Form:**
    *   First Name / Last Name (Row).
    *   Email.
    *   Phone Number (`+216` formatting).
    *   Gender (Dropdown/Selector) / Birth Date (Row with Date Picker).
    *   Password.
    *   Confirm Password.
5.  **Footer:** "Déjà un compte?" -> Link to Login.

## 5. Next Steps

1.  **Setup Design System:** Create the `Color.kt`, `Type.kt`, and `Theme.kt` files.
2.  **Add Assets:** Import the Logo and SVG icons (or use Material Icons Extended).
3.  **Implement Components:** Build `BlassaTextField` and `BlassaButton`.
4.  **Implement Login Screen:** Build the layout using the components.
5.  **Implement Register Screen:** Build the layout using the components.
6.  **Integrate Authentication:** Connect the screens to the `AuthRepository` (already partially set up).
