# SpeakingCashflow
Trying to make an app to automate your income/expense note-taking through voices

## Setup

### Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file and place it in the app directory
4. Enable Firestore Database and Authentication in your Firebase project
5. Enable Vertex AI API in your Google Cloud project (linked to your Firebase project)
   - Go to the Google Cloud Console > APIs & Services > Enable APIs and Services
   - Search for "Vertex AI API" and enable it
   - Ensure your Firebase project has the necessary permissions

## Features
- Voice input to record transactions
- Automatic categorization using Firebase Vertex AI
- Secure storage in Firebase
- Transaction history and analytics
