package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;
    private ExpenseManagerDbHelper dbHelper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PersistentTransactionDAO(ExpenseManagerDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        transactions = new LinkedList<>();
        loadTransactions();
    }

    public void loadTransactions(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                ExpensesManagerContract.Transaction.TABLE_COLUMN_DATE,
                ExpensesManagerContract.Transaction.TABLE_COLUMN_ACCOUNT_NO,
                ExpensesManagerContract.Transaction.TABLE_COLUMN_ACCOUNT_EXPENSE_TYPE,
                ExpensesManagerContract.Transaction.TABLE_COLUMN_AMOUNT,
        };

        Cursor cursor = db.query(ExpensesManagerContract.Transaction.TABLE_NAME, projection, null, null, null, null, null);

        while(cursor.moveToNext()){
            Date date = new Date();
            try {
                date = dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(ExpensesManagerContract.Transaction.TABLE_COLUMN_DATE)));
            }catch (ParseException e){
                e.printStackTrace();
            }
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(ExpensesManagerContract.Transaction.TABLE_COLUMN_ACCOUNT_NO));
            String type = cursor.getString((cursor.getColumnIndexOrThrow(ExpensesManagerContract.Transaction.TABLE_COLUMN_ACCOUNT_EXPENSE_TYPE)));

            ExpenseType expenseType;
            if (type == "EXPENSE"){
                expenseType = ExpenseType.EXPENSE;
            }
            else {
                expenseType = ExpenseType.INCOME;
            }

            Double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpensesManagerContract.Transaction.TABLE_COLUMN_AMOUNT));

            Transaction transaction= new Transaction(date, accountNo, expenseType, amount);
            transactions.add((transaction));
        }
        cursor.close();
    }

    public void insertTransaction(Transaction transaction){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesManagerContract.Transaction.TABLE_COLUMN_DATE, String.valueOf(transaction.getDate()));
        values.put(ExpensesManagerContract.Transaction.TABLE_COLUMN_ACCOUNT_NO, transaction.getAccountNo());
        values.put(ExpensesManagerContract.Transaction.TABLE_COLUMN_ACCOUNT_EXPENSE_TYPE, transaction.getExpenseType().name());
        values.put(ExpensesManagerContract.Transaction.TABLE_COLUMN_AMOUNT, transaction.getAmount());

        db.insert(ExpensesManagerContract.Transaction.TABLE_NAME, null, values);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        insertTransaction(transaction);
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
