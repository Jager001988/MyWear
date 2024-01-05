package com.zeta.mywear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zeta.mywear.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private static final String PREFERENCES_NAME = "MyPreferences";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_BADGE = "badge";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";
    private ArrayList<Azienda> dataList;
    private Button login_button;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner mySpinner;
    private SpinAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        login_button = findViewById(R.id.loginButton);
        dataList = new ArrayList<>();
        mySpinner = findViewById(R.id.spinner);
        try {
            ApiManager.getAziende( new ApiManager.ApiCallback() {
                @Override
                public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                    if (response.getStatusCode() == 200) {
                        // Login riuscito, ottenere il token dalla risposta
                        JSONArray respArray = response.getBody().getJSONArray("resp");
                        // Iterare attraverso gli elementi dell'array utilizzando un ciclo for
                        for (int i = 0; i < respArray.length(); i++) {
                            // Ottenere l'oggetto JSON interno
                            JSONObject innerObject = respArray.getJSONObject(i);
                            // Leggere i valori desiderati
                            String nome = innerObject.getString("nome");
                            String link = innerObject.getString("link");
                            Azienda az = new Azienda();
                            az.setLink(link);
                            az.setNome(nome);
                            dataList.add(az);
                        }
                        Azienda[] array = dataList.toArray(new Azienda[0]);

                        adapter = new SpinAdapter(LoginActivity.this,
                                android.R.layout.rowazienda,
                                array);
                        mySpinner = (Spinner) findViewById(R.id.spinner);
                        mySpinner.setAdapter(adapter); // Set the custom adapter to the spinner
                        // You can create an anonymous listener to handle the event when is selected an spinner item
                        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view,
                                                       int position, long id) {
                                // Here you get the current item (a User object) that is selected by its position
                                Azienda user = adapter.getItem(position);

                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapter) {  }
                        });

                       // ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, dataList);
                        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        //spinner.setAdapter(adapter);

                    } else {
                        Toast.makeText(LoginActivity.this, "Errore nel recupero delle aziende", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esegui la logica di autenticazione
                String codiceBadge = binding.usernameEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                FirebaseApp tmp = FirebaseApp.initializeApp(LoginActivity.this);
                FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                if (TextUtils.isEmpty(codiceBadge) || TextUtils.isEmpty(password)) {
                    showMessage("Inserisci username e password.");
                } else {
                    // Ottenere il token dell'istanza corrente
                    firebaseMessaging.getToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String tokenFireBase = task.getResult();
                                    try {
                                        ApiManager.login(codiceBadge, password,tokenFireBase, new ApiManager.ApiCallback() {
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
                                                    Toast.makeText(LoginActivity.this, "Errore durante il login", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Errore durante il login", Toast.LENGTH_SHORT).show();
                                }
                            });
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