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
	@JsonProperty("jp")
	private String japanese;
	@JsonProperty("kn")
	private String furigana;
	@JsonProperty("rm")
	private String romaji;
	@JsonProperty("fr")
	private String french;
	@JsonProperty("dt")
	private String details;

	@Override
	public int compareTo(WordEntry o) {
		return Integer.compare(id, o.id);
	}
}
