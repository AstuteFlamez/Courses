/*======================================================================
 * PROJECT: C-cret Codex
 *----------------------------------------------------------------------
 * AUTHOR: Bilal Usman
 * EMAIL: busman4@wisc.edu
 * ADDITIONAL SOURCES: NONE
 * provided by course staff) that were used to complete this code here>
 * FILE: ccret.c
 * COURSE: COMP SCI 354 - Fall 2026
 * INSTRUCTOR: Dahl
 * COPYRIGHT: 2026, Dahl
 * Posting or sharing this file with anyone outside of course staff prohibited.
 */

#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include "ccret.h"

/*
 * Entry point for this C-cret Codex application.  This program can be used
 * to encode and decode plain text using a variety of different substitution
 * ciphers.
 *
 * This program takes 2 or 3 command line arguments (in addition to this
 * executable's name).  The first argument is an optional -d, which specifies
 * that the message should be decoded rather than encoded with the given key.
 * The next argument is the key that should be used for encoding or decoding.
 * And the last argument is the message.  This program will the print out
 * the resulting encoded/decoded message to standard out.
 *
 * Examples:
 *   ./ccret A abc                -> encoded: ZYX
 *   ./ccret -d A ZYX             -> decoded: ABC
 *   ./ccret cc "hello, world"    -> encoded: JGNNQ, YQTNF
 */
int main(int argc, char** argv) {

	if (argc != 3 && argc != 4) {
		fprintf(stderr, "Usage: %s [-d] KEY MESSAGE\n", argv[0]);
		return 1;
	}

	int decode = 0; 
	/* 
	0: encode message
	1: decode message
	*/
	char* key;
	char* message;
	if (argc == 4) {
		if (strcmp(argv[1], "-d") != 0) {
			fprintf(stderr, "Usage: %s [-d] KEY MESSAGE\n", argv[0]);
			return 1;
		}
		decode = 1;
		key = argv[2];
		message = argv[3];
	} else {
		key = argv[1];
		message = argv[2];
	}

	char output[strlen(message) + 1];
			
	ccret(key,decode,message,output);
	printf("%s: %s\n",!decode?"encoded":"decoded",output);
	
	return 0;
}

/*
 * This function does the heavy lifting for the C-cret Codex application.
 * Arugments:
 * key is a c-string reference to the key used for encoding/decoding
 * pDecode is either 0: encode message with key, otherwise message is decoded
 * message is a c-string to the message being encoded or decoded
 * output references the memory where the encoded/decoded string is written
 */
void ccret(char* key, int pDecode, char* message, char* output) {

	char substitutionMap[LETTER_COUNT];
	int keyLength = strlen(key);
	int startIndex = keyLength > 0 ? 1 % keyLength : 0;
	int polyalphabetic = keyLength > 0 && toupper((unsigned char)key[0]) == 'P';

	for (int i = 0; message[i] != '\0'; i++) {
		char c = message[i];
		int letterIndex = toupper((unsigned char)c) - 'A';
		if (letterIndex >= 0 && letterIndex < LETTER_COUNT) {
			if (polyalphabetic)
				createSubstitutionMap(key + startIndex, substitutionMap);
			else
				createSubstitutionMap(key, substitutionMap);
			if (pDecode) invertMap(substitutionMap);
			c = substitutionMap[letterIndex] + 'A';
			if (polyalphabetic)
				startIndex = (startIndex + 1) % keyLength;
		}
		output[i] = c;
	}
	output[strlen(message)] = '\0';
}

/*
 * Creates a substitution map that can be used for encoding messages using
 * a variety of different ciphers.  This function makes use of the helper
 * functions: createAtbashMap, createCaesarMap, and createMixedMap.  When
 * the first letter of a key is 'a') it creates the map for an Atbash cipher, 
 * 'c') it creates the map for a Caesar cipher, otherwise) it creates a map
 * for a Mixed Alphabet cipher.
 */
void createSubstitutionMap(char* key, char* map) {

	// this placeholder identity map can be overwritten or replaced
	for(int i=0;i<LETTER_COUNT;i++) map[i] = i;
	
	if (toupper(key[0]) == 'A') createAtbashMap(key, map);
	else if (toupper(key[0]) == 'C') createCaesarMap(key, map);
	else createMixedMap(key, map);
}

/*
 * This function inverts a substitution map so that the result can be used for
 * decoding messages rather than encoding them.  For example, if A maped to X
 * in the input map, then X will map back to A in that map after calling this
 * function.
 */
void invertMap(char* map) {
	
	char invertedMap[LETTER_COUNT];	
	for(int i=0;i<LETTER_COUNT;i++)
		invertedMap[(int)map[i]] = i;
	memcpy(map,invertedMap,LETTER_COUNT*sizeof(char));
}

void createAtbashMap(char* key, char* map) {
	for (int i = 0; i < LETTER_COUNT; i++) map[i] = LETTER_COUNT - 1 - i;
}

void createCaesarMap(char* key, char* map) {
	int offset = strlen(key) % LETTER_COUNT;
	for (int i = 0; i < LETTER_COUNT; i++)
		map[i] = (i + offset) % LETTER_COUNT;
}

void createMixedMap(char* key, char* map) {
	int used[LETTER_COUNT] = {0};
	int mapIndex = 0;

	for (int i = 0; key[i] != '\0'; i++) {
		if (isalpha((unsigned char)key[i])) {
			int letterIndex = toupper((unsigned char)key[i]) - 'A';
			if (!used[letterIndex]) {
				map[mapIndex++] = letterIndex;
				used[letterIndex] = 1;
			}
		}
	}

	for (int i = 0; i < LETTER_COUNT; i++) {
		if (!used[i]) map[mapIndex++] = i;
	}
}


// EOF -----------------------------------------------------------------
