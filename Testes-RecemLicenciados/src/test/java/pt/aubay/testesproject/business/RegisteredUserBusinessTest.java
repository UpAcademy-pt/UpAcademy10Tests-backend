package pt.aubay.testesproject.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.business.RegisteredUserBusiness;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisteredUserBusinessTest {
	RegisteredUserBusiness userBusiness=new RegisteredUserBusiness();
	
	@Test
	@Order(1)
	@DisplayName("Teste da função de username or email?")
	public void isUsernameOrEmailTest() {
		assertEquals("username",userBusiness.isUsernameOrEmail("olago"));
		assertEquals("email",userBusiness.isUsernameOrEmail("olago@tree.com"));
	}
}
