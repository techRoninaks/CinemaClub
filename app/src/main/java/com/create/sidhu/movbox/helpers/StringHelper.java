package com.create.sidhu.movbox.helpers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * String formatting helper
 */

public class StringHelper {
    /***
     * Formats Integers. Eg: 20,000 to 20k. 43,744 to 43.74k
     * @param count - The integer to format
     * @return - Returns the formatted string
     */
    public static String formatTextCount(int count) throws Exception{
        String formattedCount = "";
        if(count >= 10000){
            if(count % 1000 == 0){
                formattedCount = "" + count/1000 + "k";
            }else {
                double temp = ((double) count) / 1000.0;
                formattedCount = "" + (Math.round(temp * 100.0) % 100 == 0 ? Math.round(temp) : Math.round(temp * 100.0)/ 100.0) + "k";
            }
        }
        else
            formattedCount = "" + count;
        return formattedCount;
    }

    /***
     * Converts string to Sentence case
     * @param s- String to be converted
     * @return Sentence cased string
     */
    public static String toSentenceCase(String s) throws Exception{
        if(s != null && !s.isEmpty()) {
            int length = s.length();
            String temp = "";
            int position = 0;
            for(int i = 0; i < length; i++){
                if(s.charAt(i) == '.'){
                    temp = temp.concat(s.substring(position, position + 1).toUpperCase() + s.substring(position + 1, i + 1));
                    position = i + 1;
                }
            }
            if(position < length)
                temp = temp.concat(s.substring(position, position + 1).toUpperCase() + (position >= length - 1 ? "" : s.substring(position + 1)));
            return temp;
        }
        else
            return "";
    }

    /***
     * Converts string to Title case
     * @param s- String to be converted
     * @return Title cased string
     */
    public static String toTitleCase(String s) throws Exception{
        if(s != null && !s.isEmpty()) {
            int length = s.length();
            String str = "";
            String strTemp = "";
            for (int i = 0; i < length; i++) {
                char ch = s.charAt(i);
                if (ch == ' ') {
                    strTemp += ch;
                    str = str.concat(toSentenceCase(strTemp));
                    strTemp = "";
                } else {
                    strTemp += ch;
                }
            }
            str = str.concat(toSentenceCase(strTemp));
            return str;
        }else
            return "";
    }

    /***
     * Changes toolbar font
     * @param toolbar
     * @param context
     * @throws Exception
     */
    public static void changeToolbarFont(Toolbar toolbar, Activity context) throws Exception{
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }


    public static void applyFont(TextView tv, Activity context) throws Exception{
        tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Semibold.otf"));
    }

    /***
     * Format date to specified format
     * @param datetime
     * @param format
     * @return
     */
    public static Date getDate(String datetime, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = formatter.parse(datetime);
            return date;
        }catch (Exception e){

        }
        try {
            return formatter.parse(formatter.format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {

        }
        return new Date();
    }

    /***
     * Round of floating point to specified precision points
     * @param number
     * @param precision
     * @return rounded float
     */
    public static float roundFloat(float number, int precision){
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(number * scale) / scale;
    }

    /***
     * Encrypt password using new Salt
     * @param password
     * @return encrypted password String
     * @throws Exception
     */
    public static String encryptPassword(String password) throws Exception{
        byte[] salt = getSalt();
        String encryptedPassword = encryptPassword(password, salt);
        return encryptedPassword.concat("!~" + convertSaltToString(salt));
    }

    /***
     * Encrypt password using known salt
     * @param password
     * @param salt
     * @return encrypted password String
     * @throws Exception
     */
    public static String encryptPassword(String password, byte[] salt) throws Exception{
        String encryptedPassword = generateStrongPassword(password, salt);
        return encryptedPassword;
    };

    /***
     * Converts exception stacktrace to String object
     * @param e- Exception object
     * @return stacktrace as a string
     */
    public static String convertStackTrace(Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
            return sw.toString();
    }

    /***
     * Generates secure Salt
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static String convertSaltToString(byte[] salt){
        int size = salt.length;
        String converted = "";
        for(int i = 0; i < size; i++){
            if(i == 0)
                converted = converted.concat("" + salt[i]);
            else
                converted = converted.concat("!@" + salt[i]);
        }
        return converted;
    }

    public static byte[] convertSaltToByte(String saltString){
        byte[] salt = new byte[16];
        String saltStringArray[] = saltString.split("!@");
        for(int i = 0; i < 16; i++){
            salt[i] = Byte.parseByte(saltStringArray[i]);
        }
        return salt;
    }

    private static String generateStrongPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(salt) + ":" + toHex(hash);
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
}
