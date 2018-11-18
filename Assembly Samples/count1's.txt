	xor $2, $2, $2 ;
	lw $1, 0($0) ;
Next:	andi $3, $1, 1 ;
	add $2, $2, $3 ;
	srl $1, $1, 1 ;
	bnez $1, Next ;