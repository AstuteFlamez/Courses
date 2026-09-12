module RCA32_tb();

  reg [31:0] A_op,B_op;		// stimulus for A[31:0] and B[31:0]
  reg Cin;					// stimulus for Cin
  wire [31:0] S;			// wire to connect to sum for self-check
  wire Cout;				// outputs to monitor of type wire
  reg error,gold_Cout;
  reg [31:0] gold_sum;
  integer vectors;			// index used to apply random vectors
  
  //////////////////////
  // Instantiate DUT //
  ////////////////////
  RCA32 iDUT(.A(A_op),.B(B_op),.Cin(Cin),.S(S),.Cout(Cout));
  
  initial begin
    error = 1'b0;	// innnocent till proven guilty
	/// start with a known case before going to randoms ///
	$display("Lets start with a couple of classic vectors for adder");
	A_op = 32'h55667766;
	B_op = 32'hAA998899;
	Cin = 1'b0;
	#5;
	if (S!==32'hFFFFFFFF) begin
	  $display("ERR: 0x55667766 + 0xAA998899 with a Cin = 0");
	  $display("     should result in 0xFFFFFFFF, your result is %h",S);
	  error = 1'b1;
	end
	
	Cin = 1'b1;		// now apply a carry in
	#5
	if (S!==32'h00000000) begin
	  $display("ERR: 0x55667766 + 0xAA998899 with a Cin = 1");
	  $display("     should result in 0x00000000, your result is %h",S);
	  $stop();
	end	
	if (Cout!==1'b1) begin
	  $display("ERR: 0x55667766 + 0xAA998899 with a Cin = 1");
	  $display("     should result in a Cout of 1");
	  $stop();
	end
	
	$display("GOOD: now lets try some random values");
    for (vectors=0; vectors<15; vectors++) begin
	  A_op = $random;		// $random is built in and returns 32-bits
	  B_op = $random;		// $random is built in and returns 32-bits
	  Cin = $random%2'b10;			// produce 1-bit random number
	  #5;
	  {gold_Cout,gold_sum} = A_op + B_op + Cin;
	  if (gold_Cout!==Cout) begin
	    error = 1'b1;
		$display("ERR: expected Cout=%b for A=%h, B=%h with Cin=%b",gold_Cout,A_op,B_op,Cin);
	  end 	
	  if (S!==gold_sum) begin
	    error = 1'b1;			// guilty
		$display("ERR: expected S=%h with A=%h, B=%h, Cin=%b",gold_sum,A_op,B_op,Cin);
      end
	end
	if (!error)
	  $display("YAHOO!! test passed");
	$stop();
  end
  
endmodule