package main.java.mrf;

import java.util.ArrayList;

public class Normalize
{

    private static double getMean(ArrayList<Double> vec)
    {
        int n = vec.size() > 1 ? vec.size() : 1;
        Double meanscore = 0.0;
        for(Double d:vec)
        {
            meanscore+=d;
        }
        return (meanscore/n);
    }

//    private static double getStd(ArrayList<Double> vec,Double mean)
//    {
//        double summation=0.0;
//        int n = vec.size() > 1 ? vec.size() : 2;
//        for(Double d:vec)
//        {
//            summation += d - mean;
//        }
//
//        Double d1 = 1/(double) (n-1);
//        Double sum = summation;
//        Double res = Math.sqrt(d1*sum);
//        return res;
//    }

    static double  getVariance(ArrayList<Double> vec) {
        double mean = getMean(vec);
        double temp = 0;
        for(double a :vec)
            temp += (a-mean)*(a-mean);
        return temp/(vec.size()-1);
    }

    static double  getStdDev(ArrayList<Double> vec) {
        return Math.sqrt(getVariance(vec));
    }

    public static ArrayList<Double> getZScoreNormalized(ArrayList<Double> unnorm)
    {
        ArrayList<Double> norm = new ArrayList<>();
        Double mean = getMean(unnorm);
        Double std = getStdDev(unnorm);

        for(double d:unnorm)
        {
            double zscore = (d - mean)/std;
            norm.add(zscore);
        }
        return norm;
    }
}
