package com.sourcey.user;

import android.util.Log;

import java.security.MessageDigest;

public class HashPassword {

    public String Hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (Exception e) {
            Log.getStackTraceString(e);
            return null;
        }
    }
}
