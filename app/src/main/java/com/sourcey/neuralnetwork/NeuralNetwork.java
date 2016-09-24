package com.sourcey.neuralnetwork;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.experiment.InstanceQuery;

import static com.sourcey.user.MySqlHandler.PASSWORD;
import static com.sourcey.user.MySqlHandler.URL;
import static com.sourcey.user.MySqlHandler.USER;


public class NeuralNetwork {
    private static final String TAG = "NeuralNetwork";
    private final static String FILENAME = "example.save";
    private File file;

    public void train(Context context) throws Exception {
        try {
            Log.i(TAG, "Training...");

            Class.forName("com.mysql.jdbc.Driver").newInstance();

            file = new File(context.getExternalFilesDir(null), FILENAME);
            FileOutputStream fos = new FileOutputStream(file);

            // load training data from database
            InstanceQuery query = new InstanceQuery();
            query.setDatabaseURL(URL);
            query.setUsername(USER);
            query.setPassword(PASSWORD);
            query.setQuery("select * from bmr_all");
            Instances data = query.retrieveInstances();
            data.setClassIndex(4);

            MultilayerPerceptron mlp = new MultilayerPerceptron();
            mlp.setOptions(Utils.splitOptions("-L 0.5 -M 0.2 -N 2000 -V 0 -S 0 -E 20 -H 4"));

            mlp.buildClassifier(data);

            // save model + header
            Vector v = new Vector();
            v.add(mlp);
            v.add(new Instances(data, 0));
            SerializationHelper.write(fos, v);

            fos.flush();
            fos.close();
            Log.i(TAG, "Training finished!");
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }

    }

    public double predict(double weight, double height, int age, String gender) throws Exception {
        Log.i(TAG, "Predicting...");
        String sql;
        double valueBMI = 0.0;
        try {
            if ("Male".equalsIgnoreCase(gender)) {
                sql = "SELECT * FROM male_bmr WHERE `Weight`=" + weight + " AND `Height`=" + height + " AND `Age`=" + age + "";
            } else {
                sql = "SELECT * FROM female_bmr WHERE `Weight`=" + weight + " AND `Height`=" + height + " AND `Age`=" + age + "";
            }
            // load data from database that needs predicting
            InstanceQuery query = new InstanceQuery();
            query.setDatabaseURL(URL);
            query.setUsername(USER);
            query.setPassword(PASSWORD);
            System.out.println(sql);
            query.setQuery(sql);
            Instances data = query.retrieveInstances();
            data.setClassIndex(4);

            FileInputStream inputStream = new FileInputStream(file);
            Vector v = (Vector) SerializationHelper.read(inputStream);
            Classifier cl = (Classifier) v.get(0);
            Instances header = (Instances) v.get(1);

            // output predictions
            Log.i(TAG, "actual -> predicted");
            for (int i = 0; i < data.numInstances(); i++) {
                Instance curr = data.instance(i);
                Instance inst = new DenseInstance(header.numAttributes());
                inst.setDataset(header);
                for (int n = 0; n < header.numAttributes(); n++) {
                    Attribute att = data.attribute(header.attribute(n).name());
                    // original attribute is also present in the current dataset
                    if (att != null) {
                        if (att.isNominal()) {
                            // "att != null" is only used to avoid problems with nominal
                            // attributes that have 0 labels, which can easily happen with
                            // data loaded from a database
                            if ((header.attribute(n).numValues() > 0) && (att.numValues() > 0)) {
                                String label = curr.stringValue(att);
                                int index = header.attribute(n).indexOfValue(label);
                                if (index != -1) {
                                    inst.setValue(n, index);
                                }
                            }
                        } else if (att.isNumeric()) {
                            inst.setValue(n, curr.value(att));
                        } else {
                            Log.e(TAG, "Unhandled attribute type!");
                        }
                    }
                }
                // predict class
                double predictedBMI = cl.classifyInstance(inst);
                valueBMI = predictedBMI;
                Log.i(TAG, inst.classValue() + " -> " + predictedBMI);
            }
            Log.i(TAG, "Predicting finished!");
            return valueBMI;
        } catch (Exception e) {
            Log.getStackTraceString(e);
            return valueBMI;
        }
    }
}
