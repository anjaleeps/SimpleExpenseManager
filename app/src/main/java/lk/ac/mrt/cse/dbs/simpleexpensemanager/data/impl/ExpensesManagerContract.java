package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.provider.BaseColumns;

public final class ExpensesManagerContract {

    private ExpensesManagerContract(){}

    public static class Account implements BaseColumns{
        public static final String TABLE_NAME = "accounts";
        public static final String TABLE_COLUMN_ACCOUNT_NAME = "accountNo";
        public static final String TABLE_COLUMN_BANK_NAME = "bankName";
        public static final String TABLE_COLUMN_ACCOUNT_HOLDER_NAME = "accountHolderName";
        public static final String TABLE_COLUMN_BALANCE = "balance";
    }

    public static class Transaction implements BaseColumns{
        public static final String TABLE_NAME = "transactions";
        public static final String TABLE_COLUMN_DATE = "date";
        public static final String TABLE_COLUMN_ACCOUNT_NO = "accountNo";
        public static final String TABLE_COLUMN_ACCOUNT_EXPENSE_TYPE = "expenseType";
        public static final String TABLE_COLUMN_AMOUNT = "amount";
    }
}
