package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.ExpensesManagerContract.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.ExpensesManagerContract.Account;


public class ExpenseManagerDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ExpenseManager.db";
    private static final String SQL_CREATE_ACCOUNTS =
            "CREATE TABLE "+ Account.TABLE_NAME +"(" +
                    Account.TABLE_COLUMN_ACCOUNT_NAME + " string PRIMARY KEY, " +
                    Account.TABLE_COLUMN_BANK_NAME + " string not null, " +
                    Account.TABLE_COLUMN_ACCOUNT_HOLDER_NAME + " string not null, " +
                    Account.TABLE_COLUMN_BALANCE + " decimal(8, 2) )";

    private static final String SQL_CREATE_TRANSACTIONS =
            "CREATE TABLE " + Transaction.TABLE_NAME + "(" +
                    Transaction.TABLE_COLUMN_DATE + " date not null, " +
                    Transaction.TABLE_COLUMN_ACCOUNT_NO + " string not null, " +
                    Transaction.TABLE_COLUMN_ACCOUNT_EXPENSE_TYPE + " string not null, " +
                    Transaction.TABLE_COLUMN_AMOUNT + " decimal(8, 2), " +
                    "FOREIGN KEY (accountNo) REFERENCES "+ Account.TABLE_NAME + "("+ Account.TABLE_COLUMN_ACCOUNT_NAME+ "))";

    private static final String SQL_DELETE_ACCOUNTS =
            "DROP TABLE IF EXISTS " + Account.TABLE_NAME;

    private static final String SQL_DELETE_TRANSACTIONS =
            "DROP TABLE IF EXISTS " + Transaction.TABLE_NAME;

    public ExpenseManagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNTS);
        db.execSQL(SQL_CREATE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ACCOUNTS);
        db.execSQL(SQL_DELETE_TRANSACTIONS);
        onCreate(db);
    }
}
