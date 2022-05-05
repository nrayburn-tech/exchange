export interface Rate {
	/**
	 * Date this Rate is valid for.
	 */
	date: string;

	/**
	 * The key is the currency code this rate is for, ie "usd".
	 * This maps to a Record of all other currencies, and the exchange rate.
	 */
	[key: string]: Record<string, number>;
}

export interface CurrencyDetail {
	code: string;
	decimal_digits: number;
	name: string;
	name_plural: string;
	rounding: number;
	symbol: string;
	symbol_native: string;
}
