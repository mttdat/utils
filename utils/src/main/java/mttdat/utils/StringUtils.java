package mttdat.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class StringUtils {
    public static String capitalizeFirstLetter(@NonNull String input) {

        if(TextUtils.isEmpty(input)){
            return "";
        }

        String[] words = input.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if(word.equals("")){
                continue;
            }

            if (i > 0 && word.length() > 0) {
                builder.append(" ");
            }

            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }

    public static String capitalizeFirstLetterInSentence(@NonNull String input) {

        if(TextUtils.isEmpty(input)){
            return "";
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String removePunctuationCharacters(String input){
        return input.replaceAll("\\p{P}", "");
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean matchRegex(String s, String pattern){
        return Pattern.matches(pattern, s);
    }

    public static boolean notContainAlphabet(String s){
        return matchRegex(s,"[^a-zA-Z]+");
    }

    public static boolean isNumber(Character c){
        return Character.isDigit(c);
    }

    public static ArrayList<Integer> findString(String string, String subStr){
        int lastIndex = 0;
        ArrayList<Integer> result = new ArrayList<>();

        while(lastIndex != -1) {

            lastIndex = string.indexOf(subStr,lastIndex);

            if(lastIndex != -1){
                result.add(lastIndex);
                lastIndex += 1;
            }
        }

        return result;
    }

    public static String[] splitEveryCharacter(String wholeString){

        if(TextUtils.isEmpty(wholeString)){
            return new String[]{};
        }

        return wholeString.split("(?!^)");
    }
}
