package com.tss.shorty.util;

public class Validator
{
    public static final String PHONE_REGEX = "^[0-9]{10}$";

    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,128}$";
}
