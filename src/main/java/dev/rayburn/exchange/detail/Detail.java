package dev.rayburn.exchange.detail;

import lombok.Data;

@Data
public class Detail {
	private String code;
	private int decimal_digits;
	private String name;
	private String name_plural;
	private int rounding;
	private String symbol;
	private String symbol_native;
}
