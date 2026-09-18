p1: # occurrences of the word Comedy in this file
	grep -P "\bComedy\b" movies.csv

p2: # prices that end with the digit 3
	grep -P "^\d*3," cars.csv

p3: # songs with two word titles
	grep -P "^[^\s,]+\s+[^\s,]+," songs.csv

p4: # ingredients with 1000 or more calories per 100 grams
	grep -P ",\d{4,} cal" ingredients.csv

p5: # meteorites with year 20XX where XX is any number
	grep -P "((Found)|(Fell)),20\d\d" meteorites.csv