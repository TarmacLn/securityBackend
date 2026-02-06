package com.softwareengineering.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwareengineering.dto.AvatarBody;
import com.softwareengineering.dto.LoginBody;
import com.softwareengineering.dto.RegisterBody;
import com.softwareengineering.models.User;
import com.softwareengineering.models.enums.UserTypeEnum;
import com.softwareengineering.services.UserService;
import com.softwareengineering.utils.AuthUtils;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
    public static void init(Javalin app) {
        app.get("/users", UserController::getUsers);
        app.post("/register", UserController::registerUser);
        app.post("/login", UserController::loginUser);
        app.get("/login", UserController::getLogin);
        app.get("/logout", UserController::logoutUser);
        app.post("/update-avatar", UserController::updateAvatar);
        app.get("/avatar", UserController::getAvatar);
        app.post("/update-user", UserController::updateUser);
    }

    private static void registerUser(Context context) {
        try {
            RegisterBody body = context.bodyAsClass(RegisterBody.class);
            body.validate();
            
            if (UserService.registerUser(body)) {
                context.status(201).json(Map.of("message", "User registered successfully"));
            } else {
                context.status(400).json(Map.of("message", "User registration failed"));
            }
        } catch (Exception e) {
            context.status(400).json(Map.of("message", "Validation error: " + e.getMessage()));
        }
    }

    private static void loginUser(Context context) {
        try {
            LoginBody body = context.bodyAsClass(LoginBody.class);
            body.validate();
            
            User loggedInUser = UserService.loginUser(body.email, body.password);

            context.sessionAttribute("userType", loggedInUser.get("userType"));
            context.sessionAttribute("userEmail", loggedInUser.get("email"));
            context.sessionAttribute("id", loggedInUser.get("id"));
            context.status(200).json(getUserData(loggedInUser));
        } catch (Exception e) {
            context.status(400).json(Map.of("message", "Validation error: " + e.getMessage()));
        }
    }

    private static void getLogin(Context context) {
        String email = context.sessionAttribute("userEmail");
        int id = context.sessionAttribute("id");
        String userType = context.sessionAttribute("userType");

        if (email == null || userType == null || id == 0) {
            context.status(401).json(Map.of("message", "Unauthorized"));
            return;
        }

        User loggedInUser = User.findFirst("id = ?", id);
        context.status(200).json(getUserData(loggedInUser));
    }

    private static void updateAvatar(Context context) {
        try {
            int id = context.sessionAttribute("id");
            if (id == 0) {
                context.status(401).json(Map.of("message", "Unauthorized"));
                return;
            }
            AvatarBody body = context.bodyAsClass(AvatarBody.class);
            body.validate();
            
            boolean updated = UserService.updateAvatar(id, body.avatar);
            if (!updated) {
                context.status(400).json(Map.of("message", "Failed to update avatar"));
                return;
            }
            context.status(200).json(Map.of("message", "Avatar updated successfully"));
        } catch (Exception e) {
            context.status(400).json(Map.of("message", "Validation error: " + e.getMessage()));
        }
    }

    private static void getAvatar(Context context) {
        int id = context.sessionAttribute("id");
        if (id == 0) {
            context.status(401).json(Map.of("message", "Unauthorized"));
            return;
        }

        String avatar = UserService.getAvatar(id);

        if (avatar == null) {
            avatar = "";
        }
        context.status(200).json(Map.of("avatar", avatar));
    }

    private static void logoutUser(Context context) {
        context.sessionAttribute("userType", "");
        context.sessionAttribute("id", 0);
        context.sessionAttribute("userEmail", "");
        context.status(200).json(Map.of("message", "Logged out successfully"));
    }

    private static Map<String, Object> getUserData(User user) {
        Map<String, Object> userData = new HashMap<>();
        addIfNotNull(userData, "userType", user.get("userType"));
        addIfNotNull(userData, "email", user.get("email"));
        addIfNotNull(userData, "fullName", user.get("fullName"));
        addIfNotNull(userData, "phone", user.get("phone"));
        addIfNotNull(userData, "amka", user.get("amka"));
        addIfNotNull(userData, "licenceID", user.get("licenceID"));
        addIfNotNull(userData, "speciality", user.get("speciality"));
        addIfNotNull(userData, "officeLocation", user.get("officeLocation"));
        addIfNotNull(userData, "bio", user.get("bio"));
        addIfNotNull(userData, "isDark", user.getIsDark());

        return userData;
    }

    private static void updateUser(Context context) {
        try {
            UserTypeEnum userType = AuthUtils.getUserTypeFromSession(context);
            int id = AuthUtils.validateUserAndGetId(context, userType);
            RegisterBody userData = context.bodyAsClass(RegisterBody.class);
            userData.validateForUpdate(); // Use lenient validation for updates
            
            boolean updated = UserService.updateUser(id, userData);
            if (!updated) {
                context.status(400).json(Map.of("message", "Failed to update user"));
                return;
            }
            context.status(200).json(Map.of("message", "User updated successfully"));
        } catch (AuthUtils.UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
        catch (Exception e) {
            context.status(400).json(Map.of("message", "Validation error: " + e.getMessage()));
        }
    }

    private static void getUsers(Context context) {
        List<Map<String, Object>> users = UserService.getUsers();
        context.json(users);
    }

    private static void addIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
