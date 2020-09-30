package mttdat.utils;

import java.text.DecimalFormat;

public class NumberUtils {
    public static boolean isFloat(float value) {

        return (value - Math.floor(value)) != 0 ? true : false;
    }

    public static String round2Str(float number, String formatStr){
        DecimalFormat df = new DecimalFormat(formatStr);

        return df.format(number).replace(",", ".");
    }

    public static String round2Str(float number, int decimal){
        return String.format("%." + decimal + 'f', number).replace(",", ".");
    }

    public static String round2Str(double number, int decimal){
        return String.format("%." + decimal + 'f', number).replace(",", ".");
    }

    public static float round(float number, int decimal){

        float temp = 1;

        if(decimal != 0){
            temp = (float) Math.pow(10, decimal);
        }

        return Math.round(number * temp) / temp;
    }

    public static double round(double number, int decimal){

        double temp = 1;

        if(decimal != 0){
            temp = Math.pow(10, decimal);
        }

        return Math.round(number * temp) / temp;
    }

    public static int modulo(int a, int m){

        return (a >= 0) ? (a % m) : (m - (-a % m));
    }

    public static String getOrderNum(int num){
        int lastNum = num % 10;

        switch (lastNum){
            case 1:
                return num + "st";

            case 2:
                return num + "nd";

            case 3:
                return num + "rd";

            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                return num + "th";

            default:
                return String.valueOf(num);
        }
    }
}
