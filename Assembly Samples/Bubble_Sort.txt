		set 4  ; numOfComprison = arraySize - 1 
whileLoop: 	beqz $0, EndWhile ; if (numOfComprison) == 0
		xor $1, $1, $1 ; i(forLoop) = 0
ForLoop: 		nadd $5, $0, $1 ;
		beqz $5, EndForLoop ; if (i) == (numOfComprison)
		addi $2, $1, 0 ;
		lw $3, 0($2) ; array[i]
		lw $4, 1($2) ; array[i+1]
		slt $6, $4, $3 ;
		beqz $6, Endif ; if array[i] <= array[i+1]
		sw $4, 0($2) ; swap array[i+1]= array[i]  
		sw $3, 1($2) ; swap array[i] = array[i+1]
Endif: 		addi $1, $1, 1 ; i++
	                  j ForLoop ;
EndForLoop: 	addi $0, $0, -1  ; numOfComprison-- 
		j whileLoop   ;
EndWhile:		addi $7, $7, 1 ;