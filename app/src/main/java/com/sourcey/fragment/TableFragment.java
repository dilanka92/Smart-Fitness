package com.sourcey.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sourcey.smartfitness.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class TableFragment extends Fragment {

    EditText ageText;
    EditText heightText;
    EditText weightText;
    Button update_button;

    String height, weight, age, UserEmail;

    public TableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_table, container, false);

        ageText = (EditText) rootView.findViewById(R.id.input_age);
        weightText = (EditText) rootView.findViewById(R.id.input_weight);
        heightText = (EditText) rootView.findViewById(R.id.input_height);
        update_button = (Button) rootView.findViewById(R.id.btn_update);

        UserEmail = getArguments().getString("user");

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        return rootView;
    }

    public void update() {
        Log.d(TAG, "update");

        if (!validate()) {
            onUpdateFailed();
            return;
        }

        update_button.setEnabled(false);
        age = ageText.getText().toString();
        height = weightText.getText().toString();
        weight = heightText.getText().toString();

        updateUser();
    }

    public boolean validate() {
        boolean valid = true;

        age = ageText.getText().toString();
        height = weightText.getText().toString();
        weight = heightText.getText().toString();

        if (age.isEmpty()) {
            Toast.makeText(getActivity(), "enter age", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (height.isEmpty()) {

            Toast.makeText(getActivity(), "enter height", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (weight.isEmpty()) {
            Toast.makeText(getActivity(), "enter weight", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    public void onUpdateSuccess() {
        Toast.makeText(getActivity(), "Update success", Toast.LENGTH_LONG).show();
        update_button.setEnabled(true);

    }

    public void onUpdateFailed() {
        Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Update failed");

        update_button.setEnabled(true);
    }

    private void updateUser() {
        new asyncUpdate().execute(age, weight, height, UserEmail);
    }

    private class asyncUpdate extends AsyncTask<String, Void, String> {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Updating data...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String age = arg0[0];
                String weight = arg0[1];
                String height = arg0[2];
                String email = arg0[3];
                String link = "http://192.168.1.92/ServiceFitness/updateUser.php";
                URL url = new URL(link);

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("age", age)
                        .appendQueryParameter("weight", weight)
                        .appendQueryParameter("height", height)
                        .appendQueryParameter("email", email);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("false");
                }

            } catch (Exception e) {
                return ("false");
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            System.out.println("Result --- " + result);

            if (!result.equals(0)) {
                onUpdateSuccess();
            } else {
                onUpdateFailed();

            }

        }
    }
}
