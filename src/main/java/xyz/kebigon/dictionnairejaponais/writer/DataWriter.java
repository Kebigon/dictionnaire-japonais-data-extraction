package xyz.kebigon.dictionnairejaponais.writer;

import java.io.IOException;

import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.WordEntry;

public interface DataWriter {
	void write(Member member) throws IOException;

	void write(WordEntry wordEntry) throws IOException;
}
