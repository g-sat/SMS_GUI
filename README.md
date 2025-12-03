# Student Management System (Java 17, Modular, Swing UI, MySQL JDBC)

A clean, modern student database management app built with Java 17 modules, Swing UI (light/dark mode), and MySQL (JDBC). It demonstrates encapsulation, inheritance, and interfaces, with a service/repository architecture and a polished UI theme.

---

## Quick Start

- Prerequisites
  - Windows (cmd.exe terminal)
  - JDK 17 or later
  - MySQL Server (Workbench installed is fine)
  - MySQL Connector/J (already included in the repo under `mysql-connector-j-9.5.0/...`)

- DB credentials used by default
  - Host: `127.0.0.25`
  - Port: `3306`
  - Database: `Your Database name`
  - Username: `root`
  - Password: `****`

- UI Highlights
  - Modern gradient app bar
  - Light/dark mode toggle
  - Zebra-striped tables
  - Styled inputs with placeholders

---

## Clone the repository

```cmd
cd /d C:\my_programming\java\projrct_ticket_management
REM If you have a remote git URL, clone it here. Otherwise this step is optional.
REM Example:
REM git clone https://github.com/your-username/student-management-system.git
REM cd student-management-system
```

If you’re already working in the workspace provided, you can skip cloning and proceed to Build.

---

## Project Structure

```
Student_management_system/
  src/
    Student_management_system/
      module-info.java
      com/studentapp/
        App.java
        model/
          Person.java
          Student.java
        repository/
          StudentRepositoryContract.java
          StudentRepository.java
          JdbcStudentRepository.java
        service/
          StudentService.java
        ui/
          MainFrame.java
          StudentTableModel.java
  out/
    Student_management_system/
      module-info.class
      com/... compiled classes
  mysql-connector-j-9.5.0/
    mysql-connector-j-9.5.0/
      mysql-connector-j-9.5.0.jar
```

---

## Build (Java 17 modular)

```cmd
cd /d C:\my_programming\java\projrct_ticket_management\Student_management_system
javac --release 17 -d out --module-source-path src -m Student_management_system
```

Notes:
- This compiles the Java 17 module `Student_management_system` into the `out` directory.
- If you see path issues, verify the location of `module-info.java` is `src\Student_management_system\module-info.java`.

---

## Run (with MySQL Connector/J on the module path)

```cmd
cd /d C:\my_programming\java\projrct_ticket_management\Student_management_system
java --module-path out;C:\my_programming\java\projrct_ticket_management\Student_management_system\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar -m Student_management_system/com.studentapp.App
```

What this does:
- Launches the app entry point `com.studentapp.App`.
- Adds the MySQL Connector/J JAR to the module path so the JDBC driver is available.
- On first run, it will verify the `students` table schema and reconcile missing columns.

---

## Database Setup (automatic + manual)

The app tries to be resilient and automatically ensures the schema:
- Creates table `students` if missing:
  - `id INT PRIMARY KEY AUTO_INCREMENT`
  - `name VARCHAR(255) NOT NULL`
- Adds columns if missing:
  - `uid VARCHAR(36) NOT NULL UNIQUE`
  - `dob INT NULL` (stores dates as `yyyymmdd`)
  - `email VARCHAR(255) NOT NULL`
  - `major VARCHAR(255) NOT NULL`
- Backfills `uid` for existing rows using generated UUIDs.

If you want to create the starting schema yourself:

```sql
USE mydb;

CREATE TABLE IF NOT EXISTS students (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
);

ALTER TABLE students ADD COLUMN IF NOT EXISTS uid VARCHAR(36) NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uid_uniq ON students(uid);

ALTER TABLE students ADD COLUMN IF NOT EXISTS dob INT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS email VARCHAR(255) NOT NULL;
ALTER TABLE students ADD COLUMN IF NOT EXISTS major VARCHAR(255) NOT NULL;
```

Note: Some MySQL versions don’t support `IF NOT EXISTS` on `ADD COLUMN`. In that case, use the app’s automatic reconciliation or manually check `information_schema.COLUMNS` before altering.

---

## Configuration (URL, user, pass)

The app currently uses the following connection settings in `MainFrame`:
- URL: `jdbc:mysql://127.0.0.25:3306/Your_db_name`
- User: `root`
- Pass: `****`

If you need different settings, update `MainFrame` or pass JVM system properties and adjust wiring accordingly.

Optional (system properties wiring example):
```cmd
java -Ddb.url="jdbc:mysql://127.0.0.1:3306/mydb?useSSL=false&serverTimezone=UTC" -Ddb.user="root" -Ddb.pass="yourpass" --module-path out;C:\path\to\mysql-connector-j.jar -m Student_management_system/com.studentapp.App
```

---

## Usage

- Start the app
  - Use the Run command above.
- Add a student
  - Fill in Name, DOB (`yyyy-mm-dd`), Email, Major, then click “Add Student”.
- Search
  - Enter a name fragment in the header search field and click “Search”.
- Delete
  - Select a row and click “Delete Selected”.
- Light/Dark mode
  - Toggle the button on the header to switch themes.

---

## Troubleshooting

- "No suitable driver found": ensure the MySQL Connector/J JAR is present on the module path exactly as shown.
- "Access denied for user 'root'@'localhost'": verify username/password and that the user has privileges on `mydb`.
- Schema errors (e.g., unknown column): the app auto-reconciles columns. If it persists, manually create columns as shown above.
- Path issues (module not found): confirm your `module-info.java` path and rebuild.
- Dark mode not repainting correctly: the theme toggle resets component styles and repaints; if any widget looks off, resize the window once (forces full repaint).

---

## Architecture & Design Notes

- Java 17 modules: `module-info.java` declares dependencies on `java.desktop` (Swing) and `java.sql` (JDBC).
- Encapsulation & inheritance: `Person` (abstract) and `Student` (final) with validated setters.
- Interfaces & layering: `StudentRepositoryContract`, in-memory `StudentRepository`, and JDBC `JdbcStudentRepository`, consumed by `StudentService`.
- UI: `MainFrame` and `StudentTableModel`; Nimbus LAF with a themed palette.
- Identity: App uses `uid` (UUID, VARCHAR(36)); DB keeps `id INT AUTO_INCREMENT` as internal.
- DOB storage: `INT` as `yyyymmdd` -> converts to/from `LocalDate`.

---

## Common Commands (copy/paste)

Build:
```cmd
cd /d C:\my_programming\java\projrct_ticket_management\Student_management_system
javac --release 17 -d out --module-source-path src -m Student_management_system
```

Run (driver on module path):
```cmd
cd /d C:\my_programming\java\projrct_ticket_management\Student_management_system
java --module-path out;C:\my_programming\java\projrct_ticket_management\Student_management_system\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar -m Student_management_system/com.studentapp.App
```

Clean rebuild:
```cmd
cd /d C:\my_programming\java\projrct_ticket_management\Student_management_system
rd /s /q out
javac --release 17 -d out --module-source-path src -m Student_management_system
```

---

## Beautiful Tips

- Try toggling dark mode for nighttime work; colors have been tuned for balanced contrast.
- Resize the window to enjoy responsive layout and font scaling.
- Use concise names and valid emails; inputs are validated for clarity and security.

---

## License

This project includes the MySQL Connector/J under its respective license (see `mysql-connector-j-9.5.0/LICENSE`). Your application code can be adapted to your preferred license.
