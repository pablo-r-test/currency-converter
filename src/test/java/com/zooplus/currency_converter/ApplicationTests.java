package com.zooplus.currency_converter;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void testHome() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		ResponseEntity<String> entity = this.restTemplate.exchange("/", HttpMethod.GET,
				new HttpEntity<Void>(headers), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		assertThat(entity.getHeaders().getLocation().toString())
				.endsWith(this.port + "/login");
	}

	@Test
	public void testLoginPage() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		ResponseEntity<String> entity = this.restTemplate.exchange("/login",
				HttpMethod.GET, new HttpEntity<Void>(headers), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getBody()).contains("_csrf");
	}

	@Test
	public void testRegisterAndLogin() {
		registerTestUserUser();
		loginWithTestUser();
	}

	private void loginWithTestUser() {
		HttpHeaders headers = getHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.set("username", "user");
		form.set("password", "password");
		ResponseEntity<String> entity = this.restTemplate.exchange("/login",
				HttpMethod.POST, new HttpEntity<>(form, headers), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		assertThat(entity.getHeaders().getLocation().toString())
				.endsWith(this.port + "/");
		assertThat(entity.getHeaders().get("Set-Cookie")).isNotNull();
	}

	private void registerTestUserUser() {
		HttpHeaders registerHeaders = getHeaders();
		registerHeaders.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		registerHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.set("username", "user");
		form.set("password", "password");
		form.set("firstName", "user");
		form.set("lastName", "password");
		form.set("email", "2@m.com");
		form.set("birthday", "1985-03-03");
		form.set("address.country", "Spain");
		form.set("address.city", "Valencia");
		form.set("address.street", "45 Mdarigal");
		form.set("address.zip", "83623");
		ResponseEntity<String> entity = this.restTemplate.exchange("/register",
				HttpMethod.POST, new HttpEntity<>(form, registerHeaders), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<String> page = this.restTemplate.getForEntity("/login",
				String.class);
		assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
		String cookie = page.getHeaders().getFirst("Set-Cookie");
		headers.set("Cookie", cookie);
		Pattern pattern = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		Matcher matcher = pattern.matcher(page.getBody());
		assertThat(matcher.matches()).as(page.getBody()).isTrue();
		headers.set("X-CSRF-TOKEN", matcher.group(1));
		return headers;
	}

}
