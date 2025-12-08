*** Begin README

# Socializer-frontend

Simple JavaFX desktop client for the Socializer API. The app provides authentication (JWT), a posts feed with images and likes, post creation/editing with image uploads, and basic user/profile interactions.

## Key Features
- **Authentication:** login/register flows and local JWT handling.
- **Feed:** list posts with images, captions, likes and comments.
- **Post creation & editing:** upload images and create/update posts.
- **Ownership controls:** delete and edit are available only for resource owners.
- **FXML + CSS UI:** layouts in FXML and styling via CSS.

## Tech stack
- Java 17+ (module system)
- JavaFX for UI
- Maven with wrappers (`mvnw`, `mvnw.cmd`)

## Repository layout (important files)
- `src/main/java/com/example/demo`
   - `Launcher.java`, `SocializerApplication.java` — application entry points
   - `api/` — HTTP client helpers and resource clients: `ApiClient.java`, `AuthClient.java`, `PostClient.java`, `CommentClient.java`, `TagClient.java`, `UserClient.java`
   - `controllers/` — JavaFX controllers: `HomeController`, `LoginController`, `RegisterController`, `ProfileController`, `UserProfileController`, `PostCreationController`, `PostEditorController`, `PostController`, `EditProfileController`, `AvatarController` and others
   - `model/` — POJOs: `Post`, `Comment`, `User`, `Tag`, `ApiResponse`, `UserUpdate`
- `src/main/resources/`
   - `client.properties` — runtime config (backend URL)
   - `com/example/demo/fxml/*.fxml` — UI layouts
   - `com/example/demo/css/style.css` — UI styling

## Getting started

### Prerequisites
- JDK 17 or newer and `JAVA_HOME` set.
- If running outside an IDE that provides JavaFX, install JavaFX SDK and use `--module-path`/`--add-modules` when launching.

### Build (Windows PowerShell)
```powershell
.\mvnw.cmd clean package
```

### Run
- IDE: import as Maven project and run `com.example.demo.SocializerApplication` (or `Launcher`).
- Maven wrapper (if configured):

```powershell
.\mvnw.cmd javafx:run
```

### Configuration
- Set the backend URL in `src/main/resources/client.properties`:

```
backend.baseUrl=http://localhost:7007
```

## Recent changes (Dec 2025)
- Improved file upload handling in `ApiClient.uploadFile`:
   - Multipart body built with explicit UTF-8 encoding and `Content-Transfer-Encoding: binary`.
   - Request logs now print upload URI, payload size and server response to help debugging.
   - Authorization header is no longer sent when there is no JWT (prevents sending `Bearer null`).
   - Accepts HTTP 200 or 201 as success.
- Post creation (`PostCreationController`) now runs the upload + create flow on a background thread and disables the create button while running to prevent duplicate clicks.
- `UserClient.updateUserProfile` reads an `X-New-Auth-Token` response header and updates the local JWT when present.
- Brief single-line comments were added across controllers and models to improve readability for developers.

## Notes on upload-related backend loops
- If your backend appears to enter a redirect/loop when receiving uploads, check the frontend console for the `Uploading file to:` log and response details (status, body). Typical causes:
   - Server redirects POSTs to a login page (e.g. returns HTML instead of JSON) — look for 3xx or 302 responses or HTML bodies in the logs.
   - Server expects a different multipart field name (e.g. `image` vs `file`). Adjust the `name="file"` in `ApiClient.uploadFile` if your server expects `image`.
   - Authentication problems: sending an invalid or missing JWT can cause the server to redirect to auth flows — ensure `client.properties` points to the correct backend and that you are logged in.

## Where to look in code
- Upload and auth: `src/main/java/com/example/demo/api/ApiClient.java`
   - `uploadFile(File)` builds and sends multipart uploads and logs responses.
   - `login`, `register`, `logout` manage JWT state; `updateJwtToken` refreshes token from responses.
- Post creation: `src/main/java/com/example/demo/controllers/PostCreationController.java` (background upload + create)
- Post editing: `src/main/java/com/example/demo/controllers/PostEditorController.java` (uploads when saving)

## Debugging tips
- Start the app and reproduce the upload; watch the console (IDE run console or terminal) for the `Uploading file to:` log and the server response body.
- If the response contains HTML or a redirect, inspect the backend logs or ensure the backend upload endpoint accepts multipart POSTs at `/api/uploads`.
- If needed, change the multipart form field name in `ApiClient.uploadFile` to match the backend (`name="image"` or `name="file"`).

## Contributing
- Small focused PRs are preferred. Keep UI logic in controllers minimal and delegate HTTP to the API clients in `api/`.

## Troubleshooting
- JavaFX runtime errors: ensure JavaFX is available on the module path or run from an IDE that configures it.
- Uploads failing: check console logs, backend accessibility, and the `client.properties` URL.

## Next steps I can help with
- Run a Maven build locally and report compile errors.
- Change the multipart field name to match your backend and re-test uploads.
- Add more detailed request/response logging (headers+body) for upload debugging.

## License
Add a `LICENSE` file if you plan to publish this project.

*** End README


