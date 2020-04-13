# Assembler-Simulator-for-Pipelined-Processor
This Project was considered as a Bonus Part for the Pipelined Processor Design Project. The Assembler aim to translate our CPU instruction to machine language format, so we can test our CPU easily by putting the machine code into Processor Circuit, NO NEED TO TRANSLATED BY HAND :)

## Important Notes
The beginning tap-space is reserved for the label (if exist)


Also you should end each instruction with a semicolon ‘;’ 


R-type format:
<tap space> add $2, $3, $4 ;


I-type format:
<tap space> addi $2, $3, 5 ;


B-type format:
<tap space> beqz $2,  label ;
label: <tap space> next inst. ;


J-type format:
<tap space> j label ;
label: <tap space> next inst. ;
