////////////////////////////////////////////////////////
// RCA32.sv  This design will add two 32-bit vectors //
// plus a carry in to produce a sum and a carry out //
/////////////////////////////////////////////////////
module RCA32(
  input 	[31:0]	A,B,	// two 32-bit vectors to be added
  input 			Cin,	// An optional carry in bit
  output 	[31:0]	S,		// 32-bit Sum
  output 			Cout  	// and carry out
);

	/////////////////////////////////////////////////
	// Declare any internal signals as type logic //
	///////////////////////////////////////////////
	wire [31:0] Carries;	// this is driven by .Cout of FA and will
							// in a "promoted" form drive .Cin of FA's
	FA fa[31:0](.A(A), .B(B), .Cin({Carries[30:0], Cin}), .S(S), .Cout(Carries));
	assign Cout = Carries[31];
	// Implement Full Adder as structural verilog //
	///////////////////////////////////////////////
	
	
endmodule