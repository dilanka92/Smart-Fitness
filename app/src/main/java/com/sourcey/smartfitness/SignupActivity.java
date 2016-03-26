package com.sourcey.smartfitness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import user.HashPassword;
import user.User;
import user.UserDatabaseHandler;

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

    String name;
    String email;
    String password;
    int age;
    Double height;
    Double weight;


    HashPassword MD5 = new HashPassword();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

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

    public void signup() {
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


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        registerUser();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent intent = new Intent();
        intent.putExtra("user", name);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
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
//        if (lifestyle.isEmpty()) {
//            ((TextView)_input_lifestyleText.getSelectedView()).setError("select lifestyle");
//            valid = false;
//        } else {
//           // _input_lifestyleText.setError(null);
//            ((TextView)_input_lifestyleText.getSelectedView()).setError(null);
//        }

        return valid;
    }

    private void registerUser() {
        try {
            UserDatabaseHandler db = new UserDatabaseHandler(this);
            Log.d("Insert: ", "Inserting ..");
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            boolean status = db.createUser(new User(name, MD5.Hash(password), email, height, weight, age));
            if (!status) {
                onSignupFailed();
            } else {
                onSignupSuccess();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error creating user : " + ex.toString());
            onSignupFailed();
        }
    }
}