package com.nanosoft.bd.saveme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nanosoft.bd.saveme.activity.Operations;
import com.nanosoft.bd.saveme.phoneBook.Contact;

import java.util.ArrayList;

/**
 * Created by Akbar on 5/8/2016.
 */
public class DatabaseManager {


    Context context;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context){
        dbHelper=new DatabaseHelper(context);
        this.context= context;

    }

    private void open()
    {
        database=dbHelper.getWritableDatabase();

    }

    private void close()
    {
        dbHelper.close();
    }



    //-----------------------------------------*Password*-------------------------------------------------//



    private DatabaseModel databaseModel;


    public boolean addFnf(DatabaseModel databaseModel) {
        this.open();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.COL_FNF_NUMBER, databaseModel.getFnfNumber());
        contentValues.put(DatabaseHelper.COL_FNF_NAME, databaseModel.getFnfName());

        long inserted = database.insert(DatabaseHelper.TABLE_NAME_FNF_NUMBER, null, contentValues);
        database.close();
        this.close();

        if (inserted > 0) {
            return true;

        } else return false;

    }

//    public boolean rememberStatus(int id, boolean status ){
//
//        this.open();
//        ContentValues contentValues = new ContentValues();
//
//        if (status){
//            contentValues.put(DatabaseHelper.COL_HINT, "true" );
//        }else contentValues.put(DatabaseHelper.COL_HINT, "false" );
//
//        int updated = database.update(DatabaseHelper.TABLE_PASSWORD, contentValues, DatabaseHelper.COL_ID + " = " + id, null);
//        this.close();
//        if (updated > 0) {
//            return true;
//        } else return false;
//
//    }


    public DatabaseModel getFnfInfo(int fnfId) {

        this.open();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FNF_NUMBER, new String[]{DatabaseHelper.COL_ID,
                DatabaseHelper.COL_FNF_NUMBER, DatabaseHelper.COL_FNF_NAME}, DatabaseHelper.COL_ID + " = " +fnfId , null, null, null, null);

        cursor.moveToFirst();

        int mId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID));
        String validationDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNF_NUMBER));
        String validationKey = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNF_NAME));

        this.databaseModel = new DatabaseModel(mId, validationDate, validationKey);
        this.close();
        return this.databaseModel;

    }

    public ArrayList<DatabaseModel> getAllInfo() {

        this.open();
        ArrayList<DatabaseModel> fnfList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FNF_NUMBER, null, null, null, null, null, null);

        cursor.moveToFirst();

        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {

                int mId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID));
                String validationDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNF_NUMBER));
                String validationKey = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNF_NAME));

                databaseModel = new DatabaseModel(mId, validationDate); //, validationKey);

                fnfList.add(databaseModel);
                cursor.moveToNext();
            }

        }
        this.close();
        return fnfList;
    }

    public boolean deleteFnf(int fnfId) {
        this.open();
        int deleted = database.delete(DatabaseHelper.TABLE_NAME_FNF_NUMBER, DatabaseHelper.COL_ID + "= " +  fnfId, null);
        this.close();
        if (deleted > 0) {
            return true;
        } else return false;

    }

    public boolean updateFnf(int id, DatabaseModel databaseMobel) {
        this.open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_FNF_NUMBER, databaseMobel.getFnfNumber());
        contentValues.put(DatabaseHelper.COL_FNF_NAME, databaseMobel.getFnfName());

        int updated = database.update(DatabaseHelper.TABLE_NAME_FNF_NUMBER, contentValues, DatabaseHelper.COL_ID + " = " + id, null);
        this.close();
        if (updated > 0) {
            return true;
        } else return false;

    }







    public boolean initializeCheckBoxStatus(Contact contact)
    {
        long inserted = -1;
        this.open();

        int contactSize = Operations.getIntegerSharedPreference(context,"totalContact", 0);

        for (int i =0; i<contactSize; i++) {
            ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseHelper.COL_COMPANY_NAME, modelCompany.getCompanyName());
//        contentValues.put(DatabaseHelper.COL_PHONE_NUMBER, modelCompany.getPhoneNumber());
//        contentValues.put(DatabaseHelper.COL_EMAIL_ADDRESS, modelCompany.getEmailAddress());
//        contentValues.put(DatabaseHelper.COL_COMPANY_DATE, modelCompany.getCompanyDate());
//        contentValues.put(DatabaseHelper.COL_COMPANY_TIME, modelCompany.getCompanyTime());
//        contentValues.put(DatabaseHelper.COL_COMPANY_DAY_NAME, modelCompany.getCompanyDayName());
            contentValues.put(DatabaseHelper.COL_CHECK_BOX_STATUS, contact.getCheckBoxStatus());

            inserted = database.insert(DatabaseHelper.TABLE_NAME_CHECK_INVITATION, null, contentValues);
        }
        this.close();

        if(inserted>0)
        {
            return  true;
        }
        else
        {
            return  false;
        }

    }


    public boolean addCheckBoxStatus(Contact contact)
    {

        this.open();

            ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseHelper.COL_COMPANY_NAME, modelCompany.getCompanyName());
//        contentValues.put(DatabaseHelper.COL_PHONE_NUMBER, modelCompany.getPhoneNumber());
//        contentValues.put(DatabaseHelper.COL_EMAIL_ADDRESS, modelCompany.getEmailAddress());
//        contentValues.put(DatabaseHelper.COL_COMPANY_DATE, modelCompany.getCompanyDate());
//        contentValues.put(DatabaseHelper.COL_COMPANY_TIME, modelCompany.getCompanyTime());
//        contentValues.put(DatabaseHelper.COL_COMPANY_DAY_NAME, modelCompany.getCompanyDayName());
            contentValues.put(DatabaseHelper.COL_CHECK_BOX_STATUS, contact.getCheckBoxStatus());

          long  inserted = database.insert(DatabaseHelper.TABLE_NAME_CHECK_INVITATION, null, contentValues);

        this.close();

        if(inserted>0)
        {
            return  true;
        }
        else
        {
            return  false;
        }

    }



    public boolean updateCheckBoxState(int id,Contact contact)
    {

        this.open();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.COL_CHECK_BOX_STATUS, contact.getCheckBoxStatus());

        int updated = database.update(DatabaseHelper.TABLE_NAME_CHECK_INVITATION, contentValues, DatabaseHelper.COL_CHECK_ID + " = " + id, null);
        this.close();
        if (updated > 0) {
            return true;
        } else return false;

    }


    public Contact getCheckBoxStatus(int id) {

        this.open();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CHECK_INVITATION, new String[]{DatabaseHelper.COL_CHECK_ID, DatabaseHelper.COL_COMPANY_NAME, DatabaseHelper.COL_PHONE_NUMBER, DatabaseHelper.COL_EMAIL_ADDRESS, DatabaseHelper.COL_COMPANY_DATE, DatabaseHelper.COL_COMPANY_TIME, DatabaseHelper.COL_COMPANY_DAY_NAME, DatabaseHelper.COL_CHECK_BOX_STATUS}, DatabaseHelper.COL_CHECK_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();

        int companyId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CHECK_ID));
        String companyName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_NAME));
        String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PHONE_NUMBER));
        String emailAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL_ADDRESS));
        String companyDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DATE));
        String companyTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_TIME));
        String companyDayName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DAY_NAME));
        String checkBoxStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CHECK_BOX_STATUS));

        Contact contact = new Contact(checkBoxStatus);


        this.close();
        return contact;


    }

    public ArrayList<Contact> getAllContactList() {

        ArrayList<Contact> noteList = new ArrayList<>();

        this.open();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CHECK_INVITATION, null, null, null, null, null, null);

        if (cursor!=null && cursor.getCount()>0){


            cursor.moveToFirst();

            for (int i=0; i<cursor.getCount(); i++){
                int noteId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CHECK_ID));
                String companyName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PHONE_NUMBER));
                String emailAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL_ADDRESS));
                String companyDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DATE));
                String companyTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_TIME));
                String companyDayName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DAY_NAME));
                String extraId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CHECK_BOX_STATUS));

                Contact contact = new Contact(noteId,extraId);
                noteList.add(contact);
                cursor.moveToNext();
            }
        }
        database.close();
        this.close();

        return noteList;
    }




/*    //-----------------------------------------Company List-------------------------------------------------//


    public boolean addCompany (ModelCompany modelCompany)
    {
        this.open();

        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseHelper.COL_COMPANY_NAME, modelCompany.getCompanyName());
        contentValues.put(DatabaseHelper.COL_PHONE_NUMBER, modelCompany.getPhoneNumber());
        contentValues.put(DatabaseHelper.COL_EMAIL_ADDRESS, modelCompany.getEmailAddress());
        contentValues.put(DatabaseHelper.COL_COMPANY_DATE, modelCompany.getCompanyDate());
        contentValues.put(DatabaseHelper.COL_COMPANY_TIME, modelCompany.getCompanyTime());
        contentValues.put(DatabaseHelper.COL_COMPANY_DAY_NAME, modelCompany.getCompanyDayName());
        contentValues.put(DatabaseHelper.COL_COMPANY_EXTRA_ID, modelCompany.getCompanyExtraId());

        long inserted=database.insert(DatabaseHelper.TABLE_NAME_COMPANY_NAME,null,contentValues);

        this.close();

        if(inserted>0)
        {
            return  true;
        }
        else
        {
            return  false;
        }

    }


    public ArrayList<ModelCompany> getAllNoteList(String accessStateBit){  //, String noteDatePra, String noteDateOperator) {

        ArrayList<ModelCompany> noteList = new ArrayList<>();

        this.open();

        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME_NOTE + " WHERE " +
                DatabaseHelper.COL_ACCESS_STATE + "='" + accessStateBit + "'";

//                "' AND noteDate "
//                + noteDateOperator + "'" +noteDatePra + "' order by noteDate desc";


        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.getCount()>0){

            // cursor.moveToFirst();

            cursor.moveToLast();

            for (int i=0; i<cursor.getCount(); i++){
                int noteId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_NOTE_ID));
                String noteName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOTE_NAME));
                String noteText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOTE_TEXT));
                String noteDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOTE_DATE));
                String noteTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NOTE_TIME));
                String accessState = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ACCESS_STATE));
                String alarmState = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ALARM_STATE));
                String extraId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EXTRA_ID));

                ModelCompany noteModel = new ModelCompany(noteId,noteName,noteText,noteDate,noteTime,accessState,alarmState,extraId);
                noteList.add(noteModel);
                // cursor.moveToNext();
                cursor.moveToPrevious();
            }
        }
        database.close();
        this.close();
        return noteList;
    }


    public ArrayList<ModelCompany> getAllCompanyList2() {

        ArrayList<ModelCompany> noteList = new ArrayList<>();

        this.open();

//        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME_NOTE + " WHERE " +
//                DatabaseHelper.COL_ACCESS_STATE + "='" + accessStateBit + "' AND dietDate "
//                + noteDateOperator + "'" +noteDatePra + "' order by dietDate desc";

        //Cursor cursor = database.rawQuery(selectQuery, null);

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COMPANY_NAME, null, null, null, null, null, null);

        if (cursor!=null && cursor.getCount()>0){


            cursor.moveToLast();

            for (int i=0; i<cursor.getCount(); i++){
                int noteId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_ID));
                String companyName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PHONE_NUMBER));
                String emailAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL_ADDRESS));
                String companyDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DATE));
                String companyTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_TIME));
                String companyDayName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DAY_NAME));
                String extraId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_EXTRA_ID));

                ModelCompany noteModel = new ModelCompany(noteId,companyName,phoneNumber,emailAddress,companyDate,companyTime,companyDayName,extraId);
                noteList.add(noteModel);
                cursor.moveToPrevious();
            }
        }
        database.close();
        this.close();
        return noteList;
    }

    public ArrayList<ModelCompany> getCompanyListById(int companyID) {

        ArrayList<ModelCompany> noteList = new ArrayList<>();

        this.open();

        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME_COMPANY_NAME + " WHERE " +
                DatabaseHelper.COL_COMPANY_ID + " = '" + companyID + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.getCount()>0){

            // cursor.moveToFirst();

            cursor.moveToLast();

            for (int i=0; i<cursor.getCount(); i++){
                int noteId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_ID));
                String companyName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PHONE_NUMBER));
                String emailAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL_ADDRESS));
                String companyDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DATE));
                String companyTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_TIME));
                String companyDayName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DAY_NAME));
                String extraId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_EXTRA_ID));

                ModelCompany modelCompany = new ModelCompany(noteId,companyName,phoneNumber,emailAddress,companyDate,companyTime,companyDayName,extraId);


                noteList.add(i,modelCompany);
                cursor.moveToPrevious();
                // cursor.moveToNext();
            }
        }
        database.close();
        this.close();
        return noteList;
    }

    public ModelCompany getCompanyInfo(int id) {

        this.open();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COMPANY_NAME, new String[]{DatabaseHelper.COL_COMPANY_ID, DatabaseHelper.COL_COMPANY_NAME, DatabaseHelper.COL_PHONE_NUMBER, DatabaseHelper.COL_EMAIL_ADDRESS, DatabaseHelper.COL_COMPANY_DATE, DatabaseHelper.COL_COMPANY_TIME, DatabaseHelper.COL_COMPANY_DAY_NAME, DatabaseHelper.COL_COMPANY_EXTRA_ID}, DatabaseHelper.COL_COMPANY_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();

        int companyId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_ID));
        String companyName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_NAME));
        String phoneNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PHONE_NUMBER));
        String emailAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL_ADDRESS));
        String companyDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DATE));
        String companyTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_TIME));
        String companyDayName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_DAY_NAME));
        String extraId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_COMPANY_EXTRA_ID));

        ModelCompany modelCompany = new ModelCompany(companyId,companyName,phoneNumber,emailAddress,companyDate,companyTime,companyDayName,extraId);


        this.close();
        return modelCompany;


    }

    public boolean updateCompany(int id, ModelCompany modelCompany){

        this.open();

        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseHelper.COL_COMPANY_NAME, modelCompany.getCompanyName());
        contentValues.put(DatabaseHelper.COL_PHONE_NUMBER, modelCompany.getPhoneNumber());
        contentValues.put(DatabaseHelper.COL_EMAIL_ADDRESS, modelCompany.getEmailAddress());
        contentValues.put(DatabaseHelper.COL_COMPANY_DATE, modelCompany.getCompanyDate());
        contentValues.put(DatabaseHelper.COL_COMPANY_TIME, modelCompany.getCompanyTime());
        contentValues.put(DatabaseHelper.COL_COMPANY_DAY_NAME, modelCompany.getCompanyDayName());
        contentValues.put(DatabaseHelper.COL_COMPANY_EXTRA_ID, modelCompany.getCompanyExtraId());

        int update = database.update(DatabaseHelper.TABLE_NAME_COMPANY_NAME,contentValues,DatabaseHelper.COL_COMPANY_ID+" = "+id,null);
        this.close();

        if (update>0){
            return true;
        }else return false;
    }

    public boolean deleteCompany(int id){

        this.open();
        int delete = database.delete(DatabaseHelper.TABLE_NAME_COMPANY_NAME,DatabaseHelper.COL_COMPANY_ID+" = "+id,null);
        this.close();
        if (delete>0){
            return true;
        }else return false;

    }








    //////////////////////// Product ////////////////////////////////////////////////////////////////////////////////



    public boolean addProduct (ModelProduct modelProduct)
    {
        this.open();

        ContentValues cvProductValue=new ContentValues();
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_NAME,modelProduct.getProductName());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_PRICE,modelProduct.getProductPrice());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_QUANTITY,modelProduct.getProductQuantity());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_AVAILABILITY,modelProduct.getProductAvailability());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DATE,modelProduct.getProductDate());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_TIME,modelProduct.getProductTime());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DAY_NAME,modelProduct.getProductDayName());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE,modelProduct.getProductModifiedDate());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME, modelProduct.getProductModifiedTime());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME,modelProduct.getProductModifiedDayName());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY, modelProduct.getProductForeignKey());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE, modelProduct.getProductCheckBoxState());

        long inserted=database.insert(DatabaseHelper.TABLE_NAME_PRODUCT,null,cvProductValue);

        this.close();

        if(inserted>0)
        {
            return  true;
        }
        else
        {
            return  false;
        }

    }

    public ModelProduct getProductInfo(int id) {

        this.open();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_PRODUCT,
                new String[]{DatabaseHelper.COL_PRODUCT_ID,
                        DatabaseHelper.COL_PRODUCT_NAME,
                        DatabaseHelper.COL_PRODUCT_PRICE,
                        DatabaseHelper.COL_PRODUCT_QUANTITY,
                        DatabaseHelper.COL_PRODUCT_AVAILABILITY,
                        DatabaseHelper.COL_PRODUCT_DATE,
                        DatabaseHelper.COL_PRODUCT_TIME,
                        DatabaseHelper.COL_PRODUCT_DAY_NAME,
                        DatabaseHelper.COL_PRODUCT_MODIFIED_DATE,
                        DatabaseHelper.COL_PRODUCT_MODIFIED_TIME,
                        DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME,
                        DatabaseHelper.COL_PRODUCT_FOREIGN_KEY,
                        DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE},
                DatabaseHelper.COL_PRODUCT_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();

        int prid=cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_ID));
        String productName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_NAME));
        String productPrice =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_PRICE));
        String productQts=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_QUANTITY));
        String productAvailability=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_AVAILABILITY));
        String productDate=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_DATE));
        String productTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_TIME));
        String productDayName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_DAY_NAME));
        String productModifiedDate=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE));
        String productModifiedTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME));
        String productModifiedDayName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME));
        String productForeignKey=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY));
        String checkBoxState=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE));

        ModelProduct modelProduct=new ModelProduct(prid,productName,productPrice,productQts,productAvailability,productDate,productTime,productDayName,productModifiedDate,productModifiedTime,productModifiedDayName,productForeignKey,checkBoxState);

        this.close();
        return modelProduct;


    }


    public ArrayList<ModelProduct> getAllProductList(){

        ArrayList<ModelProduct> profileModels=new ArrayList<>();
        this.open();
        Cursor cursor=database.query(DatabaseHelper.TABLE_NAME_PRODUCT, null, null, null, null, null,null,null);

        if (cursor!=null && cursor.getCount()>0){

            cursor.moveToLast();

            for (int i=0; i<cursor.getCount(); i++){

                int prid=cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_ID));
                String productName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_NAME));
                String productPrice =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_PRICE));
                String productQts=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_QUANTITY));
                String productAvailability=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_AVAILABILITY));
                String productDate=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_DATE));
                String productTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_TIME));
                String productDayName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_DAY_NAME));
                String productModifiedDate=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE));
                String productModifiedTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME));
                String productModifiedDayName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME));
                String productForeignKey=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY));
                String checkBoxState=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE));

                ModelProduct modelProduct=new ModelProduct(prid,productName,productPrice,productQts,productAvailability,productDate,productTime,productDayName,productModifiedDate,productModifiedTime,productModifiedDayName,productForeignKey,checkBoxState);

                profileModels.add(modelProduct);
               cursor.moveToPrevious();
            }
        }
        database.close();
        this.close();
        return profileModels;
    }


    public ArrayList<ModelProduct> getProductListByForeignKey(String foreignKey) {

        ArrayList<ModelProduct> profileModels = new ArrayList<>();

        this.open();

        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME_PRODUCT + " WHERE " +
                DatabaseHelper.COL_PRODUCT_FOREIGN_KEY + "='" + foreignKey + "'";


        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.getCount()>0){

            cursor.moveToLast();

            for (int i=0; i<cursor.getCount(); i++){

                int prid=cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_ID));
                String productName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_NAME));
                String productPrice =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_PRICE));
                String productQts=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_QUANTITY));
                String productAvailability=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_AVAILABILITY));
                String productDate=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_DATE));
                String productTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_TIME));
                String productDayName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_DAY_NAME));
                String productModifiedDate=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE));
                String productModifiedTime=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME));
                String productModifiedDayName=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME));
                String productForeignKey=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY));
                String checkBoxState=cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE));

                ModelProduct modelProduct=new ModelProduct(prid,productName,productPrice,productQts,productAvailability,productDate,productTime,productDayName,productModifiedDate,productModifiedTime,productModifiedDayName,productForeignKey,checkBoxState);

                profileModels.add(modelProduct);
                cursor.moveToPrevious();
            }
        }
        database.close();
        this.close();
        return profileModels;
    }

    public boolean updateProduct (int id, ModelProduct modelProduct)
    {
        this.open();

        ContentValues cvProductValue=new ContentValues();
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_NAME,modelProduct.getProductName());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_PRICE,modelProduct.getProductPrice());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_QUANTITY,modelProduct.getProductQuantity());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_AVAILABILITY,modelProduct.getProductAvailability());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DATE,modelProduct.getProductDate());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_TIME,modelProduct.getProductTime());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DAY_NAME,modelProduct.getProductDayName());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE,modelProduct.getProductModifiedDate());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME, modelProduct.getProductModifiedTime());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME,modelProduct.getProductModifiedDayName());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY, modelProduct.getProductForeignKey());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE, modelProduct.getProductCheckBoxState());

        long updated = database.update(DatabaseHelper.TABLE_NAME_PRODUCT, cvProductValue, DatabaseHelper.COL_PRODUCT_ID + "= " + id, null);
        this.close();

        if (updated > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteProduct (int id)
    {
        this.open();

        int delete = database.delete(DatabaseHelper.TABLE_NAME_PRODUCT,DatabaseHelper.COL_PRODUCT_ID+" = " + id, null);
        this.close();

        if (delete>0){
            return true;
        }else return false;

    }

    public boolean deleteByForeignKey(String foreignKey) {

        this.open();

        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME_PRODUCT + " WHERE " +
                DatabaseHelper.COL_PRODUCT_FOREIGN_KEY + " = '" + foreignKey + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        int delete=9999;
        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                delete = database.delete(DatabaseHelper.TABLE_NAME_PRODUCT, DatabaseHelper.COL_PRODUCT_FOREIGN_KEY + " = " + foreignKey, null);
                cursor.moveToNext();
            }
        }
        this.close();
        if (delete > 0) {
            return true;
        } else return false;

    }

    public boolean updateCheckBoxState (int id, ModelProduct modelProduct)
    {
        this.open();

        ContentValues cvProductValue=new ContentValues();
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_NAME,modelProduct.getProductName());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_PRICE,modelProduct.getProductPrice());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_QUANTITY,modelProduct.getProductQuantity());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_AVAILABILITY,modelProduct.getProductAvailability());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DATE,modelProduct.getProductDate());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_TIME,modelProduct.getProductTime());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DAY_NAME,modelProduct.getProductDayName());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE,modelProduct.getProductModifiedDate());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME, modelProduct.getProductModifiedTime());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME,modelProduct.getProductModifiedDayName());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY, modelProduct.getProductForeignKey());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE, modelProduct.getProductCheckBoxState());

        long updated = database.update(DatabaseHelper.TABLE_NAME_PRODUCT, cvProductValue, DatabaseHelper.COL_PRODUCT_ID + "= " + id, null);
        this.close();

        if (updated > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean addCheckBoxState (ModelProduct modelProduct)
    {
        this.open();

        ContentValues cvProductValue=new ContentValues();
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_NAME,modelProduct.getProductName());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_PRICE,modelProduct.getProductPrice());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_QUANTITY,modelProduct.getProductQuantity());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_AVAILABILITY,modelProduct.getProductAvailability());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DATE,modelProduct.getProductDate());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_TIME,modelProduct.getProductTime());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_DAY_NAME,modelProduct.getProductDayName());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DATE,modelProduct.getProductModifiedDate());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_TIME, modelProduct.getProductModifiedTime());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_MODIFIED_DAY_NAME,modelProduct.getProductModifiedDayName());
//        cvProductValue.put(DatabaseHelper.COL_PRODUCT_FOREIGN_KEY, modelProduct.getProductForeignKey());
        cvProductValue.put(DatabaseHelper.COL_PRODUCT_CHECK_BOX_STATE, modelProduct.getProductCheckBoxState());

        long inserted=database.insert(DatabaseHelper.TABLE_NAME_PRODUCT,null,cvProductValue);

        this.close();

        if(inserted>0)
        {
            return  true;
        }
        else
        {
            return  false;
        }

    }*/

}
