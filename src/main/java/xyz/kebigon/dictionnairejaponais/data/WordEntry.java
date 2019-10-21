package xyz.kebigon.dictionnairejaponais.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordEntry implements Comparable<WordEntry> {
	@JsonProperty("id")
	private int id;
	@JsonProperty("j")
	private String japanese;
	@JsonProperty("k")
	private String furigana;
	@JsonProperty("f")
	private String french;

	@Override
	public int compareTo(WordEntry o) {
		return Integer.compare(id, o.id);
	}
}
