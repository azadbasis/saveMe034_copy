package com.nanosoft.bd.saveme.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Akbar on 5/8/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {



    //****************************** Database Name & Version***************************************************//

    public static final String DATABASE_NAME = "product_manager.db";
    public static final int DATABASE_VERSION = 1;





    //****************************** Table Name***************************************************//

    public static  final String TABLE_NAME_FNF_NUMBER = "fnfNumber";
    public static final String TABLE_NAME_CHECK_INVITATION = "checkInvitation";
  //  public static  final String TABLE_NAME_PRODUCT = "product_name";




    //****************************** Context Pass***************************************************//

    private Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }


    //****************************** Database OnCreate Override Method (Table String Here)***************************//

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FNF_NUMBER);
        db.execSQL(CREATE_TABLE_CHECK_INVITATION);
       // db.execSQL(CREATE_TABLE_PRODUCT_NAME);*/
    }





    //****************************** Database OnUpgrade Override Method (Table Name Here)***************************//


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME_FNF_NUMBER);
       /* db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME_COMPANY_NAME);
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME_PRODUCT);*/
    }







    //****************************** Validation Table ***************************************************//

    static final String COL_ID="fnfId";
    static final String COL_FNF_NUMBER="fnfNumber";
    static final String COL_FNF_NAME="fnfName";


    static final String CREATE_TABLE_FNF_NUMBER=" CREATE TABLE " + TABLE_NAME_FNF_NUMBER + " ( " +
            COL_ID +" INTEGER PRIMARY KEY," +
            COL_FNF_NUMBER +" TEXT," +
            COL_FNF_NAME +" TEXT )";




    //********************************* Company Table ************************************************//

   public static final String COL_CHECK_ID="checkId";
    public static final String COL_COMPANY_NAME="companyName";
    public static final String COL_PHONE_NUMBER="phoneNumber";
    public static final String COL_EMAIL_ADDRESS="emailAddress";
    public static final String COL_COMPANY_DATE="companyDate";
    public static final String COL_COMPANY_TIME="companyTime";
    public static final String COL_COMPANY_DAY_NAME="companyDayName";
    public static final String COL_CHECK_BOX_STATUS="companyExtraId";



    static final String CREATE_TABLE_CHECK_INVITATION="CREATE TABLE "+TABLE_NAME_CHECK_INVITATION +" ( " +
            COL_CHECK_ID+" INTEGER PRIMARY KEY,"+
            COL_COMPANY_NAME+" TEXT,"+
            COL_PHONE_NUMBER+" TEXT,"+
            COL_EMAIL_ADDRESS+" TEXT,"+
            COL_COMPANY_DATE+" TEXT,"+
            COL_COMPANY_TIME+" TEXT,"+
            COL_COMPANY_DAY_NAME+" TEXT,"+
            COL_CHECK_BOX_STATUS+" TEXT)";


/*

    /*//********************************* Product Table ************************************************//*/


    public static final String COL_PRODUCT_ID="productId";
    public static final String COL_PRODUCT_NAME="productName";
    public static final String COL_PRODUCT_PRICE="productPrice";
    public static final String COL_PRODUCT_QUANTITY="productQuantity";
    public static final String COL_PRODUCT_AVAILABILITY="productAvailability";
    public static final String COL_PRODUCT_TIME="productTime";
    public static final String COL_PRODUCT_DATE="productDate";
    public static final String COL_PRODUCT_DAY_NAME="productDayName";
    public static final String COL_PRODUCT_MODIFIED_DATE="productModifiedDate";
    public static final String COL_PRODUCT_MODIFIED_TIME="productModifiedTime";
    public static final String COL_PRODUCT_MODIFIED_DAY_NAME="productModifiedDayName";
    public static final String COL_PRODUCT_FOREIGN_KEY ="productForeignKey";                  //Same Company ID
    public static final String COL_PRODUCT_CHECK_BOX_STATE ="productCheckBoxState";



    static final String CREATE_TABLE_PRODUCT_NAME="CREATE TABLE "+TABLE_NAME_PRODUCT +" ( " +
            COL_PRODUCT_ID+" INTEGER PRIMARY KEY,"+
            COL_PRODUCT_NAME+" TEXT,"+
            COL_PRODUCT_PRICE+" TEXT,"+
            COL_PRODUCT_QUANTITY+" TEXT,"+
            COL_PRODUCT_AVAILABILITY+" TEXT,"+
            COL_PRODUCT_TIME+" TEXT,"+
            COL_PRODUCT_DATE+" TEXT,"+
            COL_PRODUCT_DAY_NAME+" TEXT,"+
            COL_PRODUCT_MODIFIED_DATE+" TEXT,"+
            COL_PRODUCT_MODIFIED_TIME+" TEXT,"+
             COL_PRODUCT_MODIFIED_DAY_NAME+" TEXT,"+
            COL_PRODUCT_FOREIGN_KEY+" TEXT,"+
            COL_PRODUCT_CHECK_BOX_STATE+" TEXT)";

*/


}
