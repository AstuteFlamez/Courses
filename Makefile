p1: # occurrences of the word Comedy in this file
	grep -P "???" movies.csv
p2: # prices that end with the digit 3
	grep -P "???" cars.csv
p3: # songs with two word titles
	grep -P "???" songs.csv
p4: # ingredients with 1000 or more calories per 100 grams
	grep -P "???" ingredients.csv
p5: # meteorites with year 20XX where XX is any number
	grep -P "((Found)|(Fell)),20\d\d" meteorites.csv
