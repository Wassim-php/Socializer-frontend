*** Begin README

# Socializer — JavaFX Frontend

Socializer is a desktop JavaFX client that connects to a JSON REST API (the Socializer backend). The application provides an easy-to-use UI for browsing and interacting with posts (images + captions), creating and editing posts (with image uploads), and interacting with user profiles and comments.

What's included
- Authentication: login and registration screens with JWT-based authentication. The app stores the JWT in the `ApiClient` helper and attaches it to authorized requests.
- Feed & navigation: the main feed (`home-view.fxml`) is a `ListView` of posts; each item navigates to a per-post detail screen (`post-view.fxml`) showing the image, full caption, and comments.
- Post creation & editing: the post creation UI (`PostCreationController`) lets users select an image from disk, preview it, and create a post. Image uploads are performed as multipart POSTs to the backend via `ApiClient.uploadFile` (see notes below).
- Comments: add comments from the post detail screen; comments are shown in a `ListView` in `post-view.fxml`.
- Profile actions: profile and ownership checks are implemented in controllers to show/hide edit and delete actions for posts owned by the current user.

Project layout (key files and folders)
- `src/main/java/com/example/demo`
  - `Launcher.java`, `SocializerApplication.java` — app bootstrap and JavaFX startup
  - `api/` — API layer
    - `ApiClient.java` — central HTTP helper, JWT management, and `uploadFile(File)` implementation
    - `PostClient.java`, `UserClient.java`, `CommentClient.java`, `TagClient.java`, `AuthClient.java` — resource-specific clients
  - `controllers/` — JavaFX controllers bound to FXML screens
    - `HomeController` — feed listing and navigation
    - `LoginController`, `RegisterController` — auth flows
    - `PostController` — post detail, comments and comment posting
    - `PostCreationController`, `PostEditorController` — create/edit flows with image selection and upload
  - `model/` — POJOs used for JSON mapping (`Post`, `Comment`, `User`, `Tag`, `ApiResponse`)
- `src/main/resources/`
  - `client.properties` — runtime config (e.g. `backend.baseUrl`)
  - `com/example/demo/fxml/` — FXML layouts: `home-view.fxml`, `login.fxml`, `post-view.fxml`, `register-view.fxml`, `profile-view.fxml`, etc.
  - `com/example/demo/css/style.css` — app styling used by FXML

Build and run (Windows PowerShell)
1. Build the project:

```powershell
.\mvnw.cmd clean package
```

2. Run from IDE: import the Maven project and run `com.example.demo.SocializerApplication`.

3. Run via plugin (if configured in `pom.xml`):

```powershell
.\mvnw.cmd javafx:run
```

Configuration
- Edit `src/main/resources/client.properties` to set the backend URL. Example:

```
backend.baseUrl=http://localhost:7007
```

Key implementation details and developer notes
- Multipart uploads: `ApiClient.uploadFile(File)` constructs multipart requests and logs the upload URI, payload size, and server response to help debugging. If your backend expects a different form-field name (for example `image`), update the form field name accordingly in `ApiClient`.
- Threading and UI: `PostCreationController.handleCreatePost()` runs image upload + post-create flows on a background thread and disables the create button during the operation to prevent duplicate submissions and UI blocking. UI updates (alerts, scene navigation) are performed via `Platform.runLater(...)`.
- JWT handling: `ApiClient` stores the JWT and attaches `Authorization: Bearer <token>` when present. Some server responses may provide a refreshed token via a response header (e.g., `X-New-Auth-Token`); `UserClient.updateUserProfile` contains logic to refresh the locally stored token when provided.
- Error handling and logs: controller-level alerts are used to surface errors to users; the client prints upload status and server response body to stdout/stderr to help diagnose problems such as HTML responses, redirects, or unexpected status codes.

Common troubleshooting steps
- If uploads cause the backend to redirect or return unexpected HTML, check the console logs for the `Uploading file to:` message and the server response body — this often indicates an authentication/redirect or a routing issue on the server.
- If authentication is failing, verify `client.properties` points to the correct backend and that the JWT is set after login.
- If the server rejects multipart fields, change the field name in the `ApiClient` multipart builder (the FXML and controllers only select files and pass them to the client layer).

Where to inspect or extend functionality
- UI screens: `src/main/resources/com/example/demo/fxml/*` and corresponding controllers in `src/main/java/com/example/demo/controllers`.
- API layer and endpoints: `src/main/java/com/example/demo/api`.
- Models and JSON mapping: `src/main/java/com/example/demo/model`.

Testing & iterative workflow
- Use the Maven wrapper to build quickly after changes:

```powershell
.\mvnw.cmd -DskipTests=true package
```

- Run the app from your IDE for iterative UI testing. Watch console output for request/response logs when exercising upload and auth flows.











