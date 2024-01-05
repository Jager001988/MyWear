package com.zeta.mywear;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ApiManager {

    private static final String BASE_URL = "https://demo.zetaconsulting.it/z16/trunk/api/web/v1/wears";
    private static final String BASE_URL_TOOLS = "https://api.toolsadm.zetaconsulting.it/v1/restapis";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String COUNTINTERVENTI = "/countInterventi";
    private static final String GETAZIENDE = "/getAziendez16";
    private static final String LISTINTERVENTI = "/listInterventi";
    private static final String UPDATEINTERVENTO = "/updateIntervento";
    private static final String SENDTOKEN = "/sendToken";

    // Metodo per eseguire una chiamata GET
    public static void get(String token, ApiCallback callback) {
        String url = BASE_URL;
        new ApiTask(callback).execute(url, "GET", null, token);
    }

    // Metodo per eseguire una chiamata POST per il login
    public static void login(String badgeCode, String badgeCodePwd,String tokenFireBase, ApiCallback callback) throws JSONException {
        String url = BASE_URL + LOGIN_ENDPOINT;
        JSONObject requestBody = new JSONObject();
        requestBody.put("badgecode", badgeCode);
        requestBody.put("badgecodepwd", badgeCodePwd);
        requestBody.put("tokenFireBase", tokenFireBase);
        new ApiTask(callback).execute(url, "POST", requestBody.toString(), null);
    }
    public static void sendToken(String token, String tokenFireBase, ApiCallback callback) throws JSONException {
        String url = BASE_URL + SENDTOKEN;
        JSONObject requestBody = new JSONObject();
        requestBody.put("tokenFireBase", tokenFireBase);
        new ApiTask(callback).execute(url, "POST", requestBody.toString(), token);
    }

    public static void countInterventi(String token, ApiCallback callback) throws JSONException {
        String url = BASE_URL + COUNTINTERVENTI;
        new ApiTask(callback).execute(url, "GET", null, token);
    }
    public static void listInterventi(String token, int id_stato, ApiCallback callback) throws JSONException {
        String url = BASE_URL + LISTINTERVENTI +"?id_stato="+id_stato;

        new ApiTask(callback).execute(url, "GET", null, token);
    }

    public static void updateIntervento(String token,int id_intervento, int id_stato, ApiCallback callback) throws JSONException {
        String url = BASE_URL + UPDATEINTERVENTO +"?id="+id_intervento+"&id_stato="+id_stato;
        new ApiTask(callback).execute(url, "GET", null, token);
    }
    public static void getAziende(ApiCallback callback) throws JSONException {
        String url = BASE_URL_TOOLS + GETAZIENDE;
        new ApiTask(callback).execute(url, "GET", null,null);
    }

    // Classe AsyncTask per gestire le chiamate API in background
    private static class ApiTask extends AsyncTask<String, Void, ApiResponse> {

        private ApiCallback callback;

        public ApiTask(ApiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ApiResponse doInBackground(String... params) {
            String url = params[0];
            String method = params[1];
            String data = params[2];
            String token = params[3];

            try {
                JSONObject json = null;
                if(method.equals("POST")){
                    URL apiUrl = new URL(url);
                    HttpsURLConnection con = (HttpsURLConnection) apiUrl.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);
                    String jsonInputString = data;
                    try(OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        json = new JSONObject(response.toString());
                    }

                    return new ApiResponse(200, json);
                }else{
                    URL apiUrl = new URL(url);
                    HttpsURLConnection con = (HttpsURLConnection) apiUrl.openConnection();
                    con.setRequestMethod("GET");

                    con.setRequestProperty("Authorization", "Bearer " + token);
                    con.setRequestProperty( "charset", "utf-8" );
                    con.connect();
                    int status = con.getResponseCode();
                    //TODO !=200 logut
                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        json = new JSONObject(response.toString());
                    }
                    return new ApiResponse(200, json);
                }

            } catch (Exception e) {
                Log.e("ApiManager", "Errore durante la chiamata API", e);
                return new ApiResponse(-1, null);
            }
        }

        @Override
        protected void onPostExecute(ApiResponse result) {
            if (callback != null) {
                try {
                    callback.onResponse(result);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Interfaccia per il callback delle risposte API
    public interface ApiCallback {
        void onResponse(ApiResponse response) throws JSONException;
    }

    // Classe per rappresentare una risposta API
    public static class ApiResponse {
        private int statusCode;
        private JSONObject body;

        public ApiResponse(int statusCode, JSONObject body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public JSONObject getBody() {
            return body;
        }
    }
}