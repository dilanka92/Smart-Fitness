package com.sourcey.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.sourcey.neuralnetwork.NeuralNetwork;
import com.sourcey.smartfitness.R;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ConnectFragment extends Fragment {
    private TextView txtResult;
    private Button validate;
    private Spinner lifeStyle;

    public ConnectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
        txtResult = (TextView) rootView.findViewById(R.id.txt_result);
        validate = (Button) rootView.findViewById(R.id.btn_check);
        lifeStyle = (Spinner) rootView.findViewById(R.id.gender);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
                try {
                    new NetworkASYNC().execute();

                } catch (Exception e) {
                    System.err.println("Ouch An Error");
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        return rootView;
    }

    private void setData(String data) {
        data = data + String.valueOf(lifeStyle.getSelectedItem());
        txtResult.setText(data);
    }

    private class NetworkASYNC extends AsyncTask<String, Void, String> {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Predicting...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... val) {
            try {
                NeuralNetwork network = new NeuralNetwork();
                network.train(getActivity());
                return String.valueOf(network.predict());
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
            setData(result);
        }
    }
}
