package com.validateadwords.web.beans.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;

@FacesValidator(value="emailValidator")
public class EmailValidator implements Validator{
	
	protected Logger logger = Logger.getLogger(EmailValidator.class);

	 public void validate(FacesContext facesContext, 
	            UIComponent uIComponent, Object object) throws ValidatorException {
		 
	        String enteredEmail = (String)object;
	        //Set the email pattern string
	        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	        
	        //Match the given string with the pattern
	        Matcher m = p.matcher(enteredEmail);
	        
	        //Check whether match is found
	        boolean matchFound = m.matches();
	        
	        if (!matchFound) {
	            FacesMessage message = new FacesMessage();
	            message.setDetail("O campo email n�o � v�lido.");
	            message.setSummary("O campo email n�o � v�lido.");
	            message.setSeverity(FacesMessage.SEVERITY_ERROR);
	            throw new ValidatorException(message);
	        }
	    }

}
