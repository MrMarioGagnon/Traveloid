package com.gagnon.mario.mr.traveloid.core;

/**
 * Created by Mario on 8/24/2015.
 */
public class Account {

    private String accountName;
    private String currency;
    private Double exchangeRate;
    private Boolean isPettyCash;
    private Boolean isDefault = false;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsPettyCash() {
        return isPettyCash;
    }

    public void setIsPettyCash(Boolean isPettyCash) {
        this.isPettyCash = isPettyCash;
    }

    public Account(String accountName, String currency, Double exchangeRate, Boolean isPettyCash) {
        this.accountName = accountName;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        this.setIsPettyCash(isPettyCash);
    }

    public Account(String accountName, String currency, Double exchangeRate) {
        this.accountName = accountName;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        this.setIsPettyCash(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        if (!getAccountName().equals(account.getAccountName())) return false;
        if (!getCurrency().equals(account.getCurrency())) return false;
        return getExchangeRate().equals(account.getExchangeRate());

    }

    @Override
    public int hashCode() {
        int result = getAccountName().hashCode();
        result = 31 * result + getCurrency().hashCode();
        result = 31 * result + getExchangeRate().hashCode();
        return result;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {

        if(getIsPettyCash()){
            return String.format("%s, %.2f %s", getAccountName(), getExchangeRate(), getCurrency());
        }
        else{
            return getAccountName();
        }

    }
}
