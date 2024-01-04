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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeta.mywear.databinding.ActivityUpdateBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityUpdateBinding binding;
    private static final String PREFERENCES_NAME = "MyPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Integer id_intervento = getIntent().getIntExtra("id_intervento",0);
        Integer id_stato = getIntent().getIntExtra("id_stato",999);
        String impianto = getIntent().getStringExtra("impianto");
        String stato = getIntent().getStringExtra("stato");
        String dataRichiesta = getIntent().getStringExtra("dataRichiesta");
        String tipoIntervento = getIntent().getStringExtra("tipoIntervento");

        TextView impiantoTextView = findViewById(R.id.impianto);
        impiantoTextView.setText(impianto);

        TextView tipoInterventoTextView = findViewById(R.id.tipoIntervento);
        tipoInterventoTextView.setText(tipoIntervento);

        TextView statoTextView = findViewById(R.id.stato);
        statoTextView.setText(stato);
        TextView dataTextView = findViewById(R.id.dataRichiesta);
        dataTextView.setText(dataRichiesta);

        Button button_lavora = findViewById(R.id.lavora);
        Button button_chiudi = findViewById(R.id.chiudi);

        if(id_stato.equals(TypeStato.APERTO.getId())){
            button_chiudi.setVisibility(View.GONE);
        }else if (id_stato.equals(TypeStato.IN_CORSO.getId())){
            button_lavora.setVisibility(View.GONE);
        }else {
            button_chiudi.setVisibility(View.GONE);
            button_lavora.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        String itemName = intent.getStringExtra("KEY_NAME");

        button_lavora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ApiManager.updateIntervento(getToken(), id_intervento, TypeStato.IN_CORSO.getId(), new ApiManager.ApiCallback() {
                        @Override
                        public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                            if (response.getStatusCode() == 200) {
                                Integer code = response.getBody().getInt("code");
                                String message = response.getBody().getString("message");
                                Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                if(code.equals(0)){
                                    button_lavora.setVisibility(View.GONE);
                                }
                            } else if (response.getStatusCode() == 401){
                                // Gestire il fallimento del login
                                setLogged(false);
                                startActivity(new Intent(UpdateActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            } else {
                                //errore
                                Toast.makeText(UpdateActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        button_chiudi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ApiManager.updateIntervento(getToken(), id_intervento, TypeStato.CHIUSO.getId(), new ApiManager.ApiCallback() {
                        @Override
                        public void onResponse(ApiManager.ApiResponse response) throws JSONException {
                            if (response.getStatusCode() == 200) {
                                Integer code = response.getBody().getInt("code");
                                String message = response.getBody().getString("message");
                                Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                if(code.equals(0)){
                                    button_chiudi.setVisibility(View.GONE);
                                }
                            } else if (response.getStatusCode() == 401){
                                // Gestire il fallimento del login
                                setLogged(false);
                                startActivity(new Intent(UpdateActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            } else {
                                //errore
                                Toast.makeText(UpdateActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_update);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
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