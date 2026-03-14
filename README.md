# 🧪 TestGenie AI

> AI-powered test case generator for QA engineers.
> Paste a user story or feature description — get comprehensive test cases in seconds.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![React](https://img.shields.io/badge/React-18-blue)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 🔥 The Problem

Every QA engineer writes test cases manually. You read a ticket, think through
happy paths, edge cases, negative cases, security — and type it all out.
It takes hours. It's repetitive. And you always miss something.

## ✨ The Solution

TestGenie AI reads your feature description and instantly generates a full,
structured test suite — covering every angle a senior QA engineer would think of.

---

## 🎬 What It Does

- **Paste** any feature description, user story, or ticket text
- **AI generates** test cases across 6 categories:
  - ✅ Happy Path
  - ❌ Negative / Error Cases
  - ⚠️ Edge Cases
  - 🎨 UI / UX
  - ⚡ Performance
  - 🔒 Security
- **Export** results as Markdown, Jira format, or CSV
- **Check off** test cases as you execute them

---

## 🏗️ Tech Stack

| Layer     | Tech                        |
|-----------|-----------------------------|
| Frontend  | React 18 + Vite             |
| Backend   | Java 17 + Spring Boot 3.2   |
| AI        | Claude API (Anthropic)      |

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- Maven 3.8+
- A free Claude API key → [console.anthropic.com](https://console.anthropic.com)

### 1. Clone the repo
```bash
git clone git@github.com:kevilkhadka/testgenie-ai.git
cd testgenie-ai
```

### 2. Set up the backend
```bash
cd backend
cp src/main/resources/application.example.properties src/main/resources/application.properties
# Open application.properties and add your Claude API key
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
├── backend/                          # Java Spring Boot API
│   └── src/main/java/com/testgenie/
│       ├── controller/               # REST endpoints
│       ├── service/                  # Business logic + Claude API calls
│       ├── model/                    # Request/Response objects
│       └── config/                   # CORS configuration
├── frontend/                         # React app
│   └── src/
│       ├── components/               # UI components
│       ├── hooks/                    # Custom React hooks
│       └── utils/                    # Export helpers
└── README.md
```

---

## 🗺️ Roadmap

- [x] Project setup
- [x] Spring Boot backend
- [x] Claude API integration
- [x] React frontend
- [ ] Export to CSV
- [ ] Jira ticket format export
- [ ] Save test suites locally
- [ ] Chrome extension

---

## 🤝 Contributing

PRs are welcome! If you're a QA engineer and you wish a tool existed — open an issue.

## 📄 License

MIT — free to use, fork, and modify.
