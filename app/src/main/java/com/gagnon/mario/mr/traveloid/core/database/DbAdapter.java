package com.gagnon.mario.mr.traveloid.core.database;

import com.gagnon.mario.mr.traveloid.category.Category;
import com.gagnon.mario.mr.traveloid.expense.Expense;
import com.gagnon.mario.mr.traveloid.pettycash.PettyCash;
import com.gagnon.mario.mr.traveloid.traveler.Traveler;
import com.gagnon.mario.mr.traveloid.trip.Trip;

import java.util.List;

public interface DbAdapter {

	//region Category Methods
    List<Category> categoryFetchAll() throws  RepositoryException;
    Category categorySave(Category category) throws  RepositoryException;
	//endregion

	//region Expense Methods
	List<Expense> expenseFetchForTrip(Long tripId) throws RepositoryException;
	Expense expenseSave(Expense expense) throws  RepositoryException;
	//endregion

	//region Petty Cash Methods
	List<PettyCash> pettyCashFetchForTrip(Long tripId) throws RepositoryException;
	PettyCash pettyCashSave(PettyCash pettyCash) throws  RepositoryException;
	//endregion

	//region Traveler Methods
	List<Traveler> travelerFetchAll() throws RepositoryException;
	Traveler travelerSave(Traveler traveler) throws  RepositoryException;
	int travelerCount() throws RepositoryException;
	//endregion

	//region Trip Methods
	Trip tripFetch(Long rowId) throws RepositoryException;
	List<Trip> tripFetchAll() throws RepositoryException;

	void tripChangeDefault(Trip newDefaultTrip) throws RepositoryException;
	Trip tripFetchDefault() throws RepositoryException;

	Trip tripSave(Trip trip) throws RepositoryException;
	//endregion

}