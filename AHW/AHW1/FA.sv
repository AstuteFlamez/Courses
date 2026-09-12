///////////////////////////////////////////////////
// FA.sv  This design will take in 3 bits       //
// and add them to produce a sum and carry out //
////////////////////////////////////////////////
module FA(
  input 	A,B,Cin,	// three input bits to be added
  output	S,Cout		// Sum and carry out
);

	/////////////////////////////////////////////////
	// Declare any internal signals as type logic //
	///////////////////////////////////////////////
	logic AxorB;
  	logic carryAB;
  	logic carryCin;
	
	/////////////////////////////////////////////////
	// Implement Full Adder as structural verilog //
	///////////////////////////////////////////////
	xor (AxorB, A, B);
  	xor (S, AxorB, Cin);

  	and (carryAB, A, B);
  	and (carryCin, AxorB, Cin);
  	or  (Cout, carryAB, carryCin);
	
endmodule