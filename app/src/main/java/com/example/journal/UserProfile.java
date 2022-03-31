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
import java.util.Random;

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
        if (testOld.equals(passwordHash)){
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
        }//Как-то регаемся в онлайне.
        return null;
    }

    protected static UserProfile getUserProfile(String name)
    {
        //Здесь пытаемся загрузить данные пользователя по имени.
        // Именно для поиска (вдруг понадобится потом).
        return null;
    }
    //Пара методов для паролей. Пароль состоит из 2 частей: хеш пароля и сид для генерации "полезной нагрузки", или "соли"
    //"Соль" генерируется рандомайзером по определённому сиду. Сид заранее тоже генерируется, так что нагрузка не зависит от пользователя.
    //Хеш создаётся из пароля через генератор хеш-функций.
//Тут относительно всё легко, шифруем пароль по SHA-512 с указанным сидом для генерации данных в нагрузку.
    public static String encryptPassword(String password, long seed){
        //Этап 1. Создаём данные для усложнения шифрования.
        Random gen = new Random(seed);
        byte[] salt = new byte[16];
        gen.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(salt);
        //Этап 2. Считаем хеш пароля.
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        String output = Base64.encodeToString(hash, Base64.DEFAULT);
        return output;
    }
//Генерируем новый сид для пароля.
    public static long getSeedForEncryption(){
        SecureRandom gen = new SecureRandom();
        byte[] seed = gen.generateSeed(8);
        long res = 0;
        for (int i = 0; i < 8; i++){
            res = (res << 8) + (seed[i] & 0xff);
        }
        return res;
    }
//Этот метод сохраняет данные пользователя в файл по адресу.
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
//А этот загружает их из файла.
    public static UserProfile loadFromFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream inputStream = new FileInputStream(path);
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        UserProfile profile = (UserProfile) objectInput.readObject();
        return profile;
    }
}
