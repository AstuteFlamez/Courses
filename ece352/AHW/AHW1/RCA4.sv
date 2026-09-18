///////////////////////////////////////////////////////
// RCA4.sv  This design will add two 4-bit vectors  //
// plus a carry in to produce a sum and a carry out//
////////////////////////////////////////////////////
module RCA4(
  input 	[3:0]	A,B,	// two 4-bit vectors to be added
  input 			Cin,	// An optional carry in bit
  output 	[3:0]	S,		// 4-bit Sum
  output 			Cout  	// and carry out
);

	/////////////////////////////////////////////////
	// Declare any internal signals as type logic //
	///////////////////////////////////////////////
  	wire [4:0] Carries;

  	assign Carries[0] = Cin;
  	assign Cout = Carries[4];	
	
	/////////////////////////////////////////////////
	// Implement Full Adder as structural verilog //
	///////////////////////////////////////////////
	FA fa0(.A(A[0]), .B(B[0]), .Cin(Carries[0]), .S(S[0]), .Cout(Carries[1]));
	FA fa1(.A(A[1]), .B(B[1]), .Cin(Carries[1]), .S(S[1]), .Cout(Carries[2]));
	FA fa2(.A(A[2]), .B(B[2]), .Cin(Carries[2]), .S(S[2]), .Cout(Carries[3]));
	FA fa3(.A(A[3]), .B(B[3]), .Cin(Carries[3]), .S(S[3]), .Cout(Carries[4]));

	
endmodule