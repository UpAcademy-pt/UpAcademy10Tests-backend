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
		System.out.println(myEmail);
	    Email from = new Email("anthonyduarte@outlook.com");
	    String subject = myEmail.getSubject();
	    Email to = new Email(myEmail.getEmailTo());
	    String html="    <table class=\"module\"\r\n" + 
	    		"           role=\"module\"\r\n" + 
	    		"           data-type=\"divider\"\r\n" + 
	    		"           border=\"0\"\r\n" + 
	    		"           cellpadding=\"0\"\r\n" + 
	    		"           cellspacing=\"0\"\r\n" + 
	    		"           width=\"100%\"\r\n" + 
	    		"           style=\"table-layout: fixed;\">\r\n" + 
	    		"      <tr>\r\n" + 
	    		"        <td style=\"padding:0px 0px 0px 0px;\"\r\n" + 
	    		"            role=\"module-content\"\r\n" + 
	    		"            height=\"100%\"\r\n" + 
	    		"            valign=\"top\"\r\n" + 
	    		"            bgcolor=\"\">\r\n" + 
	    		"          <table border=\"0\"\r\n" + 
	    		"                 cellpadding=\"0\"\r\n" + 
	    		"                 cellspacing=\"0\"\r\n" + 
	    		"                 align=\"center\"\r\n" + 
	    		"                 width=\"100%\"\r\n" + 
	    		"                 height=\"1px\"\r\n" + 
	    		"                 style=\"line-height:1px; font-size:1px;\">\r\n" + 
	    		"            <tr>\r\n" + 
	    		"              <td\r\n" + 
	    		"                style=\"padding: 0px 0px 1px 0px;\"\r\n" + 
	    		"                bgcolor=\"#000000\"></td>\r\n" + 
	    		"            </tr>\r\n" + 
	    		"          </table>\r\n" + 
	    		"        </td>\r\n" + 
	    		"      </tr>\r\n" + 
	    		"    </table>\r\n" + 
	    		""
	    		+ "<table class=\"wrapper\" role=\"module\" data-type=\"image\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\">\r\n" + 
	    		"      <tr>\r\n" + 
	    		"        <td style=\"font-size:6px;line-height:10px;padding:0px 0px 0px 0px;\" valign=\"top\" align=\"left\">\r\n" + 
	    		"          <img class=\"max-width\" border=\"0\" style=\"display:block;color:#000000;text-decoration:none;font-family:Helvetica, arial, sans-serif;font-size:16px;max-width:15% !important;width:15%;height:auto !important;\" src=\"https://www.aubay.pt/img/aubayCopy.png\" alt=\"\" width=\"90\">\r\n" + 
	    		"        </td>\r\n" + 
	    		"      </tr>\r\n" + 
	    		"    </table>\r\n" + 
	    		"  \r\n" + "    <table class=\"module\" role=\"module\" data-type=\"text\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\">\r\n" + 
	    				"      <tr>\r\n" + 
	    				"        <td style=\"padding:18px 0px 18px 0px;line-height:22px;text-align:inherit;\"\r\n" + 
	    				"            height=\"100%\"\r\n" + 
	    				"            valign=\"top\"\r\n" + 
	    				"            bgcolor=\"\">\r\n" + 
	    				"            <div><strong><span style=\"color:#808080;\"><span style=\"font-size:9px;\">Edifício Duque D'Ávila</span></span></strong></div>\r\n" + 
	    				"\r\n" + 
	    				"<div><strong><span style=\"color:#808080;\"><span style=\"font-size:9px;\">Avenida Duque de Ávila, nº46 7C</span></span></strong></div>\r\n" + 
	    				"\r\n" + 
	    				"<div><strong><span style=\"color:#808080;\"><span style=\"font-size:9px;\">1050-083 Lisboa</span></span></strong></div>\r\n" + 
	    				"\r\n" + 
	    				"        </td>\r\n" + 
	    				"      </tr>\r\n" + 
	    				"    </table>\r\n" + 
	    				"";
	    Content content = new Content("text/html", myEmail.getBody()+html);
	    Mail mail = new Mail(from, subject, to, content);
	    //mail.setTemplateId("d-fc6db0e66c214696a3d4c4b2efc161ec");

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
