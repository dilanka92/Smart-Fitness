package com.sourcey.smartfitness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sourcey.neuralnetwork.NeuralNetwork;

public class MainActivity extends AppCompatActivity {


    private TextView userName;
    private TextView txtResult;
    private Button validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);

        userName = (TextView) findViewById(R.id.txt_user);
        txtResult = (TextView) findViewById(R.id.txt_result);
        validate = (Button) findViewById(R.id.btn_check);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
                try {
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();

                    predict();
//                    progressDialog.dismiss();
                } catch (Exception e) {
                    System.err.println("Ouch An Error");
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                userName.setText(data.getStringExtra("com/sourcey/user"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void predict() {
        try {
            NeuralNetwork network = new NeuralNetwork();
            network.train(MainActivity.this);

            txtResult.setText("Predicted : " + network.predict());
        } catch (Exception e) {
            txtResult.setText("Prediction Failed!");
            txtResult.setTextColor(5);
            e.printStackTrace();
        }

    }
}
