package xyz.kebigon.dictionnairejaponais.writer;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataWriter<T extends Comparable<T>> implements Closeable {
	private static final int DEFAULT_FLUSH_SIZE = 100;

	private final List<T> values = new ArrayList<>();
	private final String fileName;
	private final ObjectMapper mapper = new ObjectMapper();
	private final int flushSize;

	public DataWriter(String fileName) {
		this(fileName, DEFAULT_FLUSH_SIZE);
	}

	public DataWriter(String fileName, int flushSize) {
		this.fileName = fileName;
		this.flushSize = flushSize;
	}

	public void write(T value) throws IOException {
		values.add(value);

		if (values.size() % flushSize == 0)
			flush();
	}

	private void flush() throws IOException {
		log.info("Writing {} to disk.", fileName);
		final long startTime = System.nanoTime();

		Collections.sort(values);

		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			mapper.writeValue(writer, values);
		}

		final long stopTime = System.nanoTime();
		log.info("{} values writtent in {} ms", values.size(), (stopTime - startTime) / 1000000);
	}

	@Override
	public void close() throws IOException {
		flush();
	}
}
