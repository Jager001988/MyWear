package com.zeta.mywear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "MyPreferences";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";
    private TextView username;
    private Button buttonChiusi;
    private Button buttonAperti;
    private Button buttonInCorso;

    private ProgressBar progressBar;

    @Override
    protected void onResume() {
        super.onResume();

        // Alcuni codici da eseguire quando l'activity diventa visibile dopo essere stata in pausa
        doInitialization();
    }


    private void doInitialization() {


        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        buttonChiusi = findViewById(R.id.buttonChiusi);
        buttonAperti = findViewById(R.id.buttonAperti);
        buttonInCorso = findViewById(R.id.buttonInCorso);
        progressBar = findViewById(R.id.progressBar);
        // Recupera i parametri dall'intent
        progressBar.setVisibility(View.VISIBLE);
        String id_intervento = null;
        String stato = null;
        String tipo_intervento = null;
        String data_richiesta = null;
        String impianto = null;
        String id_stato = null;
        try{
            id_intervento = getIntent().getExtras().getString("id_intervento");
            stato = getIntent().getExtras().getString("stato");
            tipo_intervento = getIntent().getExtras().getString("tipo_intervento");
            data_richiesta = getIntent().getExtras().getString("data_richiesta");
            impianto = getIntent().getExtras().getString("impianto");
            id_stato = getIntent().getExtras().getString("id_stato");
        }catch (Exception e){

        }
        if(id_intervento != null && stato != null && tipo_intervento != null && data_richiesta != null && impianto != null && id_stato != null){
            Intent intent = new Intent(MainActivity.this, UpdateActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("id_stato",TypeStato.APERTO.getId());
            intent.putExtra("id_intervento",Integer.valueOf(id_intervento));
            intent.putExtra("stato",stato);
            intent.putExtra("tipo_intervento",tipo_intervento);
            intent.putExtra("data_richiesta",data_richiesta);
            intent.putExtra("impianto",impianto);
            startActivity(intent);
            finish();
        }
        buttonChiusi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chiamo il backend per avere i dati
                Intent intent = new Intent(MainActivity.this, InterventiActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id_stato",TypeStato.CHIUSO.getId());
                startActivity(intent);
            }

        });
        buttonAperti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chiamo il backend per avere i dati
                Intent intent = new Intent(MainActivity.this, InterventiActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id_stato",TypeStato.APERTO.getId());
                startActivity(intent);
            }
        });
        buttonInCorso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chiamo il backend per avere i dati
                Intent intent = new Intent(MainActivity.this, InterventiActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id_stato",TypeStato.IN_CORSO.getId());
                startActivity(intent);
            }
        });

        if (!isLogged()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {

            //CARICO il count per la prima pagina
            try {
                username.setText(getUsername());
                ApiManager.countInterventi(getBaseUrl(), getToken(), new ApiManager.ApiCallback() {
                    @Override
                    public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                        progressBar.setVisibility(View.GONE);
                        if (response.getStatusCode() == 200) {
                            JSONArray respArray = response.getBody().getJSONArray("resp");
                            // Iterare attraverso gli elementi dell'array utilizzando un ciclo for
                            for (int i = 0; i < respArray.length(); i++) {
                                // Ottenere l'oggetto JSON interno
                                JSONObject innerObject = respArray.getJSONObject(i);
                                // Leggere i valori desiderati
                                String stato = innerObject.getString("stato");
                                String idStato = innerObject.getString("id_stato");
                                String count = innerObject.getString("count");
                                if(idStato.equals("0")){
                                    buttonAperti.setText("Aperti #"+count);
                                }else if(idStato.equals("1")){
                                    buttonInCorso.setText("IN CORSO #"+count);
                                }else if(idStato.equals("2")){
                                    buttonChiusi.setText("Chiusi #"+count);
                                }
                            }

                        } else {
                            // Gestire il fallimento del login
                            Toast.makeText(MainActivity.this, "Errore nel recuperodati dal Server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            //carico i dati
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doInitialization();
    }




    private boolean isLogged() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("isLogged", false);
    }

    private String getToken() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("token", null);
    }
    private String getUsername() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("username", null);
    }

    public void logout(View view) {
        showConfirmationDialog();
    }

    public void info(View view) {
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Sei sicuro di voler effettuare il logout?")
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Esegui l'azione confermata, ad esempio, startActivity
                        setLogged(false);
                        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                        // Ottenere il token dell'istanza corrente
                        firebaseMessaging.getToken()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String tokenFireBase = task.getResult();
                                        try {
                                            ApiManager.logout(getBaseUrl(),  getToken(),tokenFireBase, new ApiManager.ApiCallback() {
                                                @Override
                                                public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                                                    if (response.getStatusCode() == 200) {
                                                        int tt=0;
                                                    }
                                                }
                                            });
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                });
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // L'utente ha annullato l'azione, puoi gestire questa situazione se necessario
                    }
                })
                .show();
    }

    private void setLogged(boolean logged) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogged", logged);
        editor.apply();
    }
    private String getBaseUrl() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("linkAzienda", null);
    }
}