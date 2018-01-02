package com.helencoder.classification;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;
import meka.core.MLUtils;
import meka.core.Result;
import meka.filters.unsupervised.attribute.MekaClassAttributes;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

import java.io.File;


/**
 * meka示例
 *
 * Created by helencoder on 2017/8/8.
 */
public class MekaExamples {
    public static void main(String[] args) {
        // MEKA使用示例

        try {
            String trainFile = "data/meka/Music.arff";
            String testFile = "data/meka/Music_missing_classes.arff";
            String recordFile = "data/meka/record.arff";
            String percentage = "20";
            String[] arr = {trainFile, testFile};

            trainAndPredict(arr);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Prepares a dataset for use in Meka, if it isn't already prepared properly
     * (the relation name in an ARFF file used by Meka stores information on how
     * many attributes from the left are used as class attributes).
     * <br>
     * Expects the following parameters: &lt;input&gt; &lt;attribute_indices&gt; &lt;output&gt;
     * <br>
     * The "input" parameter points to a dataset that Meka can read (eg CSV or ARFF).
     * The "attribute_indices" parameter is a comma-separated list of 1-based indices
     * of the attributes to use as class attributes in Meka.
     * The "output" parameters is the filename where to store the generated output data (as ARFF).
     *
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void prepareClassAttributes(String[] args) throws Exception {
        if (args.length != 3)
            throw new IllegalArgumentException("Required parameters: <input> <attribute_indices> <output>");

        System.out.println("Loading input data: " + args[0]);
        Instances input = DataSource.read(args[0]);

        System.out.println("Applying filter using indices: " + args[1]);
        MekaClassAttributes filter = new MekaClassAttributes();
        filter.setAttributeIndices(args[1]);
        filter.setInputFormat(input);
        Instances output = Filter.useFilter(input, filter);

        System.out.println("Saving filtered data to: " + args[2]);
        ArffSaver saver = new ArffSaver();
        saver.setFile(new File(args[2]));
        DataSink.write(saver, output);
    }


    /**
     * Builds a BR Meka classifier on a dataset supplied by the user.
     * <br>
     * Expected parameters: &lt;dataset&gt;
     * <br>
     * Note: The dataset must have been prepared for Meka already.
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void justBuild(String[] args) throws Exception {
        if (args.length != 1)
            throw new IllegalArgumentException("Required arguments: <dataset>");

        System.out.println("Loading data: " + args[0]);
        Instances data = DataSource.read(args[0]);
        MLUtils.prepareData(data);

        System.out.println("Build BR classifier");
        BR classifier = new BR();
        // further configuration of classifier
        classifier.buildClassifier(data);
    }


    /**
     * Cross-validates a BR Meka classifier on a dataset supplied by the user.
     * <br>
     * Expected parameters: &lt;dataset&gt;
     * <br>
     * Note: The dataset must have been prepared for Meka already.
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void crossValidate(String[] args) throws Exception {
        if (args.length != 1)
            throw new IllegalArgumentException("Required arguments: <dataset>");

        System.out.println("Loading data: " + args[0]);
        Instances data = DataSource.read(args[0]);
        MLUtils.prepareData(data);

        int numFolds = 10;
        System.out.println("Cross-validate BR classifier using " + numFolds + " folds");
        BR classifier = new BR();
        // further configuration of classifier
        String top = "PCut1";
        String vop = "3";
        Result result = Evaluation.cvModel(classifier, data, numFolds, top, vop);

        System.out.println(result);
    }


    /**
     * Builds and evaluates a BR Meka classifier on user supplied train/test datasets.
     * <br>
     * Expected parameters: &lt;train&gt; &lt;test&gt;
     * <br>
     * Note: The datasets must have been prepared for Meka already and compatible.
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void trainTestSet(String[] args) throws Exception {
        if (args.length != 2)
            throw new IllegalArgumentException("Required arguments: <train> <test>");

        System.out.println("Loading train: " + args[0]);
        Instances train = DataSource.read(args[0]);
        MLUtils.prepareData(train);

        System.out.println("Loading test: " + args[1]);
        Instances test = DataSource.read(args[1]);
        MLUtils.prepareData(test);

        // compatible?
        String msg = train.equalHeadersMsg(test);
        if (msg != null)
            throw new IllegalStateException(msg);

        System.out.println("Build BR classifier on " + args[0]);
        BR classifier = new BR();
        // further configuration of classifier
        classifier.buildClassifier(train);

        System.out.println("Evaluate BR classifier on " + args[1]);
        String top = "PCut1";
        String vop = "3";
        Result result = Evaluation.evaluateModel(classifier, train, test, top, vop);

        System.out.println(result);
    }


    /**
     * Builds and evaluates a BR Meka classifier on a train/test split dataset supplied by the user.
     * <br>
     * Expected parameters: &lt;dataset&gt; &lt;percentage&gt;
     * <br>
     * Note: The dataset must have been prepared for Meka already.
     * And the percentage must be between 0 and 100.
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void trainTestSplit(String[] args) throws Exception {
        if (args.length != 2)
            throw new IllegalArgumentException("Required arguments: <dataset> <percentage>");

        System.out.println("Loading data: " + args[0]);
        Instances data = DataSource.read(args[0]);
        MLUtils.prepareData(data);

        double percentage = Double.parseDouble(args[1]);
        int trainSize = (int) (data.numInstances() * percentage / 100.0);
        Instances train = new Instances(data, 0, trainSize);
        Instances test = new Instances(data, trainSize, data.numInstances() - trainSize);

        System.out.println("Build BR classifier on " + percentage + "%");
        BR classifier = new BR();
        // further configuration of classifier
        classifier.buildClassifier(train);

        System.out.println("Evaluate BR classifier on " + (100.0 - percentage) + "%");
        String top = "PCut1";
        String vop = "3";
        Result result = Evaluation.evaluateModel(classifier, train, test, top, vop);

        System.out.println(result);
    }


    /**
     * Builds a BR Meka classifier on user supplied train dataset and outputs
     * predictions on a supplied dataset with missing class values.
     * <br>
     * Expected parameters: &lt;train&gt; &lt;predict&gt;
     * <br>
     * Note: The datasets must have been prepared for Meka already and compatible.
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void trainAndPredict(String[] args) throws Exception {
        if (args.length != 2)
            throw new IllegalArgumentException("Required arguments: <train> <predict>");

        System.out.println("Loading train: " + args[0]);
        Instances train = DataSource.read(args[0]);
        MLUtils.prepareData(train);

        System.out.println("Loading predict: " + args[1]);
        Instances predict = DataSource.read(args[1]);
        MLUtils.prepareData(predict);

        // compatible?
        String msg = train.equalHeadersMsg(predict);
        if (msg != null)
            throw new IllegalStateException(msg);

        System.out.println("Build BR classifier on " + args[0]);
        BR classifier = new BR();
        // further configuration of classifier
        classifier.buildClassifier(train);

        System.out.println("Use BR classifier on " + args[1]);
        for (int i = 0; i < predict.numInstances(); i++) {
            double[] dist = classifier.distributionForInstance(predict.instance(i));
            System.out.println((i+1) + ": " + Utils.arrayToString(dist));
        }
    }


    /**
     * Builds and evaluates a BR Meka classifier on user supplied train/test datasets
     * and outputs the predictions on the test to the specified file
     * (ARFF or CSV, auto-detect based on extension).
     * <br>
     * Expected parameters: &lt;train&gt; &lt;test&gt; &lt;test&gt; &lt;output&gt;
     * <br>
     * Note: The datasets must have been prepared for Meka already and compatible.
     *       The format of the output file is determined by its extension
     *       (eg .arff or .csv).
     *
     * @author FracPete (fracpete at waikato dot ac dot nz)
     * @version $Revision$
     */
    public static void exportPredictionsOnTestSet(String[] args) throws Exception {
        if (args.length != 3)
            throw new IllegalArgumentException("Required arguments: <train> <test> <output>");

        System.out.println("Loading train: " + args[0]);
        Instances train = DataSource.read(args[0]);
        MLUtils.prepareData(train);

        System.out.println("Loading test: " + args[1]);
        Instances test = DataSource.read(args[1]);
        MLUtils.prepareData(test);

        // compatible?
        String msg = train.equalHeadersMsg(test);
        if (msg != null)
            throw new IllegalStateException(msg);

        System.out.println("Build BR classifier on " + args[0]);
        BR classifier = new BR();
        // further configuration of classifier
        classifier.buildClassifier(train);

        System.out.println("Evaluate BR classifier on " + args[1]);
        String top = "PCut1";
        String vop = "3";
        Result result = Evaluation.evaluateModel(classifier, train, test, top, vop);

        System.out.println(result);

        System.out.println("Saving predictions test set to " + args[2]);
        Instances performance = Result.getPredictionsAsInstances(result);
        DataSink.write(args[2], performance);
    }

}
