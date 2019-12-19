package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final Map<String, Account> accounts;
    private ExpenseManagerDbHelper dbHelper;

    public PersistentAccountDAO(ExpenseManagerDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.accounts = new HashMap<>();
    }

    public void loadTransactions(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                ExpensesManagerContract.Account.TABLE_COLUMN_ACCOUNT_NAME,
                ExpensesManagerContract.Account.TABLE_COLUMN_BANK_NAME,
                ExpensesManagerContract.Account.TABLE_COLUMN_ACCOUNT_HOLDER_NAME,
                ExpensesManagerContract.Account.TABLE_COLUMN_BALANCE,
        };

        Cursor cursor = db.query(ExpensesManagerContract.Account.TABLE_NAME, projection, null, null, null, null, null);

        while(cursor.moveToNext()){

            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(ExpensesManagerContract.Account.TABLE_COLUMN_ACCOUNT_NAME));
            String bankName = cursor.getString((cursor.getColumnIndexOrThrow(ExpensesManagerContract.Account.TABLE_COLUMN_BANK_NAME)));
            String accountHolderName = cursor.getString((cursor.getColumnIndexOrThrow(ExpensesManagerContract.Account.TABLE_COLUMN_ACCOUNT_HOLDER_NAME)));
            double balance = cursor.getDouble((cursor.getColumnIndexOrThrow(ExpensesManagerContract.Account.TABLE_COLUMN_BALANCE)));

            Account account= new Account(accountNo, bankName, accountHolderName, balance);
            accounts.put(accountNo, account);
        }
        cursor.close();
    }

    public void insertAccount(Account account){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesManagerContract.Account.TABLE_COLUMN_ACCOUNT_NAME, account.getAccountNo());
        values.put(ExpensesManagerContract.Account.TABLE_COLUMN_BANK_NAME, account.getBankName());
        values.put(ExpensesManagerContract.Account.TABLE_COLUMN_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(ExpensesManagerContract.Account.TABLE_COLUMN_BALANCE, account.getBalance());

        db.insert(ExpensesManagerContract.Account.TABLE_NAME, null, values);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        insertAccount(account);
        accounts.put(account.getAccountNo(), account);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);
    }

}
