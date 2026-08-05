<div align="center">

<table>
  <tr>
    <td width="12%" align="center"><img src="./binary-knights-logo.png" /></td>
    <td width="76%" align="center"><img src="https://capsule-render.vercel.app/api?type=waving&color=0:8A5CF5,50:38BDF8,
100:6EE7B7&height=260&section=header&text=BINARY%20KNIGHTS&fontSize=46&fontColor=FFFFFF&animation=fadeIn&fontAlignY=35&
desc=One%20Charter%20%C2%B7%20Four%20Knights%20%C2%B7%20Zero%20Chaos&descAlignY=58&descAlign=50&descSize=16" width="100%" /></td>
    <td width="12%" align="center"><img src="./binary-knights-logo.png" /></td>
  </tr>
</table>

<br>

<img src="https://img.shields.io/badge/status-active-8A5CF5?style=for-the-badge&logo=github&logoColor=white" /> &nbsp;
<img src="https://img.shields.io/badge/branching-Git%20Flow%20Lite-5E8BF6?style=for-the-badge&logo=git&logoColor=white" /> &nbsp;
<img src="https://img.shields.io/badge/PR%20approvals-required-38BDF8?style=for-the-badge&logo=github&logoColor=white" /> &nbsp;
<img src="https://img.shields.io/badge/members-4-6EE7B7?style=for-the-badge&logo=android&logoColor=white" />

<br><br>

*Governance rules, coding standards, communication protocols, and architecture guidelines that apply to every task built
in this repository.*

</div>

---

## Table of Contents

- [1. Workflow, Team Roles & Git Governance](#1-workflow-team-roles--git-governance-by-ahmed-farani)
  - [1.1 Team Roles Outline](#11-team-roles-outline)
  - [1.2 Selected Branching Model (Git Flow Lite)](#12-selected-branching-model-git-flow-lite)
  - [1.3 Commit Message Prefixes](#13-commit-message-prefixes)
- [2. Clean Code Standards](#2-clean-code-standards-by-roba-nassar)
  - [2.1 Naming Conventions](#21-naming-conventions)
  - [2.2 Code Structure Guidelines](#22-code-structure-guidelines)
- [3. Communication & SLAs](#3-communication--slas-by-sabah-baraka)
  - [3.1 Communication Channels](#31-communication-channels)
  - [3.2 Standing Meeting](#32-standing-meeting)
  - [3.3 SLA Rules](#33-sla-rules)
  - [3.4 Peer Review](#34-peer-review)
- [4. Architecture & Repository Guidelines](#4-architecture--repository-guidelines-by-kamal-ashour)
  - [4.1 Target Directory Model](#41-target-directory-model)
  - [4.2 Standardized .gitignore Exclusions](#42-standardized-gitignore-exclusions)

---

## 1. Workflow, Team Roles & Git Governance (By: Ahmed Farani)

![owner](https://img.shields.io/badge/owner-Ahmed%20Farani-93A2F7?style=flat-square&logo=git&logoColor=white)

### 1.1 Team Roles Outline

| # | Member       | Responsibility                                                           |
|---|--------------|--------------------------------------------------------------------------|
| 1 | Ahmed Farani | Repository governance, branching model integrity, and commit monitoring. |
| 2 | Roba Nassar  | Kotlin naming conventions, readability, and formatting rules.            |
| 3 | Sabah Baraka | Scheduling syncs, internal review deadlines, and response times.         |
| 4 | Kamal Ashour | Modular package architecture and root `.gitignore` maintenance.          |

### 1.2 Selected Branching Model (Git Flow Lite)

Our team uses a **Git Flow Lite** model with three branch tiers to isolate development environment tasks:

- **`main`** — The protected production branch. Writing or pushing code directly to `main` is strictly prohibited. Only
  receives merges from `develop` once work has stabilized there.
- **`develop`** — The protected integration branch. All feature branches merge here first. This is where the team
  validates that everyone's work builds and runs together before it reaches `main`.
- **`feature/task[X]-[short-description]`** — Temporary development branches created for each specific academy
  assignment (e.g., `feature/task2-logiroute`). Branched from `develop`, merged back into `develop`.

**Integration rules:**

- `feature/*` → `develop`: merged only through a Pull Request (PR), after receiving a minimum of **1 peer approval**.
- `develop` → `main`: merged once the team agrees `develop` is stable (e.g., all sub-tasks for the current milestone are
  complete and CI passes). Requires **1 peer approval** from repository governance (Ahmed Farani), since the underlying
  commits were already reviewed when they entered `develop`.

### 1.3 Commit Message Prefixes

Every commit must use one of the prefixes below. Commits missing a valid prefix are rejected during peer review.

| Prefix   | Used for                                                                 |
|----------|--------------------------------------------------------------------------|
| `feat:`  | New code, features, or logic implementations.                            |
| `fix:`   | Bug fixes, runtime exceptions, or compilation errors.                    |
| `chore:` | Non-code changes: repo setup, documentation edits, `.gitignore` updates. |

---

## 2. Clean Code Standards (By: Roba Nassar)

![owner](https://img.shields.io/badge/owner-Roba%20Nassar-6EE7B7?style=flat-square&logo=kotlin&logoColor=white)

### 2.1 Naming Conventions

| Element    | Convention                      |
|------------|---------------------------------|
| Parameters | camelCase                       |
| Variables  | camelCase                       |
| Functions  | camelCase, starting with a verb |
| Constants  | UPPER_SNAKE_CASE                |

### 2.2 Code Structure Guidelines

1. Each function has a single responsibility.
2. Keep functions short — recommended length is 20–30 lines.
3. Comments explain *why* an approach was used, not *what* the code does.
4. No unused variables or functions in submitted code.
5. Use `try-catch` around code that is expected to be error-prone.
6. Format code with `Ctrl + Alt + L` before every commit or push.
7. Prefer `val` by default; use `var` only when mutability is required.

---

## 3. Communication & SLAs (By: Sabah Baraka)

![owner](https://img.shields.io/badge/owner-Sabah%20Baraka-B39DDB?style=flat-square&logo=whatsapp&logoColor=white)

### 3.1 Communication Channels

- **WhatsApp** — used for daily communication.
- **GitHub Issues** — used for reporting bugs and tasks.
- **GitHub Pull Requests** — used for code reviews.

### 3.2 Standing Meeting

- Daily stand-up at 12:00 PM.
- Meeting duration: 1 hour.

### 3.3 SLA Rules

- Respond to team messages within 24 hours.
- Review Pull Requests within 24 hours.
- Notify the team if a task will be delayed.

### 3.4 Peer Review

- Every Pull Request must be reviewed before merging.
- At least **1 approval** is required.
- All review comments must be addressed before merging.
- PR descriptions must include:
  1. What was changed.
  2. Why it was changed.
  3. Any testing performed.

---

## 4. Architecture & Repository Guidelines (By: Kamal Ashour)

![owner](https://img.shields.io/badge/owner-Kamal%20Ashour-8A5CF5?style=flat-square&logo=android&logoColor=white)

### 4.1 Target Directory Model

```
src/main/
├── kotlin/
│   ├── data/
│   ├── domain/
│   └── ui/
└── resources/
```

### 4.2 Standardized `.gitignore` Exclusions

A robust and comprehensive `.gitignore` configuration is established at the repository root level. This baseline
configuration ensures that temporary compiler files, local environments, and IDE artifacts from different team editors
do not cause repository pollution or development conflicts during collaborative merges.

The repository strictly excludes and untracks the following:

**Build tools & runtime outputs**

- `.gradle/` and `build/` — temporary build structures and localized compiler outputs.
- `!gradle/wrapper/gradle-wrapper.jar` — explicitly tracked wrapper jar to preserve build consistency.
- `out/` — standard Java/Kotlin binary compilation directories.

**JetBrains IntelliJ IDEA**

- `.idea/modules.xml`, `.idea/jarRepositories.xml`, `.idea/compiler.xml` — local editor preferences.
- `.idea/libraries/` — project-specific library mapping indices.
- `*.iml`, `*.iws`, `*.ipr` — workspace configurations unique to each developer machine.

**Cross-platform IDE support (Eclipse, NetBeans, VS Code)**

- **Eclipse:** `.apt_generated`, `.classpath`, `.factorypath`, `.project`, `.settings/`, `.springBeans`, `.sts4-cache`,
  `bin/`
- **NetBeans:** `/nbproject/private/`, `/nbbuild/`, `/dist/`, `/nbdist/`, `/.nb-gradle/`
- **VS Code:** `.vscode/`

**Ecosystem & operating system metadata**

- `.kotlin` — Kotlin compiler environment flags.
- `.DS_Store`, `Thumbs.db` — macOS and Windows desktop services metadata.

---

<div align="center">

<table>
  <tr>
    <td width="12%" align="center"><img src="./binary-knights-logo.png" /></td>
    <td width="76%" align="center"><img src="https://capsule-render.vercel.app/api?type=waving&color=0:6EE7B7,
50:38BDF8,100:8A5CF5&height=160&section=footer&text=Governed%20by%20the%20Knights,%20followed%20by%20everyone.
&fontSize=19&fontColor=FFFFFF&animation=fadeIn&fontAlignY=68" width="100%"/></td>
    <td width="12%" align="center"><img src="./binary-knights-logo.png" /></td>
  </tr>
</table>

</div>