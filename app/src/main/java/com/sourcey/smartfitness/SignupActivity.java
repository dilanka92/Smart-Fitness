package com.sourcey.smartfitness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sourcey.user.HashPassword;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_age)
    EditText _ageText;
    @Bind(R.id.input_height)
    EditText _heightText;
    @Bind(R.id.input_weight)
    EditText _weightText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    private Spinner genders;

    private String name;
    private String gender;
    private String email;
    private String password;
    private Double height;
    private Double weight;
    private int age;


    private HashPassword MD5 = new HashPassword();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        genders = (Spinner) findViewById(R.id.gender);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        name = _nameText.getText().toString();
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        age = Integer.parseInt(_ageText.getText().toString());
        height = Double.parseDouble(_heightText.getText().toString());
        weight = Double.parseDouble(_weightText.getText().toString());
        gender = genders.getSelectedItem().toString();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        registerUser();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    private void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent intent = new Intent();
        intent.putExtra("com/sourcey/user", email);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Registration failed");

        _signupButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String age = _ageText.getText().toString();
        String height = _heightText.getText().toString();
        String weight = _weightText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (age.isEmpty()) {
            _ageText.setError("enter age");
            valid = false;
        } else {
            _ageText.setError(null);
        }
        if (height.isEmpty()) {
            _heightText.setError("enter height");
            valid = false;
        } else {
            _heightText.setError(null);
        }
        if (weight.isEmpty()) {
            _weightText.setError("enter weight");
            valid = false;
        } else {
            _weightText.setError(null);
        }

        return valid;
    }

    private void registerUser() {
        String hashPassword = MD5.Hash(password);
        int userType = 1;
        new asyncSignup().execute(email, hashPassword, name, String.valueOf(userType),
                String.valueOf(age), String.valueOf(weight), String.valueOf(height), gender);
    }

    private class asyncSignup extends AsyncTask<String, Void, String> {

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Creating Account...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String email = arg0[0];
                String hashPassword = arg0[1];
                String name = arg0[2];
                String userType = arg0[3];
                String age = arg0[4];
                String weight = arg0[5];
                String height = arg0[6];
                String gender = arg0[7];
                String link = "http://192.168.1.92/ServiceFitness/newUser.php";
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
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("password", hashPassword)
                        .appendQueryParameter("userType", userType)
                        .appendQueryParameter("name", name)
                        .appendQueryParameter("age", age)
                        .appendQueryParameter("weight", weight)
                        .appendQueryParameter("height", height)
                        .appendQueryParameter("gender", gender);
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

            if (!result.equals("false")) {
                onSignupSuccess();
            } else {
                onSignupFailed();

            }

        }
    }
}