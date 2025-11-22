# El País Opinion Scraper – Cross‑Browser Automation

![CI](https://github.com/chiragguruswamy673/elpais-crossbrowser-automation/actions/workflows/ci.yml/badge.svg)

## 📖 Project Overview
This project demonstrates end‑to‑end automation using **Selenium** and **TestNG**, integrating web scraping, translation, and text analysis.  
It navigates to the *El País* Opinion section, scrapes the first five articles in Spanish, downloads cover images when available, translates article titles into English via an API, and analyzes repeated words across headers.

The pipeline is validated through **GitHub Actions CI** and executed on **BrowserStack** across multiple desktop and mobile browsers in parallel, with outputs bundled as downloadable artifacts (`elpais_outputs`).

---

## ⚙️ Features
- Scrapes the first five articles from *El País* Opinion section in Spanish
- Extracts titles and content, saves cover images locally
- Translates article headers into English using a translation API
- Analyzes translated headers for repeated words (frequency count)
- Runs Selenium + TestNG tests locally and on BrowserStack
- Executes 5 parallel threads across desktop and mobile browsers
- CI/CD pipeline with GitHub Actions, producing downloadable artifacts

---

## 🚀 Setup Instructions

### Prerequisites
- Java 17+
- Maven 3+
- BrowserStack account (free trial works)
- API key for translation service (Google Translate API or Rapid Translate API)

### Clone the repository
```bash
git clone https://github.com/chiragguruswamy673/elpais-crossbrowser-automation.git
cd elpais-crossbrowser-automation

Install dependencies
mvn install


Run tests locally
mvn test


Run tests on BrowserStack
Update your testng.xml with desired browser/device capabilities.
Set your BrowserStack credentials as environment variables:
export BROWSERSTACK_USERNAME=<your-username>
export BROWSERSTACK_ACCESS_KEY=<your-access-key>


Then run:
mvn test -DsuiteXmlFile=testng.xml



📦 Outputs
- Scraped articles and cover images → saved in elpais_articles/article_1 … article_5/
- Metadata index → elpais_articles/index.csv (ID, title, date, URL, image count, folder path)
- Translated headers → elpais_articles/translated_titles.txt
- Repeated word frequency analysis → elpais_articles/repeated_words.txt
- GitHub Actions uploads the entire elpais_articles/ folder as an artifact named elpais_outputs
- Reviewers can download the artifact directly from the Actions run page and inspect all outputs without checking logs


🌐 CI/CD
- Automated build and test pipeline using GitHub Actions
- Workflow file: .github/workflows/ci.yml
- Badge above shows live build status
- Artifacts (elpais_outputs) are available for download after each successful run

📸 Deliverables
- Automated Build Link: GitHub Actions Run
- Screenshot of Build Running: Google Drive link here

🧪 Cross‑Browser Coverage
Tests validated across:
- Chrome (latest)
- Firefox (latest)
- Edge (latest)
- Safari (mobile emulator)
- Android Chrome (mobile emulator)

 Author
Developed by Chirag Guruswamy


