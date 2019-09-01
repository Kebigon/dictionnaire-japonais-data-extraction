package xyz.kebigon.dictionnairejaponais.writer;

import java.io.IOException;
import java.io.Writer;

import lombok.AllArgsConstructor;
import xyz.kebigon.dictionnairejaponais.data.Member;
import xyz.kebigon.dictionnairejaponais.data.WordEntry;

@AllArgsConstructor
public class CSVDataWriter implements DataWriter {
	private static final char TAB = '\t';
	private static final char NL = '\n';

	private final Writer writer;

	@Override
	public void write(Member member) throws IOException {
		writer.write(member.getId() + NL);
	}

	@Override
	public void write(WordEntry wordEntry) throws IOException {
		writer.write(wordEntry.getId() + TAB + wordEntry.getJapanese() + TAB + wordEntry.getFurigana() + TAB
				+ wordEntry.getRomaji() + TAB + wordEntry.getFrench() + TAB + wordEntry.getDetails() + NL);
	}
}
