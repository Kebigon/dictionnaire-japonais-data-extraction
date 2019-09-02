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
public class Member implements Comparable<Member> {
	@JsonProperty("id")
	private int id;
	@JsonProperty("words")
	private int words;

	@Override
	public int compareTo(Member o) {
		return -Integer.compare(words, o.words);
	}
}
