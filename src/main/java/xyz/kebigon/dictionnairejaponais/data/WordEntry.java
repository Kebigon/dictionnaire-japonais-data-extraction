package xyz.kebigon.dictionnairejaponais.data;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WordEntry {
	private final String id;
	private final String japanese;
	private final String furigana;
	private final String romaji;
	private final String french;
	private final String details;
}
