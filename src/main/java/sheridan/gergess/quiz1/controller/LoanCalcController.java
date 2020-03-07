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
    public ModelAndView input(@CookieValue(value = "loanAmount", defaultValue = "") String loanAmount,
                              @CookieValue(value = "annualInterest", defaultValue = "") String annualInterest,
                              @CookieValue(value = "numOfYears", defaultValue = "") String numOfYears,
                              HttpServletResponse response){
        if (loanAmount.isEmpty() || annualInterest.isEmpty() || numOfYears.isEmpty()){
            Cookie cookie = new Cookie("empty", "empty");

            cookie.setMaxAge(7*24*60*60);
            response.addCookie(cookie);
            return new ModelAndView("Input","form", new LoanForm());
        }else{
            String amount = cookieEncoder.decode(loanAmount);
            String interest = cookieEncoder.decode(annualInterest);
            String years = cookieEncoder.decode(numOfYears);

            ModelAndView modelAndView = new ModelAndView("Input");

            modelAndView.addObject("loanAmount", loanAmount);
            modelAndView.addObject("annualInterest", annualInterest);
            modelAndView.addObject("numOfYears", numOfYears);

            return modelAndView;
        }

    }

    @RequestMapping("/Calculate.do")
    public ModelAndView calculate(
            @RequestParam(defaultValue = "") String loanAmount,
            @CookieValue(value = "loanAmount", defaultValue = "") String loanAmountC,
            @RequestParam(defaultValue = "") String annualInterest,
            @CookieValue(value = "annualInterest", defaultValue = "") String annualInterestC,
            @RequestParam(defaultValue = "") String numOfYears,
            @CookieValue(value = "numOfYears", defaultValue = "") String numOfYearsC,
            HttpServletResponse response,
            @Validated @ModelAttribute(name="form") LoanForm form,
            BindingResult bindingResult){

        logger.trace("Received a user input.");
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
            loan.setLoanAmount(Double.parseDouble(form.getLoanAmount()));
            loan.setAnnualInterestRate(Double.parseDouble(form.getAnnualInterestRate()));
            loan.setNumberOfYears(Integer.parseInt(form.getNumberOfYears()));



            Cookie amountCookie = new Cookie("loanAmount", cookieEncoder.encode(loanAmount));
            Cookie interestCookie = new Cookie("annualInterest", cookieEncoder.encode(annualInterest));
            Cookie yearsCookie = new Cookie("numOfYears", cookieEncoder.encode(numOfYears));
            amountCookie.setMaxAge(7*24*60*60);
            interestCookie.setMaxAge(7*24*60*60);
            yearsCookie.setMaxAge(7*24*60*60);

            response.addCookie(amountCookie);
            response.addCookie(interestCookie);
            response.addCookie(yearsCookie);

            ModelAndView mv = new ModelAndView("Output", "loan", loan);

            mv.addObject("loanAmount", loan);
            mv.addObject("annualInterest", annualInterest);
            mv.addObject("numOfYears", numOfYears);

            // make the object available to the Output page and show the page
            return mv ;
        }
    }

}
