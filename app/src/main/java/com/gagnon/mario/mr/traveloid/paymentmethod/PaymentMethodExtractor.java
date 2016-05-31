package com.gagnon.mario.mr.traveloid.paymentmethod;

import android.content.Context;

import com.gagnon.mario.mr.traveloid.R;
import com.gagnon.mario.mr.traveloid.core.Account;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapter;
import com.gagnon.mario.mr.traveloid.core.database.DbAdapterImpl;
import com.gagnon.mario.mr.traveloid.pettycash.PettyCash;
import com.gagnon.mario.mr.traveloid.trip.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mario on 8/20/2015.
 */
public class PaymentMethodExtractor {
    public static List<Account> extract(Trip trip, Context context) throws Exception {


        List<Account> accounts = new ArrayList<>();
        DbAdapter dbAdapter = new DbAdapterImpl(context);

        final List<PettyCash> pettyCashList = dbAdapter.pettyCashFetchForTrip(trip.getId());

        for (PettyCash pettyCash : pettyCashList) {
            accounts.add(new Account(pettyCash.getName(), pettyCash.getCurrency(), pettyCash.getExchangeRate(), true));
        }

        String[] paymentMethods = context.getResources().getStringArray(R.array.payment_methods_array);
        CharSequence defaultPaymentMethod = context.getResources().getText(
                R.string.default_payment_method);

        Account account;
        for (String item : paymentMethods) {

            account = new Account(item, trip.getCurrency(), 1.0);
            if (item.equals(defaultPaymentMethod)) {
                account.setIsDefault(true);
            }
            accounts.add(account);
        }

        if (accounts.size() > 0) {
            Collections.sort(accounts, new Comparator<Account>() {

                        @Override
                        public int compare(Account o1, Account o2) {

                            String cmp1 = String.format("%s%s", o1.getIsPettyCash() ? "1" : "2", o1.getAccountName());
                            String cmp2 = String.format("%s%s", o2.getIsPettyCash() ? "1" : "2", o2.getAccountName());

                            return cmp1.compareToIgnoreCase(cmp2.toString());
                        }
                    }
            );
        }

        return accounts;

    }
}
