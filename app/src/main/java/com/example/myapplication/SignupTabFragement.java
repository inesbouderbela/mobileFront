package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupTabFragement extends Fragment {
    TextView pass;
    TextView email;
    TextView confpass;
    TextView username;
    Button button;
    int v=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState){
        ViewGroup root=(ViewGroup) inflater.inflate(R.layout.signup_tab_fragement,container,false);

        pass=root.findViewById(R.id.pass);
        email=root.findViewById(R.id.email);
        confpass=root.findViewById(R.id.confpass);
        username=root.findViewById(R.id.username);
        button = root.findViewById(R.id.butTicketbtn);

        button.setOnClickListener(view -> {
            String emailValue = email.getText().toString();
            String passValue = pass.getText().toString();
            String confpassValue = confpass.getText().toString();
            String user = username.getText().toString();
            sendSignUpRequest(emailValue, passValue, user);
        });

        return root;
    }

    private void sendSignUpRequest(String email, String pass, String user) {
        String url = Constants.BASE_URL+"api/auth/register";  // Remplace avec l'IP locale de ton PC

        // Créer un objet JSON à envoyer dans le corps de la requête
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", pass);
            jsonBody.put("username", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Créer une requête POST avec Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Réponse de l'API Spring Boot
                    Toast.makeText(getActivity(), "User registered: " + response.toString(), Toast.LENGTH_LONG).show();
                },
                error -> {

                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Ajouter la requête à la file d'attente de Volley
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(jsonObjectRequest);
    }

}
