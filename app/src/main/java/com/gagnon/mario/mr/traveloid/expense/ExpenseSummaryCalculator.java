package com.gagnon.mario.mr.traveloid.expense;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.gagnon.mario.mr.traveloid.core.DatePart;
import com.gagnon.mario.mr.traveloid.core.Tools;

public class ExpenseSummaryCalculator {

	public static List<ExpenseSummary> calculate(DatePart currentDate,
			SortedSet<String> travelerSet, List<Expense> expenseList)
			throws ParseException {

		// Setup map entry for each traveler
		Map<String, ExpenseSummary> expenseSummaryMap = new HashMap<>();
		for (String traveler : travelerSet) {
			expenseSummaryMap.put(traveler, new ExpenseSummary(traveler, 0.0,
					0.0));
		}

		for (Expense expense : expenseList) {

			for (String traveler : travelerSet) {

				ExpenseSummary expenseSummary = expenseSummaryMap
						.get(traveler);

				if (null != expenseSummary) {

					double amount = expenseSummary.getTotalExpense()
							+ (expense.getAmount() * expense.getExchangeRate())
							/ expense.getTravelerSet().size();

					if (expense.getTravelerSet().contains(traveler)) {
						expenseSummary.setTotalExpense(Tools
								.roundTwoDecimals(amount));

						DatePart dp = expense.getDatePart();

						if (dp.equals(currentDate)) {

							amount = expenseSummary.getTodayExpense()
									+ (expense.getAmount() * expense.getExchangeRate())
									/ expense.getTravelerSet().size();

							expenseSummary.setTodayExpense( Tools.roundTwoDecimals(amount));
						}
					}
				}

			}

		}

		List<ExpenseSummary> expenseSummaryList = new ArrayList<>();
		for (Map.Entry<String, ExpenseSummary> item : expenseSummaryMap
				.entrySet()) {
			expenseSummaryList.add(item.getValue());
		}

		return expenseSummaryList;

	}

}
