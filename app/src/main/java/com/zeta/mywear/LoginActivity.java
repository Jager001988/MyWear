package com.zeta.mywear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.zeta.mywear.databinding.ActivityLoginBinding;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private static final String PREFERENCES_NAME = "MyPreferences";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_BADGE = "badge";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";

    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        login_button = findViewById(R.id.loginButton);


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esegui la logica di autenticazione
                String codiceBadge = binding.usernameEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();

                if (TextUtils.isEmpty(codiceBadge) || TextUtils.isEmpty(password)) {
                    showMessage("Inserisci username e password.");
                } else {
                    try {
                        ApiManager.login(codiceBadge, password, new ApiManager.ApiCallback() {
                            @Override
                            public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                                if (response.getStatusCode() == 200) {
                                    // Login riuscito, ottenere il token dalla risposta
                                    String token = response.getBody().getString("token");
                                    String username = response.getBody().getString("username");
                                    saveCredentials(codiceBadge, password, token, username);
                                    setLogged();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                } else {
                                    // Gestire il fallimento del login
                                    Log.e("MainActivity", "Errore durante il login: " + response.getBody());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        // Controllo se è il primo avvio
        if (isLogged()) {
            // L'utente ha già effettuato il login in precedenza
            startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {

        }
    }

    private boolean isLogged() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("isLogged", false);
    }

    private void setLogged() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogged", true);
        editor.apply();
    }

    private void saveCredentials(String badge, String password, String token, String username) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_BADGE, badge);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }


    private String getSavedUsername() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USERNAME, "");
    }

    private void showMessage(String message) {
        // Utilizza la tua logica per mostrare messaggi all'utente
    }
}