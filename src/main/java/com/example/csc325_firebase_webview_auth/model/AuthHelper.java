package com.example.csc325_firebase_webview_auth.model;

import com.example.csc325_firebase_webview_auth.view.App;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AuthHelper {

    private static String webApiKey;

    private static String getWebApiKey() {
        if (webApiKey != null) {
            return webApiKey;
        }
        try {
            Properties props = new Properties();
            InputStream in = AuthHelper.class.getResourceAsStream("/files/firebase-config.properties");
            props.load(in);
            webApiKey = props.getProperty("webApiKey");
        } catch (Exception e) {
            webApiKey = "";
        }
        return webApiKey;
    }

    public static boolean register(String email, String password) {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(false)
                .setDisabled(false);

        try {
            App.fauth.createUser(request);
            return true;
        } catch (FirebaseAuthException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean signIn(String email, String password) {
        try {
            String apiKey = getWebApiKey();
            URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            BufferedReader reader;
            if (code >= 200 && code < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString().contains("idToken");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
