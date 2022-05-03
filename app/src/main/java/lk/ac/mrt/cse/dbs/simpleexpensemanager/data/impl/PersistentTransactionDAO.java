package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.service.DBService;

public class PersistentTransactionDAO implements TransactionDAO{

    private Context context;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PersistentTransactionDAO(@Nullable Context context) {
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("acc_no", accountNo);

        contentValues.put(DBService.COLUMN_DATE,  dateFormat.format(date));
        contentValues.put(DBService.COLUMN_TYPE, String.valueOf(expenseType));
        contentValues.put(DBService.COLUMN_AMOUNT, amount);

        DB.insert(DBService.TABLE_NAME2 , null , contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DBService.TABLE_NAME2+";" , null);

        return cursor.getCount()>0 ? getAllTransactionLogsPvt(cursor) : new ArrayList<Transaction>();

    }

    private ArrayList<Transaction> getAllTransactionLogsPvt(Cursor cursor){
        ArrayList<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()){
            String acc_no = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_NO));
            String dateStr = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_DATE));
            String type = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndex(DBService.COLUMN_AMOUNT));

            ExpenseType expenseType = null;
            expenseType = ExpenseType.EXPENSE.name().equals(type) ? ExpenseType.EXPENSE : ExpenseType.INCOME;

            try{
                Date date = dateFormat.parse(dateStr);
                transactions.add(new Transaction(date , acc_no , expenseType , amount));
            } catch (ParseException e){
                e.printStackTrace();
            }

        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        DBService dbService = DBService.getInstanceDB(context);
        SQLiteDatabase DB = dbService.getWritableDatabase();
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ DBService.TABLE_NAME2+ " LIMIT "+limit+";" , null);

        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String acc_no = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_NO));
                String dateStr = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndex(DBService.COLUMN_AMOUNT));

                ExpenseType expenseType = ExpenseType.valueOf(type);

//                ExpenseType.valueOf()
//
//                if(ExpenseType.EXPENSE.name().equals(type)){
//                    expenseType = ExpenseType.EXPENSE;
//                }else expenseType = ExpenseType.INCOME;

                try{
                    Date date = dateFormat.parse(dateStr);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
        }else {
            cursor = DB.rawQuery("SELECT * FROM "+ DBService.TABLE_NAME2+";" , null);
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    String acc_no = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_ACC_NO));
                    String dateStr = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_DATE));
                    String type = cursor.getString(cursor.getColumnIndex(DBService.COLUMN_TYPE));
                    double amount = cursor.getDouble(cursor.getColumnIndex(DBService.COLUMN_AMOUNT));

                    ExpenseType expenseType = null;
                    expenseType = ExpenseType.EXPENSE.name().equals(type) ? ExpenseType.EXPENSE : ExpenseType.INCOME;

                    try{
                        Date date = dateFormat.parse(dateStr);
                        transactions.add(new Transaction(date , acc_no , expenseType , amount));
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                }

            }
        }
        return transactions;

    }


}
