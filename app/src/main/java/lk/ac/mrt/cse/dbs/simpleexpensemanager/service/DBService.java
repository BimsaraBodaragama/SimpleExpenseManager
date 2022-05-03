package lk.ac.mrt.cse.dbs.simpleexpensemanager.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBService extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "190093T.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME1 = "account";
    public static final String TABLE_NAME2 = "transaction_";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ACC_NO = "acc_no";
    public static final String COLUMN_BANK_NAME = "bank_name";
    public static final String COLUMN_ACC_HOLDER = "acc_holder";
    public static final String COLUMN_ACC_BALANCE = "balance";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "expenseType";
    public static final String COLUMN_AMOUNT = "amount";

    private static DBService db;


    private DBService( Context context) {
        super(context , DATABASE_NAME , null  , DATABASE_VERSION);
    }

    public static DBService getInstanceDB(Context context){
        if(db==null){
            db = new DBService(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE " + TABLE_NAME1 +
                " ("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_ACC_NO + " TEXT UNIQUE, "+
                COLUMN_BANK_NAME + " TEXT, " +
                COLUMN_ACC_HOLDER + " TEXT, "+
                COLUMN_ACC_BALANCE + " REAL);";

        String sql2 = "CREATE TABLE " + TABLE_NAME2 +
                " ("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_ACC_NO + " TEXT, "+
                COLUMN_DATE + " DATETIME, " +
                COLUMN_TYPE + " TEXT, "+
                COLUMN_AMOUNT + " REAL);";

        db.execSQL(sql1);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql1 = "DROP TABLE IF EXISTS " + TABLE_NAME1+";";
        String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME2+";";

        db.execSQL(sql1);
        db.execSQL(sql2);
        onCreate(db);
    }
    
}
