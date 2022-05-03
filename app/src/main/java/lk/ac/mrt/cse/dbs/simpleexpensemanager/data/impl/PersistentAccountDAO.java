package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.service.DBService;

public class PersistentAccountDAO implements AccountDAO{
    
    private Context context;

    public PersistentAccountDAO(@Nullable Context context) {
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DBService.TABLE_NAME1+";" , null);

        return cursor.getCount()>0 ? getAccountNumberListPvt(cursor) : new ArrayList<String>();

    }

    private ArrayList<String> getAccountNumberListPvt(Cursor cursor){
        ArrayList<String> accountsNumberList = new ArrayList<>();
        while (cursor.moveToNext()){
            String acc_no = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_NO));
            accountsNumberList.add(acc_no);
        }
        return accountsNumberList;
    }

    @Override
    public List<Account> getAccountsList() {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DBService.TABLE_NAME1+";" , null);

        return cursor.getCount()>0 ? getAccountsListPvt(cursor) : new ArrayList<Account>();

    }

    private ArrayList<Account> getAccountsListPvt(Cursor cursor){
        ArrayList<Account> accounts = new ArrayList<>();
        while (cursor.moveToNext()){
            String acc_no = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_NO));
            String bank_name = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_BANK_NAME));
            String acc_holder = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndex(DBService.COLUMN_ACC_BALANCE));

            accounts.add(new Account(acc_no , bank_name , acc_holder , balance));
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+DBService.TABLE_NAME1+" WHERE "+DBService.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        return cursor.getCount()>0 ? getAccountPvt(cursor) : throwAccountExceptionPvt(accountNo);

    }

    private Account throwAccountExceptionPvt(String accountNo) throws InvalidAccountException {
        throw new InvalidAccountException(accountNo+" is a invalid account number.");
     }

    private Account getAccountPvt(Cursor cursor){
        Account account = null;
        while (cursor.moveToNext()){
            String acc_no = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_NO));
            String bank_name = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_BANK_NAME));
            String acc_holder = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndex(DBService.COLUMN_ACC_BALANCE));

            account = new Account(acc_no , bank_name , acc_holder , balance);
            break;
        }
        return account;
    }


    @Override
    public void addAccount(Account account) {

        try{
            getAccount(account.getAccountNo());
        } catch(InvalidAccountException e){
            DBService dbService = DBService.getInstanceDB(context);
            SQLiteDatabase DB = dbService.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("acc_no", account.getAccountNo());
            contentValues.put("bank_name", account.getBankName());
            contentValues.put("acc_holder", account.getAccountHolderName());
            contentValues.put("balance", account.getBalance());

            DB.insert(DBService.TABLE_NAME1 , null , contentValues);
        }

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+DBService.TABLE_NAME1+" WHERE "+DBService.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        if(cursor.getCount()>0){
            DB.delete(DBService.TABLE_NAME1 , "acc_no=?" , new String[]{accountNo});
        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = DB.rawQuery("SELECT * FROM "+DBService.TABLE_NAME1+" WHERE "+DBService.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        if(cursor.getCount()>0){
            double pre_balance = 0;
            while (cursor.moveToNext()){
                pre_balance = cursor.getDouble(cursor.getColumnIndex(DBService.COLUMN_ACC_BALANCE));
                break;
            }
            double new_balance = -1;
            switch (expenseType) {
                case EXPENSE:
                    new_balance = pre_balance - amount;
                    break;
                case INCOME:
                    new_balance = pre_balance + amount;
                    break;
            }

            contentValues.put("balance" , new_balance);
            DB.update(DBService.TABLE_NAME1 , contentValues , "acc_no=?" , new String[]{accountNo});

        }else {
            throw new InvalidAccountException(accountNo+" is a invalid account number.");
        }

    }

}
