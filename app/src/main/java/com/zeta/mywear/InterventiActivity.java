package com.zeta.mywear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.zeta.mywear.databinding.ActivityInterventiBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterventiActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "MyPreferences";
    private ActivityInterventiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInterventiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Integer id_stato = getIntent().getIntExtra("id_stato",999);

        try {
            ApiManager.listInterventi(getToken(), id_stato, new ApiManager.ApiCallback() {
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
                            String tipoIntervento = innerObject.getString("tipoIntervento");
                            String data_richiesta = innerObject.getString("data_richiesta");
                            String stato = innerObject.getString("stato");
                            Integer id_stato = innerObject.getInt("id_stato");
                            Integer id_intervento = innerObject.getInt("id_intervento");
                            Intervento intervento = new Intervento();
                            intervento.setImpianto(impianto);
                            intervento.setTipoIntervento(tipoIntervento);
                            intervento.setDataRichiesta(data_richiesta);
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
}