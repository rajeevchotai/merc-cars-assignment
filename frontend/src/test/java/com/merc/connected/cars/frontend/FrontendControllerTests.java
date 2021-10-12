package com.merc.connected.cars.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merc.connected.cars.frontend.svc.FrontendService;
import com.merc.connected.cars.model.PersonModel;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {FrontendController.class})
@WebMvcTest(controllers = FrontendController.class)
class FrontendControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FrontendService frontendService;

	@InjectMocks
	private FrontendController frontendController;


	@Test
	public void testStoreFutureDate() throws Exception {
		String person="{ \"age\": 20, \"dob\": \"2021-10-13\", \"id\": 0, \"name\": \"Hello\", \"salary\": 122111241.15}";
		testStoreInvalidFields(person,"CSV",person);
	}

	@Test
	public void testStoreNullAge() throws Exception {
		String person="{  \"dob\": \"2021-10-10\", \"id\": 0, \"name\": \"Hello\", \"salary\": 122111241.15}";
		testStoreInvalidFields(person,"CSV",person);
	}

	@Test
	public void testStoreNullDate() throws Exception {
		String person="{ \"age\": 20, \"id\": 0, \"name\": \"Hello\", \"salary\": 122111241.15}";
		testStoreInvalidFields(person,"CSV",person);
	}

	@Test
	public void testStoreNullName() throws Exception {
		String person="{ \"age\": 20, \"dob\": \"2021-10-10\", \"id\": 0, \"salary\": 122111241.15}";
		testStoreInvalidFields(person,"CSV",person);
	}

	@Test
	public void testStoreNullSalary() throws Exception {
		String person="{ \"age\": 20, \"dob\": \"2021-10-10\", \"id\": 0, \"name\": \"Hello\"}";
		testStoreInvalidFields(person,"CSV",person);
	}

	@Test
	public void testStoreValidJSON() throws Exception {
		String person="{\"id\":0,\"name\":\"Hello\",\"dob\":\"2021-10-10\",\"salary\":1.2211124115E8,\"age\":20}";
		testStoreValidJSON("CSV",person);
	}

	@Test
	public void testStoreXMLValidJSON() throws Exception {
		String person="{\"id\":0,\"name\":\"Hello\",\"dob\":\"2021-10-10\",\"salary\":1.2211124115E8,\"age\":20}";
		testStoreValidJSON("XML",person);
	}

	@Test
	public void testUpdateValidJSON() throws Exception {
		String person="{\"id\":0,\"name\":\"Hello\",\"dob\":\"2021-10-10\",\"salary\":1.2211124115E8,\"age\":20}";
		testUpdate("CSV",person);
	}

	@Test
	public void testUpdateXMLValidJSON() throws Exception {
		String person="{\"id\":0,\"name\":\"Hello\",\"dob\":\"2021-10-10\",\"salary\":1.2211124115E8,\"age\":20}";
		testUpdate("XML",person);
	}

	@Test
	public void testReadEmptyResult() throws Exception {

		List<PersonModel> personModels = new ArrayList<>();
		when(frontendService.read()).thenReturn(personModels);

		mockMvc.perform(MockMvcRequestBuilders.get("/read")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();

		verify(frontendService, times(1)).read();
	}

	@Test
	public void testReadException() throws Exception {

		List<PersonModel> personModels = new ArrayList<>();
		when(frontendService.read()).thenThrow(new Exception());

		mockMvc.perform(MockMvcRequestBuilders.get("/read")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andReturn();

		verify(frontendService, times(1)).read();
	}

	@Test
	public void testReadResult() throws Exception {

		List<PersonModel> personModels = new ArrayList<>();
		PersonModel personModel = new PersonModel();
		personModels.add(personModel);
		when(frontendService.read()).thenReturn(personModels);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/read")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		verify(frontendService, times(1)).read();
		ObjectMapper mapper = new ObjectMapper();

		// this uses a TypeReference to inform Jackson about the Lists's generic type
		List<PersonModel> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonModel>>() {});

		assertNotNull(actual);
		assertEquals(personModels, actual);

	}

	private void testStoreValidJSON(String fileType, String person) throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/store?fileType="+fileType)
						.content(person)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String resultPerson = result.getResponse().getContentAsString();
		assertNotNull(resultPerson);
		assertEquals(person, resultPerson);
	}

	private void testStoreInvalidFields(String person, String fileType, String output) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/store?fileType="+fileType)
						.content(person)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

	}

	private void testUpdate(String fileType, String person) throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/update?fileType="+fileType)
						.content(person)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String resultPerson = result.getResponse().getContentAsString();
		assertNotNull(resultPerson);
		assertEquals(person, resultPerson);
	}



}
