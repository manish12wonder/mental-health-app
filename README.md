# Mindora

Mindora is an Android app for **daily journaling**, **mood check-ins**, and **gentle AI-style feedback**. It is built as a self-care companion—not a medical or diagnostic tool.

## What it does

- **Home** — Greeting, today’s mood summary, quick mood buttons, and a shortcut to the journal.
- **Journal** — Write entries (optional tags, word limit), save and analyze text for mood and supportive copy.
- **Insights** — Weekly mood chart, simple stats, recent entries.
- **AI** — Shows the latest saved analysis (mood, confidence, mindful response from your last entry).

## Tech stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose, Material 3  
- **Architecture:** Clean-style layers (`domain`, `data`, `presentation`), Hilt for DI, `ViewModel` + `StateFlow`  
- **Backend:** Firebase **Authentication** (anonymous) and **Cloud Firestore**  
- **Sentiment:** Optional **Hugging Face** inference API; falls back to a local keyword heuristic if no key or if the request fails  

## Requirements

- **Android Studio** (recent stable) with Android SDK  
- **JDK 17** (recommended for Android Gradle / Kotlin toolchains)  
- A **Firebase** project for real auth and cloud sync  

## Setup

1. **Clone** the repo and open the project in Android Studio.

2. **Firebase**
   - Create a project in the [Firebase Console](https://console.firebase.google.com).
   - Add an Android app with package name `com.manish.mindora`.
   - Download **`google-services.json`** and place it in **`app/`** (replace the placeholder if present).
   - Enable **Anonymous** sign-in under Authentication.
   - Create a **Firestore** database and deploy rules that match your model (see `firestore.rules` in the repo root).

3. **Optional: Hugging Face API key** (remote mood analysis)  
   In `local.properties` add:
   ```properties
   SENTIMENT_API_KEY=your_token_here
   ```
   If omitted, the app still works using the **local** mood heuristic.

4. **Build**
   ```bash
   ./gradlew assembleDebug
   ```

## Project layout (high level)

- `app/src/main/java/com/manish/mindora/` — App code (`domain`, `data`, `presentation`, `di`, `ui/theme`, etc.)  
- `app/src/main/res/values/strings.xml` — Copy, disclaimers, mood labels  
- `firestore.rules` — Example security rules for `users/{userId}/entries/{entryId}`  

## Safety & privacy

The app shows a **disclaimer** on first launch: it is **not** a substitute for professional care. Journal content is stored under the signed-in user in Firestore; use Firebase rules and your privacy policy for production.

## License

Specify your license here if the project is public or shared.
