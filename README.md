# 🧪 TestGenie AI

> AI-powered test case generator for QA engineers.  
> Describe a feature — get a full test suite in seconds.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![React](https://img.shields.io/badge/React-18-blue)
![Groq](https://img.shields.io/badge/AI-Groq%20%2B%20Llama3-purple)
![License](https://img.shields.io/badge/license-MIT-green)

---

## 🔥 The Problem

Every QA engineer writes test cases manually.  
You read a ticket, think through happy paths, edge cases, negative cases, security —
and type it all out. It takes hours. It's repetitive. And you always miss something.

## ✨ The Solution

TestGenie AI reads your feature description and instantly generates a full,
structured test suite — covering every angle a senior QA engineer would think of.

---

## 🎬 Demo

> Paste any feature description and get results like this in seconds:

**Input:**
```
Checkout flow with credit card payment
```

**Output:**
- ✅ 3+ Happy Path test cases
- ❌ 3+ Negative / Error cases
- ⚠️ 3+ Edge cases
- 🎨 3+ UI / UX cases
- ⚡ 3+ Performance cases
- 🔒 3+ Security cases

Export as **Markdown**, **Jira**, or **CSV** — ready to paste into your test plan.

---

## 🏗️ Tech Stack

| Layer    | Tech                          |
|----------|-------------------------------|
| Frontend | React 18 + Vite               |
| Backend  | Java 17 + Spring Boot 3.2     |
| AI       | Groq API + Llama 3.3 (free)   |

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- Maven 3.8+
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
├── backend/                            # Java Spring Boot API
│   └── src/main/java/com/testgenie/
│       ├── controller/                 # REST endpoints
│       ├── service/                    # Business logic + Groq API calls
│       ├── model/                      # Request/Response objects
│       └── config/                     # CORS configuration
├── frontend/                           # React app
│   └── src/
│       ├── components/                 # UI components
│       ├── hooks/                      # Custom React hooks
│       └── App.jsx                     # Main UI
└── README.md
```

---

## 🗺️ Roadmap

- [x] Java Spring Boot backend
- [x] Groq AI integration (free)
- [x] React frontend with dark terminal UI
- [x] Export as Markdown, Jira, CSV
- [x] Checkable test cases
- [ ] Download CSV file directly
- [ ] Save test suites locally
- [ ] Chrome extension
- [ ] Jira integration

---

## 🙋 About

Built by a QA engineer who was tired of writing test cases manually.  
This is an open source project — feel free to fork it, use it, and improve it.

If this helped you, consider giving it a ⭐ on GitHub!

---

## 🤝 Contributing

PRs welcome! If you're a QA engineer and you wish a feature existed — open an issue.

## 📄 License

MIT — free to use, fork, and modify.
