# TechCrunch Web Automation Project

**Automated tests for TechCrunch’s "Latest News" section and article details**, using Selenium, TestNG, and Allure.

---

## Overview

This project runs end-to-end tests on TechCrunch:

* Check the "Latest News" list for missing authors or images.
* Open an article and verify its title and links.

---

## Prerequisites

* **Java JDK 21+**
* **Maven 3.8+**
* **IDE** (IntelliJ IDEA, VSCode, etc.)

---


## Technologies Used

* Selenium Java
* TestNG
* WebDriverManager
* Allure Reports
* Maven
* Java 21

---

## Installation

1. Clone the repo:

```bash
git clone https://bitbucket.org/kubradas/TechCrunchCase_KubraDasdogan.git
cd TechCrunchCase_KubraDasdogan
```
Alternatively, you can download the project as a ZIP archive:
https://bitbucket.org/kubradas/TechCrunchCase_KubraDasdogan/src/master/

After extracting the ZIP file, we recommend renaming the folder to:

```nginx
TechCrunchCase_KubraDasdogan
```
to match the original project structure and make file navigation easier.


2. Install dependencies:

```bash
mvn clean install
```

---

## Configuration

Edit `src/test/resources/config.properties`:

```properties
# Site and browser settings
baseUrl=https://techcrunch.com/
browser=chrome      # chrome or firefox
headless=true        # true for headless, false for browser window
```

Or use Maven options:

```bash
mvn test -Dtest.browser=firefox -Dtest.headless=false
```

---

## Running Tests

Run all tests:

```bash
mvn test
```


---

## Test Scenarios

1. **Check Latest News**

    * Go to the home page.
    * Under “Latest News”, make sure each news item has an author and an image.

2. **Verify Article Details**

    * Pick a news item.
    * Open the article page.
    * Check that the page title matches the news title.
    * Check that each link in the article returns HTTP 200.

---

## 📊 Allure Reports

After tests finish, results are in `target/allure-results`.

To generate and view the report:

```bash
mvn allure:serve
```  
![img.png](img.png)

> **Note:** You need Allure Commandline 2.x installed and in your PATH for `mvn allure:serve` to work:
>
> * macOS: `brew install allure`
> * Windows: `choco install allure`
> * Or download from [https://github.com/allure-framework/allure2/releases](https://github.com/allure-framework/allure2/releases)

* Raw results: `target/allure-results`
* HTML report: `target/allure-report`

---
