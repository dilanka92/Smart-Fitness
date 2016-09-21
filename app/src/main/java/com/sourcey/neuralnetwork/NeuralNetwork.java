package com.sourcey.neuralnetwork;

import android.content.Context;

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
    private final static String FILENAME = "example.save";
    private File file;

    public void train(Context context) throws Exception {
        try {
            System.out.println("Training...");

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
            System.out.println("Training finished!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public double predict(double weight, double height, int age, String gender) throws Exception {
        System.out.println("Predicting...");
        String sql;
        try {
            if ("Male".equalsIgnoreCase(gender)) {
                sql = "SELECT * FROM male_bmr WHERE `Weight`=" + weight + " AND `Height`=" + height + " AND `Age`=" + age + "";
            } else {
                sql = "SELECT * FROM female_bmr WHERE `Weight`=" + weight + " AND `Height`=" + height + " AND `Age`=" + age + "";
            }
            double predVal = 0.0;
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
            System.out.println("actual -> predicted");
            for (int i = 0; i < data.numInstances(); i++) {
                Instance curr = data.instance(i);
                // create an instance for the classifier that fits the training data
                // Instances object returned here might differ slightly from the one
                // used during training the classifier, e.g., different order of
                // nominal values, different number of attributes.
                // Instance inst = new Instance(header.numAttributes());
                Instance inst = new DenseInstance(header.numAttributes());
                inst.setDataset(header);
                for (int n = 0; n < header.numAttributes(); n++) {
                    Attribute att = data.attribute(header.attribute(n).name());
                    // original attribute is also present in the current dataset
                    if (att != null) {
                        if (att.isNominal()) {
                            // is this label also in the original data?
                            // Note:
                            // "numValues() > 0" is only used to avoid problems with nominal
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
                            throw new IllegalStateException("Unhandled attribute type!");
                        }
                    }
                }

                // predict class
                double pred = cl.classifyInstance(inst);
                predVal = pred;
                System.out.println(inst.classValue() + " -> " + pred);
            }

            System.out.println("Predicting finished!");
            return predVal;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
