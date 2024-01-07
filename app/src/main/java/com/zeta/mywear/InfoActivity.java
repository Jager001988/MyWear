package com.zeta.mywear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "MyPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        String linkAz = preferences.getString("linkAzienda", null);
        TextView linkTextView = findViewById(R.id.linkazienda);
        linkTextView.setText(linkAz);

        String nomeAzienda = preferences.getString("nomeAzienda", null);
        TextView nomeAziendaTextView = findViewById(R.id.azienda);
        nomeAziendaTextView.setText(nomeAzienda);

        String badge = preferences.getString("badge", null);
        TextView badgeTextView = findViewById(R.id.badge);
        badgeTextView.setText(badge);

        String nome = preferences.getString("username", null);
        TextView usernameTextView = findViewById(R.id.nome);
        usernameTextView.setText(nome);


    }
}