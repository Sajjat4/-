# BongoLive AI - Real-time Bengali Voice & Screen Assistant (MYRA)

BongoLive AI is a native Android application built with **Kotlin** and **Jetpack Compose**. It provides a real-time Bengali conversational voice assistant and MYRA Universal Autonomous AI Agent.

## Core Features
- **Live Bengali Voice Assistant**: Real-time voice interaction with animated canvas Voice Orb visualizer (Idle, Listening, Thinking, Speaking).
- **MYRA Autonomous AI Engine**: Executes autonomous tasks with step-by-step progress tracking, verification, and narration timeline.
- **Conversational Chat**: Bengali natural language support, Gemini REST API integration, suggested queries, and chat history management.
- **Universal Communication**: Incoming call simulation (WhatsApp, Messenger, Telegram, Signal) with voice command answer ("ধরো") and reject ("কেটে দাও"), plus full-screen active call UI with live Bengali transcription.
- **YouTube & News Bulletins**: YouTube video player modal and Bengali news bulletin sheet with speech narration.
- **Bengali Dialects & Customizable Settings**: Support for Standard, Dhakai, Sylheti, Chittagonian, and Noakhali dialects, API keys, system prompts, and accessibility settings.

## Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Build System**: Gradle (Kotlin DSL)
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines and StateFlow
