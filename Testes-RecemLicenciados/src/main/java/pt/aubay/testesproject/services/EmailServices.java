package pt.aubay.testesproject.services;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import pt.aubay.testesproject.auxiliary.MyEmail;

@Path("email")
public class EmailServices {
	
	@Path("test")
	public void sendEmail() throws IOException{
	    Email from = new Email("anthonyduarte@outlook.com");
	    String subject = "Sending with SendGrid is Fun";
	    Email to = new Email("agd077@hotmail.com");
	    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
	    Mail mail = new Mail(from, subject, to, content);

	    SendGrid sg = new SendGrid(System.getProperty("SGkey"));
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders());
	    } catch (IOException ex) {
	      throw ex;
	    }
	}
	
	@POST
	@Path("send")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendEmail(MyEmail myEmail) throws IOException{
	    Email from = new Email("anthonyduarte@outlook.com");
	    String subject = "Sending with SendGrid is Fun";
	    Email to = new Email(myEmail.getEmailTo());
	    Content content = new Content("text/plain", myEmail.getBody());
	    Mail mail = new Mail(from, subject, to, content);

	    SendGrid sg = new SendGrid(System.getProperty("SGkey"));
	    Request request = new Request();
	    try {
	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders());
	    } catch (IOException ex) {
	      throw ex;
	    }
	    return "Enviado";
	}
}
