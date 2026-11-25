# SL3 – Sesiunea 21: Log4j2, Allure, CI/CD și Paralelizare (hapifyMe API v1)

Proiect Maven (Java 21 + TestNG + REST Assured) cu **toate exemplele și soluțiile exercițiilor** din
*Curs 21 – Material auxiliar*. Testele rulează pe `https://apps.qualiadept.eu/hapifyme/api/v1`.

## 1. Cerințe
- JDK 21 și Maven 3.9+ (sau IntelliJ IDEA, care le are incluse)
- Un cont de test hapifyMe (email + parolă)

## 2. Credențialele (obligatoriu)
Testele citesc credențialele din variabilele de mediu `HAPIFY_USER` și `HAPIFY_PASS` – **nu le scriem niciodată în cod**.

**IntelliJ:** Run → Edit Configurations… → Environment variables:
`HAPIFY_USER=userul_tău;HAPIFY_PASS=parola_ta`

**PowerShell:**
```powershell
$env:HAPIFY_USER="userul_tău"; $env:HAPIFY_PASS="parola_ta"; mvn clean test
```

**Bash (Linux / macOS / Git Bash):**
```bash
export HAPIFY_USER="userul_tău" HAPIFY_PASS="parola_ta"; mvn clean test
```

Opțional: `HAPIFY_BASE_URL` schimbă URL-ul de bază (implicit: `https://apps.qualiadept.eu/hapifyme/api/v1`).

## 3. Comenzi utile
| Comandă | Ce face |
|---|---|
| `mvn clean test` | Rulează suita completă `testng.xml`, în paralel (8 thread-uri) |
| `mvn test -Dtest=LoginTest` | Rulează o singură clasă (fără suita `testng.xml`) |
| `mvn test -Dtest=SystemHealthTest` | Test care **nu** are nevoie de credențiale |
| `mvn allure:serve` | Generează și deschide raportul Allure |
| `mvn test -Dhealth.expected=degraded` | Exercițiul 5.7.3: forțează `SystemHealthTest` să pice ca să vezi retry-ul |

Log-urile ajung în consolă și în `logs/test_execution.log`; rezultatele Allure în `target/allure-results`.

## 4. Unde găsești fiecare exercițiu
| Capitol din material | Fișier(e) |
|---|---|
| 1.3 Dependențe Maven | `pom.xml` |
| 1.3 Configurare log-uri | `src/test/resources/log4j2.xml` |
| 1.4 BaseTest + LoginTest | `BaseTest.java`, `LoginTest.java` |
| 1.7 Ex. 1 – Comutatorul de detalii | `log4j2.xml`: `level="info"` → `level="debug"` (apare mesajul „Payload trimis…”) |
| 1.7 Ex. 2 – Arhivarea log-urilor | `log4j2.xml`: `append="false"` → `append="true"`, rulează de 3 ori |
| 2.1 Allure + AspectJ, `allure.properties` | `pom.xml`, `src/test/resources/allure.properties` |
| 2.3 Login cu Allure | `HapifyMeLoginTest.java` |
| 2.8 / 2.9 Exercițiu – timp de răspuns | `HapifyMeLoginTest.verifyResponseTime()` |
| 3.5 Pipeline GitHub Actions | `.github/workflows/automated_tests.yml` |
| 3.8 Ex. 1 – Cron de noapte | `automated_tests.yml` → `schedule: cron '0 2 * * *'` |
| 3.8 Ex. 2 – Variabile vs. Secrete | `automated_tests.yml` → `vars.HAPIFY_BASE_URL`; `BaseTest.resolveBaseUrl()` |
| 4.1.3 Jenkins | `Jenkinsfile` |
| 4.2.2 GitLab CI | `.gitlab-ci.yml` |
| 5.1 Paralelizare | `testng.xml`, `SystemHealthTest.java` |
| 5.2 ThreadLocal | `ro/hapifyme/utils/TestContext.java` |
| 5.3 Retry – metoda 1 (per test) | `RetryAnalyzer.java`, `RetryDemoTest.java` |
| 5.3 Retry – metoda 2 (@Retryable) | `Retryable.java`, `AnnotationTransformer.java`, `testng.xml` (listeners) |
| 5.7 Ex. 1 / 5.8 Soluția 1 – BaseTest paralel | `BaseTest.java` (thread ID + `@AfterMethod(alwaysRun = true)` cu `TestContext.clear()`) |
| 5.7 Ex. 2 / 5.8 Soluția 2 – login pe 4 thread-uri | `ThreadSafeTest.java` |
| 5.7 Ex. 2 – Bonus (static String) | `StaticTokenBonusTest.java` (dezactivat; vezi comentariul din clasă) |
| 5.7 Ex. 3 / 5.8 Soluția 3 – Retry doar unde are sens | `SystemHealthTest.java` (`@Retryable`) + `-Dhealth.expected=degraded` |

> **Diferență față de material:** `BaseTest` este varianta finală, unificată (Cap. 1 + Cap. 3 Ex. 2 + Cap. 5 Soluția 1),
> iar toate clasele folosesc `BASE_URL` din `BaseTest` în loc de URL-ul scris direct în fiecare test.

## 5. CI/CD – ce trebuie configurat
### GitHub Actions (Capitolul 3)
1. **Settings → Secrets and variables → Actions → Secrets:** `HAPIFY_USER`, `HAPIFY_PASS`.
2. *(Opțional, Ex. 2)* tab-ul **Variables:** `HAPIFY_BASE_URL` = `https://apps.qualiadept.eu/hapifyme/api/v1`.
3. După prima rulare: **Settings → Pages → Deploy from a branch → `gh-pages` / (root)**.
4. Eroare 403 la Deploy: **Settings → Actions → General → Workflow permissions → Read and write**.

Fără secrete, testele de login pică și build-ul devine **roșu** – comportament corect.

### Jenkins (Capitolul 4, Partea 1)
Imagine `jenkins/jenkins:lts-jdk21`, plugin Allure, Tools: `Maven 3.8.x` și `Allure 2.x`,
credential **Username with password** cu ID-ul `hapify-creds`.

### GitLab CI (Capitolul 4, Partea 2)
Runner cu tag-ul `qagitlab`; **Settings → CI/CD → Variables:** `HAPIFY_USER`, `HAPIFY_PASS` (Masked).
Raportul se descarcă din artefactele job-ului `allure_report` (un singur fișier HTML).

## 6. Structura
```
sl3-s21-practice/
├── .github/workflows/automated_tests.yml
├── .gitlab-ci.yml
├── Jenkinsfile
├── pom.xml
├── testng.xml
└── src/test/
    ├── java/ro/hapifyme/
    │   ├── tests/   BaseTest, LoginTest, HapifyMeLoginTest, SystemHealthTest,
    │   │            ThreadSafeTest, StaticTokenBonusTest, RetryDemoTest
    │   └── utils/   TestContext, RetryAnalyzer, Retryable, AnnotationTransformer
    └── resources/   log4j2.xml, allure.properties
```
