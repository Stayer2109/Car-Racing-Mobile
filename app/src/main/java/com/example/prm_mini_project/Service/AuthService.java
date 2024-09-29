package com.example.prm_mini_project.Service;

import com.example.prm_mini_project.Entity.User;
import com.google.gson.Gson;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthService  {
    // convert user to json
    public String userToJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    // convert json to user
    public User jsonToUser(String json) {
        if (!json.startsWith("{")) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    // hash password using Bcrypt
    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    // check password
    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }
}
