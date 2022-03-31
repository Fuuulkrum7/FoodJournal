package com.example.journal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import android.util.Base64;

public class UserProfile implements Serializable {
    private String name;
    private String surname;
    private String login;
    private int id;
    private String passwordHash;
    private boolean shareDishes;
    private long saltSeed;

    public static boolean checkConnection(){
        //Здесь нужна проверка на онлайн-режим
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public int getId() {
        return id;
    }

    public boolean isShareDishes() {
        return shareDishes;
    }

    public void setShareDishes(boolean shareDishes) {
        this.shareDishes = shareDishes;
    }

    public boolean changePassword(String old, String newPassword){
        String testOld = encryptPassword(old, saltSeed);
        if (testOld == passwordHash){
            saltSeed = getSeedForEncryption();
            passwordHash = encryptPassword(newPassword, saltSeed);
            if (checkConnection()) {
                //Здесь фиксируем изменение пароля.
            }
            return true;
        }
        else return false;
    }

    public boolean updateProfile(){
        if (checkConnection()){
            //Обновляем БД.
            return true;
        }
        return false;
    }

    public UserProfile(String login, String password) {
        this.login = login;
        saltSeed = getSeedForEncryption();
        passwordHash = encryptPassword(password, saltSeed);
    }

    public static UserProfile tryLogin(String name, String password){
        //Пытаемся подключиться к БД.
        //Дальше делаем выборку только логина и хеша пароля. Если совпадут, то делаем полную выборку
        return null;
    }

    public static UserProfile registerNew(String name, String password, boolean onlineMode){
        if (!onlineMode || !checkConnection())
        {
            return new UserProfile(name, password);
        }
        return null;
    }

    protected static UserProfile getUserProfile(String name, boolean localSearch)
    {
        return null;
    }

    public static String encryptPassword(String password, long seed){
        byte[] seedArray = new byte[8];
        for (int i = 0; i < 8; i++){
            seedArray[i] = (byte)((seed >> i * 8) & 0xff);
        }
        SecureRandom gen = new SecureRandom();
        byte[] salt = new byte[16];
        gen.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        String output = Base64.encodeToString(hash, Base64.DEFAULT);
        return output;
    }

    public static long getSeedForEncryption(){
        SecureRandom gen = new SecureRandom();
        byte[] seed = gen.generateSeed(8);
        long res = 0;
        for (int i = 0; i < 8; i++){
            res = (res << 8) + (seed[i] & 0xff);
        }
        return res;
    }

    public static void saveToFile(String path, UserProfile profile) throws IOException {
        FileOutputStream fileOutput = null;
        ObjectOutputStream objectOutput = null;
        try {
            fileOutput = new FileOutputStream(path);
            objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(profile);
            objectOutput.flush();
        }
        finally {
            objectOutput.close();
        }
    }

    public static UserProfile loadFromFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream inputStream = new FileInputStream(path);
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        UserProfile profile = (UserProfile) objectInput.readObject();
        return profile;
    }
}
