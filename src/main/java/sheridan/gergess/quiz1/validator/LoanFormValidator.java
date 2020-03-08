package sheridan.gergess.quiz1.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import sheridan.gergess.quiz1.model.LoanForm;

@SuppressWarnings("unused")
public class LoanFormValidator implements Validator {

    private final Logger logger = LoggerFactory.getLogger(LoanFormValidator.class);

    @Override
    public boolean supports(Class<?> type) {
        return LoanForm.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        logger.trace("The validator is called to validate.");
        LoanForm form = (LoanForm) obj;
        if (form.getLoanAmount().trim().isEmpty()) {
            errors.rejectValue("loanAmount", "NotBlank.loanAmount.form.loanAmount");
        } else {
            try {
                double amount = Double.parseDouble(form.getLoanAmount());
                if(amount <= 0){
                    errors.rejectValue("loanAmount", "Positive.loanAmount.form.loanAmount");
                }
                double cents = amount*100;
                if(cents - Math.floor(cents) != 0.0){
                    errors.rejectValue("loanAmount", "IntegerCents.loanAmount.form.loanAmount");
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("loanAmount", "Number.loanAmount.form.loanAmount");
            }
        }

        if (form.getAnnualInterestRate().trim().isEmpty()) {
            errors.rejectValue("annualInterestRate", "NotBlank.annualInterestRate.form.annualInterestRate");
        } else {
            try {
                double rate = Double.parseDouble(form.getAnnualInterestRate());
                if(rate <= 0){
                    errors.rejectValue("annualInterestRate", "Positive.annualInterestRate.form.annualInterestRate");
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("annualInterestRate", "Number.annualInterestRate.form.annualInterestRate");
            }
        }

        if (form.getNumberOfYears().trim().isEmpty()) {
            errors.rejectValue("numberOfYears", "NotBlank.numberOfYears.form.numberOfYears");
        } else {
            try {
                int years = Integer.parseInt(form.getNumberOfYears().trim());
                if(years <= 0 ){
                    errors.rejectValue("numberOfYears", "Positive.numberOfYears.form.numberOfYears");
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("numberOfYears", "Integer.numberOfYears.form.numberOfYears");
            }
        }
    }

}
