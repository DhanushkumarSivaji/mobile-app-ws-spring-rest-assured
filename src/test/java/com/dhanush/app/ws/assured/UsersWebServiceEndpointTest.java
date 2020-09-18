package com.dhanush.app.ws.assured;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;


@TestMethodOrder(Alphanumeric.class)
class UsersWebServiceEndpointTest {

	private final String CONTEXT_PATH="/mobile-app-ws";
	private final String EMAIL_ADDRESS = "dhanushkumarsivaji@g.com";
	private final String JSON = "application/json";
	private static String authorizationHeader;
	private static String userId;
	private static List<Map<String, String>> addresses;
	
	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=8888;
	}
	
	/*
	 * testUserLogin()
	 * 
	 * */
	
	@Test
	final void a() {
		 Map<String, String> loginDetails = new HashMap<>();
		 loginDetails.put("email", EMAIL_ADDRESS);
		 loginDetails.put("password", "123");
		 
		 Response resposne = RestAssured.given().
		 contentType(JSON).
		 accept(JSON).
		 body(loginDetails).
		 when().
		 post(CONTEXT_PATH + "/users/login").
		 then().
		 statusCode(200).extract().response();
		 
		 authorizationHeader = resposne.header("Authorization");
		 userId = resposne.header("UserID");
		 
		 assertNotNull(authorizationHeader);
		 assertNotNull(userId);
	}
	
	/*
	 * testGetUserDetails()
	 * 
	 * */
	
	@Test
	final void b() {
		
		Response response = RestAssured.given()
		 .pathParam("id", userId)
		 .header("Authorization",authorizationHeader)
		 .accept(JSON)
		 .when()
		 .get(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS, userEmail);
		
		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);

	}
	
	/*
	 * Test Update User Details
	 * */
	@Test
	final void c()
	{
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Dhanush");
		userDetails.put("lastName", "kumar");
		
		 Response response = RestAssured.given()
		 .contentType(JSON)
		 .accept(JSON)
		 .header("Authorization",authorizationHeader)
		 .pathParam("id", userId)
		 .body(userDetails)
		 .when()
		 .put(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		 
         String firstName = response.jsonPath().getString("firstName");
         String lastName = response.jsonPath().getString("lastName");
         
         List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");
         
         assertEquals("Dhanush", firstName);
         assertEquals("kumar", lastName);
         assertNotNull(storedAddresses);
         assertTrue(addresses.size() == storedAddresses.size());
         assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
	}
	
	/*
	 * Test the Delete User Details
	 * */
	@Test
//	@Ignore
	final void d()
	{
		Response response = RestAssured.given()
		.header("Authorization",authorizationHeader)
		.accept(JSON)
		.pathParam("id", userId)
		.when()
		.delete(CONTEXT_PATH + "/users/{id}")
		.then()
		.statusCode(200)
		.contentType(JSON)
		.extract()
		.response();
		
		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS", operationResult);
		
	}

}
