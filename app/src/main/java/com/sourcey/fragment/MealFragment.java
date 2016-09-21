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
import android.widget.TextView;
import android.widget.Toast;

import com.sourcey.neuralnetwork.NeuralNetwork;
import com.sourcey.smartfitness.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MealFragment extends Fragment {
    private TextView txtBMR;
    private Button validate;

    private String userEmail;

    public MealFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
        txtBMR = (TextView) rootView.findViewById(R.id.lbl_BMR);
        validate = (Button) rootView.findViewById(R.id.btn_check);

        userEmail = getArguments().getString("user");

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new NetworkASYNC().execute(userEmail);

                } catch (Exception e) {
                    System.err.println("Ouch An Error");
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    private void setData(String data) {
        txtBMR.setText(data + " kcal");
    }


    private class NetworkASYNC extends AsyncTask<String, Void, String> {
        private static final String TAG = "NetworkASYNC";
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Calculating...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            int age = 0;
            double bmr = 0.0;
            double weight = 0.0;
            double height = 0.0;
            String gender = null;
            try {
                String email = arg0[0];

                String link = "http://192.168.1.92/ServiceFitness/selectUser.php";
                URL url = new URL(link);

                // Setup HttpURLConnection class to send and receive data from php and mysql
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
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
                    input.close();
                    reader.close();
                    JSONObject json = new JSONObject(result.toString());

                    age = Integer.valueOf(json.getString("Age"));
                    weight = Double.valueOf(json.getString("Weight"));
                    height = Double.valueOf(json.getString("Height"));
                    gender = json.getString("Gender");
                }

                //Predicting BMR
                NeuralNetwork network = new NeuralNetwork();
                network.train(getActivity());
                bmr = network.predict(weight, height, age, gender);

                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String newBMR = decimalFormat.format(bmr);

                return newBMR;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result != null) {
                setData(result);
            } else {
                Toast.makeText(getActivity(), "Prediction failed", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Prediction failed");
            }

        }
    }
}