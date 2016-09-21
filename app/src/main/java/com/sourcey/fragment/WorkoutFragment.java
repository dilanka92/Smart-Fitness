package com.sourcey.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
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

public class WorkoutFragment extends Fragment {
    private Button calculate;
    private TextView result;
    private Spinner lifeStyles;

    private String id;
    private String userEmail;

    public WorkoutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fixtures, container, false);

        calculate = (Button) rootView.findViewById(R.id.btn_calculateTDEE);
        result = (TextView) rootView.findViewById(R.id.lbl_TDEE);
        lifeStyles = (Spinner) rootView.findViewById(R.id.spin_lifeStyle);

        userEmail = getArguments().getString("user");

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    id = String.valueOf(lifeStyles.getSelectedItemId());
                    new tdeeASYNC().execute(id, userEmail);
                } catch (Exception e) {
                    System.err.println("Ouch An Error");
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    private void setData(String tdee) {
        result.setText(tdee);
    }

    private boolean addToDataSet(double weight, double height, int age, String gender, double bmr) {
        System.out.println("Adding to data set");
        boolean status = false;
        try {
            String link = "http://192.168.1.92/ServiceFitness/addToDataSet.php";
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
                    .appendQueryParameter("weight", String.valueOf(weight))
                    .appendQueryParameter("height", String.valueOf(height))
                    .appendQueryParameter("age", String.valueOf(age))
                    .appendQueryParameter("gender", String.valueOf(gender))
                    .appendQueryParameter("bmr", String.valueOf(bmr));
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
                if (Boolean.valueOf(result.toString())) {
                    status = addToTestDataSet(weight, height, age, gender, bmr);
                }
                return status;
            } else {

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean addToTestDataSet(double weight, double height, int age, String gender, double bmr) {
        System.out.println("Adding to test data set");
        try {
            String link = "http://192.168.1.92/ServiceFitness/addToTestDataSet.php";
            String sql;
            URL url = new URL(link);

            // Setup HttpURLConnection class to send and receive data from php and mysql
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            if ("Male".equalsIgnoreCase(gender)) {
                sql = "INSERT  INTO `male_bmr`(`Weight`,`Height`,`Age`,`Gender`,`BMR`)" +
                        "VALUES (" + weight + "," + height + "," + age + ",'" + gender + "'," + null + ")";
            } else {
                sql = "INSERT  INTO `female_bmr`(`Weight`,`Height`,`Age`,`Gender`,`BMR`)" +
                        "VALUES (" + weight + "," + height + "," + age + ",'" + gender + "'," + null + ")";
            }
            System.out.println("sql " + sql);
            // Append parameters to URL
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("sql", sql);
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
                return (Boolean.valueOf(result.toString()));

            } else {

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class tdeeASYNC extends AsyncTask<String, Void, String> {
        private static final String TAG = "Async TDEE";
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Calculating TDEE...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            double bmr;
            double tdee = 0.0;
            int age;
            double weight;
            double height;
            String gender;
            try {
                int id = Integer.valueOf(arg0[0]);
                String email = arg0[1];

                String link = "http://192.168.1.92/ServiceFitness/selectUser.php";
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


                    //Predicting BMR
                    NeuralNetwork network = new NeuralNetwork();
                    network.train(getActivity());
                    bmr = network.predict(weight, height, age, gender);

                    Log.i(TAG, "BMR Value " + bmr);
                    //If failed to predict
                    if (bmr == 0.0) {
                        if (Looper.myLooper() == null)
                            Looper.prepare();
                        if ("Male".equalsIgnoreCase(gender)) {
                            bmr = (10 * (weight) + 6.25 * (height) - 5 * age - 161);
                        } else {
                            bmr = (10 * (weight) + 6.25 * (height) - 5 * age + 5);
                        }
                        boolean status = addToDataSet(weight, height, age, gender, bmr);
                        if (status) {
                            Toast.makeText(getActivity(), "Information updated", Toast.LENGTH_LONG).show();
                            Log.i(TAG, "Added to the data set");
                        }
                    }

                    switch (id) {
                        case 0:
                            tdee = 1.2 * bmr;
                            break;
                        case 1:
                            tdee = 1.375 * bmr;
                            break;
                        case 2:
                            tdee = 1.55 * bmr;
                            break;
                        case 3:
                            tdee = 1.725 * bmr;
                            break;
                        case 4:
                            tdee = 1.9 * bmr;
                    }

                }
                // Pass data to onPostExecute method
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String newTDEE = decimalFormat.format(tdee);
                return String.valueOf(newTDEE);
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
            if (result == "0.0") {
                Toast.makeText(getActivity(), "Prediction failed", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Prediction failed");
            } else {
                setData(result);
            }
        }
    }
}
