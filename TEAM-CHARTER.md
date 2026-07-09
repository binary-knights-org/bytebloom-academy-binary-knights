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








## 4. Architecture & Repository Guidelines ( Kamal Ashour )
### Target Directory Models
The repository will follow a modular architecture structured around core packages and distinct use cases to separate concerns within our Kotlin setup:
* `/src/main/kotlin/packages/`: Dedicated to core configurations, shared utilities, and common data models.
* `/src/main/kotlin/usecases/`: Dedicated to specific business logic implementations and individual application features.

### .gitignore Exclusions
To ensure local build outputs and IDE configurations are not tracked, the root `.gitignore` strictly excludes:
* **Build Tools & Outputs:** `.gradle/`, `build/`
* **IDE Settings:** `.idea/`, `*.iml`
* **OS Files:** `.DS_Store`, `Thumbs.db`