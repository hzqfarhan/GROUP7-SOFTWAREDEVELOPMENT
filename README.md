# UTHM Course Registration System - Group 7

##  Overview
The **UTHM Course Registration System** is a console-based Java application designed to manage student course enrollments. Built using **Java, Maven, and MySQL**, the system features two main modules:
- **Admin Module:** Allows administrators to manage students, manage courses, and oversee the entire registration database.
- **Student Module:** Enables students to securely log in with their Matric Number, view available courses, register for classes, and drop courses while adhering to credit limits and seat availability constraints.

Data is stored remotely using a cloud MySQL database (Railway), ensuring real-time synchronization between users.

---

##  Setup Tutorial (Maven Installation for Windows)

If you haven't installed Maven yet, follow these steps:

1. **Download Maven:** Download the binary zip archive from this link:  
   [apache-maven-3.9.16-bin.zip](https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip)
2. **Extract:** Right-click the downloaded zip file and select **Extract All**.
3. **Move Folder:** Move the extracted `apache-maven-3.9.16` folder to `C:\Program Files\`.
4. **Open Environment Variables:** Press the Windows key and search for **"Environment Variables"**.
5. Select **"Edit the system environment variables"** (this opens System Properties).
6. Click the **"Environment Variables..."** button at the bottom.
7. **Create MAVEN_HOME:** Under the **System Variables** section (the bottom half), click the **New...** button.
8. Fill in the details exactly as follows:
   - **Variable name:** `MAVEN_HOME`
   - **Variable value:** `C:\Program Files\apache-maven-3.9.16`
9. Click **OK**.
10. **Edit PATH:** Still under the **System Variables** section, scroll down to find the variable named **`Path`** (or `PATH`).
11. Select it and click **Edit...**.
12. Click **New** on the right side and paste the following:  
    `%MAVEN_HOME%\bin`
13. Click **OK** on all the open windows to save your changes and close System Properties.

---

##  Compile and Run Tutorial

Once you have cloned or downloaded the project code to your laptop, follow these steps to connect to the database and start the system:

1. Locate the file named `.env.example` in the main project folder.
2. Open it in a text editor (like Notepad or VS Code) and paste the following database credentials:
   ```env
   MYSQL_URL=jdbc
   MYSQL_USER=root
   MYSQL_PASSWORD=
   ```
3. **Rename** the file from `.env.example` to exactly **`.env`**.
4. Open your Terminal or Command Prompt inside the project directory.

### Useful Commands

* **To Compile the code:**
  ```bash
  mvn compile
  ```

* **To Run the system:**
  ```bash
  mvn exec:java
  ```

* **To Compile and Run at the same time (Recommended):**
  ```bash
  mvn compile exec:java
  ```
