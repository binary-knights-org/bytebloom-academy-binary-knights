# Binary Knights — Team Charter

Governance rules, coding standards, communication protocols, and architecture guidelines that apply to every task built in this repository.

---

## 1. Workflow, Team Roles & Git Governance (By: ِAhmed Farani)

### 1.1 Team Roles Outline
| # | Member | Responsibility |
|---|--------|-----------------|
| 1 | Ahmed Farani | Repository governance, branching model integrity, and commit monitoring |
| 2 | Roba Nassar | Kotlin naming conventions, readability, and formatting rules |
| 3 | Sabah Baraka | Scheduling syncs, internal review deadlines, and response times |
| 4 | Kamal Ashour | Modular package architecture and root `.gitignore` maintenance |

### 1.2 Selected Branching Model (Git Flow Lite)
Our team utilizes a **Feature-Branch Workflow** model to isolate development environment tasks:
-`main`: The protected production branch. Writing or pushing code directly to `main` is strictly prohibited.
-`feature/task[X]-[short-description]`: Temporary development branches created for each specific academy assignment (e.g., `feature/task2-logiroute`).
**Integration rule:** code from a feature branch is merged into `main` only through a Pull Request, after receiving a minimum of **2 peer approvals**.

### 1.3 Commit Message Prefixes

Every commit must use one of the prefixes below. Commits missing a valid prefix are rejected during peer review.

- `feat:` — new code, features, or logic implementations
- `fix:` — bug fixes, runtime exceptions, or compilation errors
- `chore:` — non-code changes: repo setup, documentation edits, `.gitignore` updates

---

## 2. Clean Code Standards (By: Roba Nassar)

### 2.1 Naming Conventions

- **Parameters:** camelCase
- **Variables:** camelCase
- **Functions:** camelCase, starting with a verb
- **Constants:** UPPER_SNAKE_CASE

### 2.2 Code Structure Guidelines

1. Each function has a single responsibility.
2. Keep functions short — recommended length is 20–30 lines.
3. Comments explain *why* an approach was used, not *what* the code does.
4. No unused variables or functions in submitted code.
5. Use `try-catch` around code that is expected to be error-prone.
6. Format code with `Ctrl + Alt + L` before every commit or push.
7. Prefer `val` by default; use `var` only when mutability is required.

---

## 3. Communication & SLAs  (by: Sabah Baraka)  

### 3.1 Communication Channels

- WhatsApp is used for daily communication.
- GitHub Issues are used for reporting bugs and tasks.
- GitHub Pull Requests are used for code reviews.

### 3.2 Standing Meeting

- daily stand‑up at 12:00 PM 
- Meeting duration: 1 hour

### 3.3 SLA Rules 

- Respond to team messages within 24 hours.
- Review Pull Requests within 24 hours.
- Notify the team if a task will be delayed.

### 3.4 Peer Review

- Every Pull Request must be reviewed before merging.
- At least two approvals are required.
- All review comments must be addressed before merging.
- PR descriptions must include:(1. What was changed  2. Why it was changed 3. Any testing performed)

---

## 4. Architecture & Repository Guidelines (By: Kamal Ashour)

### 4.1 Target Directory Model

```
src/main/
├── kotlin/
│   ├── dataholders/
│   ├── parsers/
│   ├── sorter/
│   └── Main.kt 
└── resources/
```

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