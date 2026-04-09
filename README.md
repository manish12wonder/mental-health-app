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
- **Local data:** **Room** (`journal_entries` table) — all journal rows stay on device for this MVP  
- **Preferences:** DataStore (e.g. disclaimer acknowledgment)  
- **Sentiment:** Optional **Hugging Face** inference API; falls back to a local keyword heuristic if no key or if the request fails  

## Requirements

- **Android Studio** (recent stable) with Android SDK  
- **JDK 17** (recommended for Android Gradle / Kotlin toolchains)  

## Setup

1. **Clone** the repo and open the project in Android Studio.

2. **Optional: Hugging Face API key** (remote mood analysis)  
   In `local.properties` add:
   ```properties
   SENTIMENT_API_KEY=your_token_here
   ```
   If omitted, the app still works using the **local** mood heuristic.

3. **Build**
   ```bash
   ./gradlew assembleDebug
   ```

## Project layout (high level)

- `app/src/main/java/com/manish/mindora/` — App code (`domain`, `data`, `presentation`, `di`, `ui/theme`, etc.)  
- `data/local/db/` — Room entities, DAO, `MindoraDatabase`, mappers  
- `app/src/main/res/values/strings.xml` — Copy, disclaimers, mood labels  

## Safety & privacy

The app shows a **disclaimer** on first launch: it is **not** a substitute for professional care. Journal entries are stored **only on the device** in the app database. For production you would document retention, backups, and any future sync in your privacy policy.

## License

Specify your license here if the project is public or shared.
