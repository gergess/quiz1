package sheridan.gergess.quiz1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sheridan.gergess.quiz1.encoder.CookieEncoder;
import sheridan.gergess.quiz1.model.Loan;
import sheridan.gergess.quiz1.model.LoanForm;
import sheridan.gergess.quiz1.validator.LoanFormValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("unused")
@Controller
public class LoanCalcController {

    private final Logger logger = LoggerFactory.getLogger(LoanCalcController.class);
    private final CookieEncoder cookieEncoder;

    public LoanCalcController(@Qualifier("cookieUrlEncoder") CookieEncoder cookieEncoder){
        this.cookieEncoder = cookieEncoder;
    }

    @InitBinder("form")
    protected void initBinder(WebDataBinder binder) {
            binder.setValidator(new LoanFormValidator());
    }

    @RequestMapping(value={"/","/Input.do"})
    public ModelAndView input(@CookieValue(value = "loanAmountC", defaultValue = "") String loanAmount,
                              @CookieValue(value = "annualInterestC", defaultValue = "") String annualInterest,
                              @CookieValue(value = "numOfYearsC", defaultValue = "") String numOfYears){

        if (loanAmount.isEmpty() || annualInterest.isEmpty() || numOfYears.isEmpty()) {

            return new ModelAndView("Input", "form", new LoanForm());
        } else {
            String amount = cookieEncoder.decode(loanAmount);
            String interest = cookieEncoder.decode(annualInterest);
            String years = cookieEncoder.decode(numOfYears);

            LoanForm form = new LoanForm();

            form.setLoanAmount(amount);
            form.setAnnualInterestRate(interest);
            form.setNumberOfYears(years);

            ModelAndView modelAndView = new ModelAndView("Input", "form", form);

            return modelAndView;
        }

    }

    @RequestMapping("/Calculate.do")
    public ModelAndView calculate(
            @CookieValue(value = "loanAmountC", defaultValue = "") String loanAmountC,
            @CookieValue(value = "annualInterestC", defaultValue = "") String annualInterestC,
            @CookieValue(value = "numOfYearsC", defaultValue = "") String numOfYearsC,
            HttpServletResponse response,
            @Validated @ModelAttribute(name="form") LoanForm form,
            BindingResult bindingResult){

        // check the validation errors
        if (bindingResult.hasErrors()) {
            logger.trace("The received data is invalid, going back to the inputs.");
            // if we got input errors, we are going back to the Input page
            // insert the previous user inputs into the Input page
            // the errors are already included
            return new ModelAndView("Input", "form" , form);
        } else {
            logger.trace("The input data is valid.");
            // if no errors, the input data is valid
            // convert the data into numbers for the calculation
            // put the numbers in the object for the calculation

            Loan loan = new Loan();

            ModelAndView mv = new ModelAndView("Output", "loan", loan);

            if(loanAmountC.isEmpty() || annualInterestC.isEmpty() || numOfYearsC.isEmpty()){

                loan.setLoanAmount(Double.parseDouble(form.getLoanAmount()));
                loan.setAnnualInterestRate(Double.parseDouble(form.getAnnualInterestRate()));
                loan.setNumberOfYears(Integer.parseInt(form.getNumberOfYears()));

                Cookie amountCookie = new Cookie("loanAmountC", cookieEncoder.encode(form.getLoanAmount()));
                Cookie interestCookie = new Cookie("annualInterestC", cookieEncoder.encode(form.getAnnualInterestRate()));
                Cookie yearsCookie = new Cookie("numOfYearsC", cookieEncoder.encode(form.getNumberOfYears()));
                amountCookie.setMaxAge(7*24*60*60);
                interestCookie.setMaxAge(7*24*60*60);
                yearsCookie.setMaxAge(7*24*60*60);

                response.addCookie(amountCookie);
                response.addCookie(interestCookie);
                response.addCookie(yearsCookie);

                mv.addObject("loanAmountC", form.getLoanAmount());
                mv.addObject("annualInterestC", form.getAnnualInterestRate());
                mv.addObject("numOfYearsC", form.getNumberOfYears());

            }

            // make the object available to the Output page and show the page
            return mv ;
        }
    }

}
