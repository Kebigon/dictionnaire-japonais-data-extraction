package xyz.kebigon.dictionnairejaponais.writer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.dictionnairejaponais.data.Member;

@Slf4j
public class DataReader {
	private final String fileName;
	private final ObjectMapper mapper = new ObjectMapper();

	public DataReader(String fileName) {
		this.fileName = fileName;
	}

	public List<Member> readAll() throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			final List<Member> readValue = mapper.readValue(reader, new TypeReference<List<Member>>() {
			});
			log.info("from file: {}, {}", readValue.getClass().getSimpleName(), readValue);
			return readValue;
		}
	}

}
