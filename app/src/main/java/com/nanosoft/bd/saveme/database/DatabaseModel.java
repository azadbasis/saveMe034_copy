package com.nanosoft.bd.saveme.database;

/**
 * Created by Akbar on 13-Oct-16.
 */

public class DatabaseModel {

/*
    private int companyId;
    private String companyName;
    private String phoneNumber;
    private String emailAddress;
    private String companyDate;
    private String companyTime;
    private String companyDayName;
    private String companyExtraId;

    private int imageId = R.drawable.background_splash;







    private int productId;

    private String productName;
    private String productPrice;
    private String productQuantity;
    private String productAvailability;
    private String productDate;
    private String productTime;
    private String productDayName;
    private String productModifiedDate;
    private String productModifiedTime;
    private String productModifiedDayName;
    private String productForeignKey;                  //Same Company ID
    private String productCheckBoxState;*/



    private  int fnfNumberId;
    private String fnfNumber;
    private String fnfName;


    public DatabaseModel(int fnfNumberId, String fnfNumber, String fnfName) {
        this.fnfNumberId = fnfNumberId;
        this.fnfNumber = fnfNumber;
        this.fnfName = fnfName;
    }


    public DatabaseModel(int fnfNumberId, String fnfNumber) {
        this.fnfNumberId = fnfNumberId;
        this.fnfNumber = fnfNumber;
    }

    public DatabaseModel(String fnfNumber) {
        this.fnfNumber = fnfNumber;
    }

    public String getFnfNumber() {
        return fnfNumber;
    }

    public int getFnfNumberId() {
        return fnfNumberId;
    }

    public String getFnfName() {
        return fnfName;
    }

}



