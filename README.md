# JavaTech Frontend
# Socializer-frontend

Simple JavaFX frontend for the Socializer API. This project is a desktop client that provides login/registration, a posts feed, post details with comments, and basic user interactions (like, comment, create/delete when authorized).

**Key Features**
- **Authentication:** Login and registration flows (JWT-based auth handled by the API client).
- **Feed:** Display posts with images, captions, likes and comments.
- **Post details:** View full post and comments, add comments.
- **Ownership controls:** Delete buttons visible only to resource owners.
- **FXML UI:** Layouts defined with FXML and styled with `CSS`.

**Tech stack**
- **Java:** Requires Java 17+ (project uses the Java module system).
- **JavaFX:** UI implemented with JavaFX and FXML.
- **Maven:** Build tool with wrapper included (`mvnw`, `mvnw.cmd`).

**Repository layout (important files)**
- `src/main/java/com/example/demo` : main application package
   - `Launcher.java`, `SocializerApplication.java` : application entry points
   - `api/` : `ApiClient.java`, `AuthClient.java`, `PostClient.java`, `CommentClient.java`, `TagClient.java`, `UserClient.java` — HTTP clients for the backend
   - `controllers/` : `HomeController.java`, `LoginController.java`, `PostController.java`, `RegisterController.java` — UI controllers bound to FXML
   - `model/` : POJOs used by the UI (`Post`, `Comment`, `User`, `Tag`, `ApiResponse`)
- `src/main/resources/` : runtime resources
   - `client.properties` — frontend configuration (e.g. backend base URL)
   - `com/example/demo/fxml/*.fxml` — UI layouts (`home-view.fxml`, `login.fxml`, `post-view.fxml`, `register-view.fxml`)
   - `com/example/demo/css/style.css` — UI styling

Getting started
-----------------
Prerequisites
- JDK 17 or newer installed and `JAVA_HOME` set.
- (Optional) JavaFX SDK if running outside of a build that bundles JavaFX.

Build (Windows)
1. Open PowerShell in the project root.
2. Build with the Maven wrapper:

```powershell
.\mvnw.cmd clean package
```

Run
- In your IDE: import the project as a Maven project and run `SocializerApplication` (or `Launcher`) as a Java application.
- With the Maven wrapper (if `javafx-maven-plugin` is configured in the `pom.xml`):

```powershell
.\mvnw.cmd javafx:run
```

- If your `pom.xml` provides an `exec` configuration, you can run:

```powershell
.\mvnw.cmd -Dexec.mainClass="com.example.demo.SocializerApplication" exec:java
```

Configuration
--------------
- Backend URL: Edit `src/main/resources/client.properties` (or `target/classes/client.properties` after build) and set:

```
backend.baseUrl=http://localhost:7007
```

- The application reads this value to know where the API server is hosted.

Notes for development
---------------------
- UI: FXML files are in `src/main/resources/com/example/demo/fxml` and are wired to controllers in `src/main/java/com/example/demo/controllers`.
- Styling: `src/main/resources/com/example/demo/css/style.css`.
- API clients: check `src/main/java/com/example/demo/api` to see how HTTP requests are formed and how JWT tokens are attached.
- Module system: the project contains `module-info.java`. If you add dependencies, ensure module declarations are updated when necessary.

Contributing
------------
- Prefer small, focused pull requests.
- Keep UI and business logic separated: controllers should orchestrate UI and call API clients and models.

Troubleshooting
---------------
- If you see JavaFX errors at runtime, ensure either your IDE provides JavaFX on the module path or install the JavaFX SDK and use the `--module-path`/`--add-modules` JVM flags when running the jar.
- If API calls fail, check `client.properties` and ensure the backend is running and reachable.

License
-------
This repository does not include a license file. Add a `LICENSE` if you intend to make the project open-source.


