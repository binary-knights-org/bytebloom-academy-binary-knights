## 1. Workflow, Team Roles & Git Governance (By: ِAhmed Farani)

### 👥 1.1 Team Roles Outline
To ensure clear accountability throughout the academy season, the Binary Knights team roles are explicitly defined as follows:
* **Member 1 (Workflow & Git Rules):** Ahmed Farani - Responsible for repository governance, branching model integrity, and commit monitoring.
* **Member 2 (Clean Code Standards):** Roba Nassar - Responsible for defining Kotlin naming conventions, readability, and formatting rules.
* **Member 3 (Communication & SLAs):** Sabah Baraka - Responsible for scheduling syncs, setting internal review deadlines, and managing response times.
* **Member 4 (Architecture & Ignore Policy):** Kamal Ashour - Responsible for the modular package architecture and maintaining the project's root `.gitignore`.

### 🌿 1.2 Selected Branching Model (Git Flow Lite)
Our team utilizes a **Feature-Branch Workflow** model to isolate development environment tasks:
* `main`: The protected production branch. Writing or pushing code directly to `main` is strictly prohibited.
* `feature/task[X]-[short-description]`: Temporary development branches created for each specific academy assignment (e.g., `feature/task2-logiroute`).
* **Integration Rule:** Code from a feature branch can only be merged into `main` via a formal Pull Request (PR) after receiving a minimum of **2 peer approvals**.

### 📝 1.3 Strict Prefix Rules for Commit Messages
To maintain a transparent and trackable project audit log, all repository updates must adhere to the following strict prefix convention. Commits missing these prefixes will be rejected during peer review:
* `feat:` Used strictly when introducing new code, features, or logic implementations.
* `fix:` Used strictly for resolving bugs, runtime exceptions, or logic compilation errors.
* `chore:` Used for non-code modifications including repo setup, editing documentation, or updating the root `.gitignore` file.


## 2. Clean Code Standards (By: Roba Nassar)

### 🏷️ 2.1 Naming Conventions

* `Variables:` Use camelCase.
* `Functions:` Start with a verb and follow camelCase.
* `Constants:` Use UPPER_SNAKE_CASE.

### 🧩 2.2 Code Structure Guidelines
1. Each function should have a single responsibility.
2. Avoid writing overly long functions. The recommended length is 20–30 lines.
3. Comments should explain why a particular approach was used, not what the code does.
4. Do not submit code that contains unused variables or functions.
5. If a part of the code is expected to be error-prone, prefer using try-catch for exception handling.
6. Before every commit or push, press Ctrl + Alt + L to automatically format the code.
7. Prefer using val by default. Use var only when mutability is absolutely necessary.



## 3. Communication & SLAs  (by: Sabah Baraka)  

### * 3.1 Communication *

- WhatsApp is used for daily communication.
- GitHub Issues are used for reporting bugs and tasks.
- GitHub Pull Requests are used for code reviews.

### * 3.2 Standing Meeting *

- daily stand‑up at 6:00 PM 
- Meeting duration: 1 hour

### * 3.3 SLA Rules *

- Respond to team messages within 24 hours.
- Review Pull Requests within 24 hours.
- Notify the team if a task will be delayed.

### * 3.4 Peer Review *

- Every Pull Request must be reviewed before merging.
- At least two approvals are required.
- All review comments must be addressed before merging.
- PR descriptions must include:(1. What was changed  2. Why it was changed 3. Any testing performed)




## 4. Architecture & Repository Guidelines (By: Kamal Ashour)

### Target Directory Models
To maintain high codebase scalability, clean separation of concerns, and ease of testing, our Kotlin project repository enforces a structured, modular directory model. The system codebase is strictly separated into core utilities and encapsulated business operations:

* **Core Packages (`/src/main/kotlin/packages/`):**
  This directory is dedicated to global system architecture configurations, cross-cutting shared utilities, networking/database layers, and common data structures that support the entire application infrastructure.

* **UseCases (`/src/main/kotlin/usecases/`):**
  This directory isolates the pure business logic of the application. Following the Single Responsibility Principle (SRP), every individual business action or specific feature operation must be implemented as an independent usecase class (e.g., `CalculateRouteUseCase.kt`), protecting core domain logic from being entangled with UI components or data frameworks.

---

### Standardized .gitignore Exclusions
A robust and comprehensive `.gitignore` configuration has been established at the repository root level. This baseline configuration ensures that temporary compiler files, local environments, and IDE artifacts from different team editors do not cause repository pollution or development conflicts during collaborative merges.

The system repository strictly excludes and untracks the following blocks:

#### 1. Build Tools & Runtime Outputs
* `.gradle/` and `build/` (Temporary build structures and localized compiler outputs)
* `!gradle/wrapper/gradle-wrapper.jar` (Explicitly tracked wrapper jar to preserve build consistency)
* `out/` (Standard Java/Kotlin binary compilation directories)

#### 2. JetBrains IntelliJ IDEA
* `.idea/modules.xml`, `.idea/jarRepositories.xml`, `.idea/compiler.xml` (Local editor preferences)
* `.idea/libraries/` (Project-specific library mapping indices)
* `*.iml`, `*.iws`, `*.ipr` (Workspace configurations unique to each developer machine)

#### 3. Cross-Platform IDE Support (Eclipse, NetBeans, VS Code)
* **Eclipse:** `.apt_generated`, `.classpath`, `.factorypath`, `.project`, `.settings/`, `.springBeans`, `.sts4-cache`, `bin/`
* **NetBeans:** `/nbproject/private/`, `/nbbuild/`, `/dist/`, `/nbdist/`, `/.nb-gradle/`
* **VS Code:** `.vscode/`

#### 4. Ecosystem & Operating System Metadata
* `.kotlin` (Kotlin compiler environment flags)
* `.DS_Store`, `Thumbs.db` (Mac OS and Windows desktop services metadata)