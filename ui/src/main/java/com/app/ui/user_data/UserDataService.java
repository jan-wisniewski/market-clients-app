package com.app.ui.user_data;

import com.app.ui.exceptions.UserDataException;

import java.util.Scanner;

public final class UserDataService {
    private UserDataService(){}

    private final static Scanner SCANNER = new Scanner(System.in);

    public static int getInteger (String message){
        System.out.println(message);
        String value = SCANNER.nextLine();
        if (!value.matches("\\d+")){
            throw new UserDataException("Incorrect value");
        }
        return Integer.parseInt(value);
    }

}
