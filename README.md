# JavaTech Frontend
## Features
- User login and registration with JWT authentication
- Feed displaying posts with images, captions, likes, and comments
- Conditional buttons for deleting posts/comments (only visible for owners)
- Post details view with full comments
- Logout functionality

---

## TechStack
- Java 17+
- JavaFX 20+
- HttpClient for API requests
- FXML for UI layout
---

### Setup Instructions
1. Clone the repo
```bash 
git clone https://github.com/Wassim-php/Sociolizer-frontend.git
```
2. Configure backend URL in src/main/resources/client.properties
```bash
backend.baseUrl=http://localhost:7007
```
3. Run the Application
   Using IDE (IntelliJ/STS):
- Open the project
- Run MainApp.java

4. Login or Register a new user. JWT will be stored in memory for API requests
