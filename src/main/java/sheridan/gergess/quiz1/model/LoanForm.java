package sheridan.gergess.quiz1.model;

import java.io.Serializable;

@SuppressWarnings("ALL")
public class LoanForm implements Serializable {

    private String annualInterestRate="";
    private String numberOfYears="";
    private String loanAmount="";

    public String getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(String annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public String getNumberOfYears() {
        return numberOfYears;
    }

    public void setNumberOfYears(String numberOfYears) {
        this.numberOfYears = numberOfYears;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }
}
