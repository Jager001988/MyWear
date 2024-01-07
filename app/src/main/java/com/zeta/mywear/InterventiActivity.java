package com.zeta.mywear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.zeta.mywear.databinding.ActivityInterventiBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InterventiActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "MyPreferences";
    private ActivityInterventiBinding binding;




    protected void onResume() {
        super.onResume();
        doInitialization();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doInitialization();
    }
    private void doInitialization() {
        binding = ActivityInterventiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Integer id_stato = getIntent().getIntExtra("id_stato",999);
        try {
            ApiManager.listInterventi(getBaseUrl(), getToken(), id_stato, new ApiManager.ApiCallback() {
                @Override
                public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                    if (response.getStatusCode() == 200) {

                        List<Intervento> listInterventi = new ArrayList<>();
                        JSONArray respArray = response.getBody().getJSONArray("resp");
                        // Iterare attraverso gli elementi dell'array utilizzando un ciclo for
                        for (int i = 0; i < respArray.length(); i++) {
                            // Ottenere l'oggetto JSON interno
                            JSONObject innerObject = respArray.getJSONObject(i);
                            // Leggere i valori desiderati
                            String impianto = innerObject.getString("impianto");
                            String tipo_intervento = innerObject.getString("tipo_intervento");
                            String data_richiesta = innerObject.getString("data_richiesta");
                            String stato = innerObject.getString("stato");
                            Integer id_stato = innerObject.getInt("id_stato");
                            Integer id_intervento = innerObject.getInt("id_intervento");
                            Intervento intervento = new Intervento();
                            intervento.setImpianto(impianto);
                            intervento.setTipo_intervento(tipo_intervento);
                            intervento.setData_richiesta(data_richiesta);
                            intervento.setStato(stato);
                            intervento.setId_stato(id_stato);
                            intervento.setId_intervento(id_intervento);
                            listInterventi.add(intervento);
                        }

                        RecyclerView recyclerView = findViewById(R.id.listInterventi);

                        ContactsAdapter adapter = new ContactsAdapter(listInterventi);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InterventiActivity.this));

                    } else if (response.getStatusCode() == 401){
                        // Gestire il fallimento del login
                        setLogged(false);
                        startActivity(new Intent(InterventiActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else {
                        //errore
                        Toast.makeText(InterventiActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("token", null);
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