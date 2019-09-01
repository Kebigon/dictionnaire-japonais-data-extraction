package xyz.kebigon.dictionnairejaponais.writer;

import java.io.IOException;
import java.io.Writer;

import lombok.AllArgsConstructor;
import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.WordEntry;

@AllArgsConstructor
public class JSONDataWriter implements DataWriter {
	private final Writer writer;

	@Override
	public void write(Member member) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(WordEntry wordEntry) throws IOException {
		writer.write("{\"id\":\"" + wordEntry.getId() + "\",\"jp\":\"" + wordEntry.getJapanese() + "\",\"kn\":\""
				+ wordEntry.getFurigana() + "\",\"rm\":\"" + wordEntry.getRomaji() + "\",\"fr\":\""
				+ escape(wordEntry.getFrench()) + "\",\"dt\":\"" + escape(wordEntry.getDetails()) + "\"},\n");
	}

	private static String escape(String s) {
		return s //
				.replace("\\", "\\\\") // Replace \ by \\
				.replace("\"", "\\\""); // Replace " by \"
	}
}
