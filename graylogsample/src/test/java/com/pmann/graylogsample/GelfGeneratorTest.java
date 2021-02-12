package com.pmann.graylogsample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class GelfGeneratorTest {
	// Successfully transforms a single input message to GELF format
	@Test
	void singleMsgFromInputStreamSuccess() throws IOException {
		String input = "{\"field1\": \"value1\"}";
		String expected = "{\"_field1\":\"value1\",\"version\":\"1.1\",\"host\":\"test host\",\"short_message\":\"message 1\"}";

		try (GelfGenerator generator = new GelfGenerator(new ByteArrayInputStream(input.getBytes()), "test host")) {
			String result = generator.next();

			assertTrue(result.contentEquals(expected), "Unexpected result: " + result + " expected: " + expected);
		}
	}

	// Successfully produces some output from file input
	@Test
	void singleMsgFromFileSuccess() throws IOException {
		try (GelfGenerator generator = new GelfGenerator(getResourcePath("messages-one.txt"), "test host")) {
			String result = generator.next();

			assertFalse(result.isEmpty());
		}
	}

	// Output message contains all the required GELF fields
	@Test
	void singleMsgContainsRequiredFields() throws IOException {
		try (GelfGenerator generator = new GelfGenerator(getResourcePath("messages-one.txt"), "test host")) {
			String result = generator.next();

			assertTrue(checkReservedFields(result), "Missing at least one reserved GELF field: " + result);
		}
	}

	// Successfully prepends "_" to output field names from a single input message
	@Test
	void singleMsgFieldSubstitutions() throws IOException {
		try (GelfGenerator generator = new GelfGenerator(getResourcePath("messages-one.txt"), "test host")) {
			String result = generator.next();

			assertTrue(checkFieldNames(result), "Additional field does not have prepended underscore");
		}
	}

	// Throws expected exception when input file does not exist
	@Test
	void throwsOnInvalidFilename() throws IOException {
		assertThrows(FileNotFoundException.class, () -> new GelfGenerator("badfile", "host"));
	}

	// Throws expected exception when we call next() and there are no more remaining
	// messages
	@Test
	void throwsOnExceededEOF() throws IOException {
		try (GelfGenerator generator = new GelfGenerator(getResourcePath("messages-one.txt"), "test host")) {
			generator.next();

			assertFalse(generator.hasNext(), "Unexpected input");
			assertThrows(IOException.class, () -> generator.next());
		}
	}

	// Return true if msg contains all the mandatory fields
	boolean checkReservedFields(String string) {
		JsonElement jsonMsg = JsonParser.parseString(string);
		JsonObject obj = jsonMsg.getAsJsonObject();

		Set<Entry<String, JsonElement>> entries = obj.entrySet();
		long reservedFields = entries.stream().filter(e -> isReservedGelfField(e.getKey())).count();
		return (reservedFields == 3);
	}

	// Return true if all additional fields have "_" prepended
	boolean checkFieldNames(String string) {
		JsonElement jsonMsg = JsonParser.parseString(string);
		JsonObject obj = jsonMsg.getAsJsonObject();

		Set<Entry<String, JsonElement>> entries = obj.entrySet();
		long invalidFields = entries.stream().filter(e -> !isReservedGelfField(e.getKey()))
				.filter(e -> !e.getKey().startsWith(GelfGenerator.GELF_PREFIX)).count();
		return (invalidFields == 0);
	}

	boolean isReservedGelfField(String key) {
		return key.contentEquals(GelfGenerator.GELF_HOST) || key.contentEquals(GelfGenerator.GELF_VERSION)
				|| key.contentEquals(GelfGenerator.GELF_SHORT_MESSAGE);
	}

	String getResourcePath(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		return classLoader.getResource(name).getPath();
	}
}
