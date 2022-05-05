import { useEffect, useMemo, useState } from 'react';
import { Box, MenuItem, Stack, TextField, Theme } from '@mui/material';
import { Rate } from './types';

export function App() {
	const [baseCurrency, setBaseCurrency] = useState('USD');
	const [baseCurrencyExchangeRates, setBaseCurrencyExchangeRates] = useState<Rate | null>(null);
	const [exchangeCurrency, setExchangeCurrency] = useState('EUR');
	const [baseQuantity, setBaseQuantity] = useState(1);
	const [supportedCurrencyList, setSupportedCurrencyList] = useState<Record<string, string>>({});
	const [isLoadingSupportedCurrencyList, setIsLoadingSupportedCurrencyList] = useState(true);

	// On first load, get a list of supported currencies.
	useEffect(() => {
		setIsLoadingSupportedCurrencyList(true);
		fetch('/api/detail/currencyList')
			.then((res) => {
				return res.json();
			})
			.then((data) => {
				setSupportedCurrencyList(data);
			})
			.finally(() => {
				setIsLoadingSupportedCurrencyList(false);
			});
	}, []);

	// Anytime the baseCurrency updates, get the new rates.
	useEffect(() => {
		fetchCurrencyExchangeRates(baseCurrency).then((data) => {
			setBaseCurrencyExchangeRates(data);
		});
	}, [baseCurrency]);

	// Creates and memos an array of MenuItems to use
	// for the currency dropdowns.
	const supportedCurrencyMenuItems = useMemo(() => {
		return Object.entries(supportedCurrencyList)
			.sort(([, valueOne], [, valueTwo]) => {
				return valueOne.localeCompare(valueTwo);
			})
			.map(([key, val]) => {
				return (
					<MenuItem key={key} value={key}>
						{val}
					</MenuItem>
				);
			});
	}, [supportedCurrencyList]);

	const exchangeQuantity = useMemo(() => {
		if (baseCurrencyExchangeRates?.[baseCurrency.toLowerCase()]?.[exchangeCurrency.toLowerCase()]) {
			return baseQuantity * baseCurrencyExchangeRates[baseCurrency.toLowerCase()][exchangeCurrency.toLowerCase()];
		}
		// Shows -1 while loading, or on invalid values.
		return -1;
	}, [baseCurrency, baseCurrencyExchangeRates, baseQuantity, exchangeCurrency]);

	return (
		<Box sx={{ marginTop: (theme: Theme) => theme.spacing(4) }}>
			{isLoadingSupportedCurrencyList ? (
				'Loading...'
			) : (
				<Stack spacing={6}>
					<Stack direction='row'>
						<TextField
							fullWidth
							helperText='This is the currency you want to convert from.'
							label='Starting Currency'
							onChange={(event) => setBaseCurrency(event.target.value)}
							select
							value={baseCurrency}
						>
							{supportedCurrencyMenuItems}
						</TextField>
						<TextField
							fullWidth
							helperText='This is the currency you want to convert to.'
							label='Exchange Currency'
							onChange={(event) => {
								setExchangeCurrency(event.target.value);
							}}
							select
							value={exchangeCurrency}
						>
							{supportedCurrencyMenuItems}
						</TextField>
					</Stack>
					<Stack direction='row'>
						<TextField
							fullWidth
							label='Amount'
							onChange={(event) => {
								setBaseQuantity(event.target.value as unknown as number);
							}}
							type='number'
							value={baseQuantity}
						/>
						<TextField disabled fullWidth label='Exchanged Amount' value={exchangeQuantity} />
					</Stack>
				</Stack>
			)}
		</Box>
	);
}

function fetchCurrencyExchangeRates(currency: string) {
	const now = new Date();
	const year = String(now.getFullYear());
	const month = String(now.getMonth() < 11 ? '0' + now.getMonth() : now.getMonth());
	const day = String(now.getDate() < 10 ? '0' + now.getDate() : now.getDate());
	const folder = `${year}-${month}-${day}`;
	const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;

	return fetch(`/api/rate/${currency}?folder=${folder}&timeZone=${timeZone}`).then((res) => {
		return res.json();
	});
}
