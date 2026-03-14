# 🧪 TestGenie AI

> AI-powered test case generator for QA engineers.  
> Upload a Jira ticket, describe a feature, and get a full critical test suite in seconds.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![React](https://img.shields.io/badge/React-18-blue)
![Groq](https://img.shields.io/badge/AI-Groq%20%2B%20Llama3-purple)
![OCR](https://img.shields.io/badge/OCR-Tesseract-yellow)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 🔥 The Problem

Every QA engineer writes test cases manually.  
You read a Jira ticket, think through happy paths, edge cases, negative cases, security —
and type it all out. It takes hours. It's repetitive. And you always miss something.

## ✨ The Solution

TestGenie AI reads your Jira ticket screenshot or feature description and instantly
generates a full, critical test suite — covering every angle a senior QA engineer
would think of. And if it misses something? Just tell it, and it refines.

---

## 🎬 Features

### 📎 Jira Screenshot Upload
Upload a screenshot of any Jira ticket. TestGenie uses OCR to extract the ticket
title, description, acceptance criteria, and labels — then generates test cases
based on the actual ticket content.

### ⚡ Critical Test Case Generation
Every test case comes with:
- **P1 / P2 / P3 priority rating** — know what blocks release and what doesn't
- **Given / When / Then steps** — actionable, specific, ready to execute
- **6 categories** — Happy Path, Negative, Edge Cases, UI/UX, Performance, Security
- **Risk Areas** — the most dangerous failure points highlighted upfront
- **AI Summary** — one sentence overview of what is being tested

### 🔁 Refinement Loop
Not happy with the results? Tell the AI what to fix:
- Quick action buttons: `+ More P1 cases`, `+ Add mobile scenarios`, `+ More security`
- Free-form feedback: "You missed offline scenarios when network is unavailable"
- Choose **Replace** (rewrite everything) or **Append** (add on top)

### 📤 Export Options
- **Markdown** — paste into any doc or wiki
- **Jira** — paste directly into Jira tickets
- **CSV** — import into any test management tool

---

## 🏗️ Tech Stack

| Layer      | Tech                              |
|------------|-----------------------------------|
| Frontend   | React 18 + Vite                   |
| Backend    | Java 17 + Spring Boot 3.2         |
| AI         | Groq API + Llama 3.3 (free tier)  |
| OCR        | Tesseract + Tess4j                |

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- Maven 3.8+
- Tesseract OCR → `brew install tesseract` (Mac) or `apt install tesseract-ocr` (Linux)
- Free Groq API key → [console.groq.com](https://console.groq.com)

### 1. Clone the repo
```bash
git clone git@github.com:kevilkhadka/testgenie-ai.git
cd testgenie-ai
```

### 2. Set up the backend
```bash
cd backend
cp src/main/resources/application.example.properties src/main/resources/application.properties
# Open application.properties and add your Groq API key
mvn spring-boot:run
```

### 3. Set up the frontend
```bash
cd frontend
npm install
npm run dev
```

### 4. Open the app
Visit `http://localhost:5173`

---

## 📁 Project Structure
```
testgenie-ai/
├── backend/                              # Java Spring Boot API
│   └── src/main/java/com/testgenie/
│       ├── controller/                   # REST endpoints
│       │   └── TestCaseController.java   # /api/generate, /api/generate/upload, /api/refine
│       ├── service/                      # Business logic
│       │   ├── ClaudeApiService.java     # Groq AI integration
│       │   ├── TestCaseService.java      # Test case generation + refinement
│       │   └── OcrService.java           # Tesseract OCR — reads Jira screenshots
│       ├── model/                        # Request/Response objects
│       │   ├── TestCaseRequest.java
│       │   ├── TestCaseResponse.java
│       │   └── RefineRequest.java
│       └── config/
│           └── CorsConfig.java           # CORS configuration
├── frontend/                             # React app
│   └── src/
│       ├── components/
│       │   ├── CategoryCard.jsx          # Test case cards with P1/P2/P3 + Given/When/Then
│       │   └── RefinePanel.jsx           # Refinement loop UI
│       ├── hooks/
│       │   ├── useTestGenerator.js       # API calls for generation
│       │   └── useRefine.js              # API calls for refinement
│       ├── App.jsx                       # Main UI
│       └── styles.css                    # Design system
└── README.md
```

---

## 🌐 API Endpoints

| Method | Endpoint                | Description                          |
|--------|-------------------------|--------------------------------------|
| POST   | `/api/generate`         | Generate from plain text description |
| POST   | `/api/generate/upload`  | Generate from Jira screenshot + text |
| POST   | `/api/refine`           | Refine existing test cases           |
| GET    | `/api/health`           | Health check                         |

---

## 🗺️ Roadmap

- [x] Java Spring Boot backend
- [x] Groq AI integration (free)
- [x] React frontend with dark terminal UI
- [x] Jira screenshot upload with OCR
- [x] P1/P2/P3 priority ratings
- [x] Given/When/Then test steps
- [x] Risk areas highlighting
- [x] AI refinement loop with append/replace
- [x] Export as Markdown, Jira, CSV
- [ ] Download CSV file directly
- [ ] Save test suites to local storage
- [ ] Jira API integration (auto-create tickets)
- [ ] Chrome extension
- [ ] Support for more languages (Spanish, French)

---

## 🙋 About

Built by a QA engineer who was tired of writing test cases manually.  
This started as a side project to rebuild coding skills and ended up as a tool
used in real QA workflows.

If this helped you, consider giving it a ⭐ on GitHub — it helps others find it!

---

## 🤝 Contributing

PRs welcome! If you're a QA engineer and you wish a feature existed — open an issue.
Check [CONTRIBUTING.md](CONTRIBUTING.md) to get started.

## 📄 License

MIT — free to use, fork, and modify.
