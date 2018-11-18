package assembler.entity;

import static assembler.utils.Utilites.*;

import java.util.*;

import assembler.exception.*;

/**
 * A class representing Instructions.
 * 
 */
public class Instruction {
	/*
	 * Represents each kind of supported our CPU instructions.
	 */
	private static enum INST {
		/**
		 * AND (R-type)
		 */
		AND(0, 0),
		/**
		 * CAND (R-type)
		 */
		CAND(0, 1),
		/**
		 * OR (R-type)
		 */
		OR(0, 2),
		/**
		 * XOR (R-type)
		 */
		XOR(0, 3),
		/**
		 * ADD (R-type)
		 */
		ADD(1, 0),
		/**
		 * NADD (R-type)
		 */
		NADD(1, 1),
		/**
		 * SLT (R-type)
		 */
		SLT(1, 2),
		/**
		 * SLTU (R-type)
		 */
		SLTU(1, 3),
		/**
		 * ANDI (I-type)
		 */
		ANDI(4, null),
		/**
		 * CANDI (I-type)
		 */
		CANDI(5, null),
		/**
		 * ORI (I-type)
		 */
		ORI(6, null),
		/**
		 * XORI (I-type)
		 */
		XORI(7, null),
		/**
		 * ADDI (I-type)
		 */
		ADDI(8, null),
		/**
		 * NADDI (I-type)
		 */
		NADDI(9, null),
		/**
		 * SLTI (I-type)
		 */
		SLTI(10, null),
		/**
		 * SLTUI (I-type)
		 */
		SLTUI(11, null),
		/**
		 * SLL (I-type)
		 */
		SLL(12, null),
		/**
		 * SRL (I-type)
		 */
		SRL(13, null),
		/**
		 * SRA (I-type)
		 */
		SRA(14, null),
		/**
		 * ROR (I-type)
		 */
		ROR(15, null),
		/**
		 * LW (I-type)
		 */
		LW(16, null),
		/**
		 * SW (I-type)
		 */
		SW(17, null),
		/**
		 * BEQZ (B-type)
		 */
		BEQZ(20, null),
		/**
		 * BNEZ (B-type)
		 */
		BNEZ(21, null),
		/**
		 * BLTZ (B-type)
		 */
		BLTZ(22, null),
		/**
		 * BGEZ (B-type)
		 */
		BGEZ(23, null),
		/**
		 * BGTZ (B-type)
		 */
		BGTZ(24, null),
		/**
		 * BLEZ (B-type)
		 */
		BLEZ(25, null),
		/**
		 * JR (B-type)
		 */
		JR(26, null),
		/**
		 * JALR (B-type)
		 */
		JALR(27, null),
		/**
		 * SET (J-type)
		 */
		SET(28, null),
		/**
		 * SSET (J-type)
		 */
		SSET(29, null),
		/**
		 * J (J-type)
		 */
		J(30, null),
		/**
		 * JAL (J-type)
		 */
		JAL(31, null);

		// opcode for the instruction
		private final Integer opcode;

		// function code for the instruction (R-type instructions only; null for the others)
		private final Integer function;

		private INST(Integer opcode, Integer function) {
			this.opcode = opcode;
			this.function = function;
		}

		/**
		 * Returns opcode.
		 * 
		 * @return opcode for this instruction
		 */
		public Integer getOpcode() {
			return this.opcode;
		}

		/**
		 * Returns function.
		 * 
		 * @return function code for this instruction (null if the instruction is not a R-type)
		 */
		public Integer getFunction() {
			return this.function;
		}
	}

	private final INST inst;
	private final int lineNo;
	private final int stepNo;

	private Integer rs = 0;
	private Integer rt = 0;
	private Integer rd = 0;
	private Integer imm5 = 0;
	private Integer imm8 = 0;
	private Integer imm11 = 0;
	private String jumpto;


	/**
	 * Constructs new Instruction object.
	 * 
	 * @param inst
	 *            the instruction kind
	 * @param lineNo
	 *            line number for the current instruction
	 * @param stepNo
	 *            step number for the current instruction
	 */
	private Instruction(INST inst, int lineNo, int stepNo) {
		this.inst = inst;
		this.lineNo = lineNo;
		this.stepNo = stepNo;
	}

	/**
	 * Get INST object from the mnemonic.
	 * 
	 * @param mnemonic
	 *            the mnemonic
	 * @return the INST object for the mnemonic
	 */
	public static INST getInstByMnemonic(String mnemonic) {
		return INST.valueOf(mnemonic.toUpperCase());
	}

	/**
	 * Creates new Instruction object with the parameter.
	 * 
	 * @param inst
	 *            the instruction kind
	 * @param lineNo
	 *            line number for the current instruction
	 * @param stepNo
	 *            step no for the current instruction
	 * @return the new Instruction object
	 */
	public static Instruction createInstruction(INST inst, int lineNo, int stepNo) {
		return new Instruction(inst, lineNo, stepNo);
	}

	/**
	 * Creates new Instruction object from binary code.
	 * 
	 * @param hexexp
	 *            the binary code in hexadecimal expression (exactly in 8 chars)
	 * @param lineNo
	 *            line no for the current instruction
	 * @param stepNo
	 *            step no for the current instruction
	 * @return the new Instruction object
	 * @throws InvalidInstructionException
	 *             If hexexp was a invalid instruction
	 */
	public static Instruction createInstruction(String hexexp, int lineNo, int stepNo)
			throws InvalidInstructionException {
		INST inst;
		int newOp, newRs, newRt, newRd, newImm5, newFunc, newImm8, newImm11;

		// Check if the code is in the correct format
		if (!hexexp.matches("^[a-zA-Z0-9]{8}$")) {
			throw new InvalidInstructionException(hexexp, lineNo);
		}

		// Decode it as hex and convert it into binary expression
		String binexp = Long.toBinaryString(Long.decode("0x" + hexexp));

		// Supplement 0
		while (binexp.length() < 16) {
			binexp = "0" + binexp;
		}

		// Split binexp into each part of instruction
		newOp = Integer.parseInt(binexp.substring(0, 5), 2);
		newRs = Integer.parseInt(binexp.substring(5, 8), 2);
		newRt = Integer.parseInt(binexp.substring(8, 11), 2);
		newRd = Integer.parseInt(binexp.substring(11, 14), 2);
		newFunc = Integer.parseInt(binexp.substring(14, 16), 2);
		newImm5 = bStrToInt(binexp.substring(11, 16), 5);
		newImm8 = bStrToInt(binexp.substring(8, 16), 8);
		newImm11 = bStrToInt(binexp.substring(5, 16), 11); 	
		
		// Look for the instruction from the current op and func
		inst = null;
		for (INST newInst : INST.values()) {
			if (newOp == newInst.getOpcode()) {
				Integer newInstFunc = newInst.getFunction();
				if ((newInstFunc == null) || (newInstFunc == newFunc)) {
					inst = newInst;
				}
			}
		}

		// No such mnemonic
		if (inst == null) {
			throw new InvalidInstructionException(hexexp, lineNo);
		}

		Instruction instruction = new Instruction(inst, lineNo, stepNo);

		switch (inst) {
		case AND:
		case CAND:	
		case OR:
		case XOR:
		case ADD:
		case NADD:
		case SLT:
		case SLTU:
			// R-Type
			instruction.rs = newRs;
			instruction.rt = newRt;
			instruction.rd = newRd;
			break;

		case ANDI:
		case CANDI:	
		case ORI:
		case XORI:
		case ADDI:
		case NADDI:
		case SLTI:
		case SLTUI:
		case SLL:
		case SRL:
		case SRA:
		case ROR:
		case LW:
		case SW:
			// I-Type
			instruction.rs = newRs;
			instruction.rt = newRt;
			instruction.imm5 = newImm5;
			break;
			
		case BEQZ:
		case BNEZ:
		case BLTZ:
		case BGEZ:
		case BGTZ:
		case BLEZ:
		case JR:
		case JALR:
			// B-Type
			instruction.rs = newRs;
			instruction.imm8 = newImm8;
			break;
			
		case SET:
		case SSET:	
		case J:
		case JAL:
			// J-Type
			instruction.imm11 = newImm11;
			break;
		}

		return instruction;
	}

	/**
	 * Converts the current instruction into the assembler code.
	 * 
	 * @return the assembler code
	 */
	public String toCode() {
		StringBuffer strbuf = new StringBuffer(4);

		strbuf.append("\t" + inst.toString().toLowerCase() + "\t");

		switch (inst) {
		case AND:
		case CAND:	
		case OR:
		case XOR:
		case ADD:
		case NADD:
		case SLT:
		case SLTU:
			// $rd, $rs, $rt
			strbuf.append("$" + rd + ", ");
			strbuf.append("$" + rs + ", ");
			strbuf.append("$" + rt);
			break;
			
		case ANDI:
		case CANDI:	
		case ORI:
		case XORI:
		case ADDI:
		case NADDI:
		case SLTI:
		case SLTUI:
			// $rt, $rs, imm5
			strbuf.append("$" + rt + ", ");
			strbuf.append("$" + rs + ", ");
			strbuf.append(extendInt(imm5, 5, false));  //false >> sign-extended
			break;

		case SLL:
		case SRL:
		case SRA:
		case ROR:
			// $rt, $rs, imm5
			strbuf.append("$" + rt + ", ");
			strbuf.append("$" + rs + ", ");
			strbuf.append(extendInt(imm5, 5, true)); //true >> zero-extended
			break;
			
		case LW:
		case SW:
			// $rt, imm($rs)
			strbuf.append("$" + rt + ", ");
			strbuf.append(extendInt(imm5, 5, false) + "($" + rs + ")"); //false >> sign-extended
			break;

			
		case BEQZ:
		case BNEZ:
		case BLTZ:
		case BGEZ:
		case BGTZ:
		case BLEZ:
		case JR:
		case JALR:
			// $rs, label (or imm11)
			strbuf.append("$" + rs + ", ");
			if (jumpto == null) {
				strbuf.append(extendInt20(imm8, 8, false)); //false >> sign-extended
			} else {
				strbuf.append(jumpto);
			}
			break;


		case SET:
		case SSET:
			// imm11
			strbuf.append(extendInt(imm11, 11, true));
			break;

			
		case J:
		case JAL:
			// label (or imm11)
			strbuf.append(extendInt20(imm11, 11, false)); //false >> sign-extended

			break;
		}
		return strbuf.toString();
	}

	/**
	 * Parse arguments of the assembler code and set the values to the current instruction.
	 * @param args 
	 * the array of arguments (e.g., {"$1", "$2", "4"})
	 * @return true if the arguments are parsed successfully
	 * @throws InvalidArgumentException
	 * If arguments contain a syntax error
	 */
	public void parseArgs(String[] args) throws InvalidArgumentException {
		int argc = 0;

		try {
			switch (inst) {
			case AND:
			case CAND:	
			case OR:
			case XOR:
			case ADD:
			case NADD:
			case SLT:
			case SLTU:
				// $rd, $rs, $rt
				argc = 3;
				rd = getRegisterNumber(args[0]);
				rs = getRegisterNumber(args[1]);
				rt = getRegisterNumber(args[2]);
				if (!isDefined(rd, rs, rt)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;
				
				
			case ANDI:
			case CANDI:	
			case ORI:
			case XORI:
			case ADDI:
			case NADDI:
			case SLTI:
			case SLTUI:
			case SLL:
			case SRL:
			case SRA:
			case ROR:
				// $rt, $rs, imm5
				argc = 3;
				rt = getRegisterNumber(args[0]);
				rs = getRegisterNumber(args[1]);
				imm5 = dStrToInt(args[2], 5);
				if (!isDefined(rt, rs, imm5)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;
				
			case LW:
			case SW:
				// $rt, imm5($rs)
				argc = 2;
				rt = getRegisterNumber(args[0]);
				if (args[1].matches("^.+\\(\\$\\d{1,2}\\)$")) {
					int rsloc = args[1].indexOf('(');
					rs = getRegisterNumber(args[1].substring(rsloc + 1, args[1].indexOf(')')));
					imm5 = dStrToInt(args[1].substring(0, rsloc), 5);
				}
				if (!isDefined(rt, rs, imm5)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;
				
			case BEQZ:
			case BNEZ:
			case BLTZ:
			case BGEZ:
			case BGTZ:
			case BLEZ:
			case JALR:	//???
			case JR:	//???
				// $rs, label (or imm11) 
				argc = 2;
				rs = getRegisterNumber(args[0]);
				if (isIntegerForm(args[1])) {
					imm8 = dStrToInt(args[1], 8);
					if (!isDefined(imm8)) {
						throw new InvalidArgumentException(args[1], lineNo);
					}
				} else {
					jumpto = args[1];
					if (!isDefined(jumpto)) {
						throw new InvalidArgumentException(args[1], lineNo);
					}
				}
				if (!isDefined(rs)) {
					throw new InvalidArgumentException(lineNo);
				}

				break;
				
		
		

			case SET:
			case SSET:
				// imm11
				argc = 1;
				imm11 = dStrToInt(args[0], 11);
				if (!isDefined(imm11)) {
					throw new InvalidArgumentException(lineNo);
				}
				break;

			case J:
			case JAL:
				// label (or imm11)
				argc = 1;
				if (isIntegerForm(args[0])) {
					imm11 = dStrToInt(args[0], 11);
					if (!isDefined(imm11)) {
						throw new InvalidArgumentException(args[0], lineNo);
					}
				} else {
					jumpto = args[0];
					if (!isDefined(jumpto)) {
						throw new InvalidArgumentException(args[0], lineNo);
					}
				}
				break;
			}

			if (args.length != argc) {
				throw new InvalidArgumentException("Too many arguments; " + argc
						+ " argument(s) are expected, but found " + args.length, lineNo);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidArgumentException("Too few arguments; " + argc + " arguments are expected, but found " + args.length + " arguments", lineNo);
		}
	}

	/**
	 * Converts the current instruction into hexadecimal expression.
	 * @param labelMap 
	 * the label map
	 * @return the hexadecimal expression of the instruction
	 * @throws LabelNotFoundException
	 *             If the instruction is trying to jump to undefined label
	 */
	public String toHexString(Map<String, Integer> labelMap) throws LabelNotFoundException {
		String str = Long.toHexString(Long.parseLong(toBinaryString(labelMap, ""), 2));
		while (str.length() < 8) {
			str = "0" + str;
		}
		return str;
	}
	
	public String toHexStringA(Map<String, Integer> labelMap) throws LabelNotFoundException {
		String str = Long.toHexString(Long.parseLong(toBinaryString(labelMap, ""), 2));
		while (str.length() < 4) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * Converts the current instruction into binary expression.
	 * 
	 * @param labelMap
	 *            the label map
	 * @return the binary expression of the instruction
	 * @throws LabelNotFoundException
	 *             If the instruction is trying to jump to undefined label
	 */
	public String toBinaryString(Map<String, Integer> labelMap) throws LabelNotFoundException {
		return toBinaryString(labelMap, " ");
	}

	/**
	 * Converts the current instruction into binary expression.
	 * 
	 * @param labelMap
	 *            the label map
	 * @param separator
	 *            the String which is used to split each part of instruction
	 * @return the binary expression of the instruction, separated by the separator
	 * @throws LabelNotFoundException
	 *             If the instruction is trying to jump to undefined label
	 */
	public String toBinaryString(Map<String, Integer> labelMap, String separator) throws LabelNotFoundException {
		StringBuffer strbuf = new StringBuffer(6);
		strbuf.append(intToBinaryString(inst.getOpcode(), 5) + separator);

		switch (inst) {
		case AND:
		case CAND:	
		case OR:
		case XOR:
		case ADD:
		case NADD:
		case SLT:
		case SLTU:
			strbuf.append(intToBinaryString(rs, 3) + separator);
			strbuf.append(intToBinaryString(rt, 3) + separator);
			strbuf.append(intToBinaryString(rd, 3) + separator);
			strbuf.append(intToBinaryString(inst.getFunction(), 2));
			break;

		case ANDI:
		case CANDI:	
		case ORI:
		case XORI:
		case ADDI:
		case NADDI:
		case SLTI:
		case SLTUI:
		case SLL:
		case SRL:
		case SRA:
		case ROR:
		case LW:
		case SW:
			strbuf.append(intToBinaryString(rs, 3) + separator);
			strbuf.append(intToBinaryString(rt, 3) + separator);
			strbuf.append(intToBinaryString(imm5, 5));
			break;

		case BEQZ:
		case BNEZ:
		case BLTZ:
		case BGEZ:
		case BGTZ:
		case BLEZ:
		case JALR:	//???
		case JR:	//???
			// $rs, label (or imm8)
			strbuf.append(intToBinaryString(rs, 3) + separator);
			if (jumpto == null) {
				strbuf.append(intToBinaryString(imm8, 8));
			} else {
				Integer jumpAddr = labelMap.get(jumpto);
				if (jumpAddr == null) {
					throw new LabelNotFoundException(jumpto, lineNo);
				}
				strbuf.append(intToBinaryString(jumpAddr - stepNo, 8));
			}
			break;
			
		case SET:
		case SSET:
			// imm11
			strbuf.append(intToBinaryString(imm11, 11));
			break;

		case J:
		case JAL:
			// label (or imm11)
			if (jumpto == null) {
				strbuf.append(intToBinaryString(imm11, 11));
			} else {
				Integer jumpAddr = labelMap.get(jumpto);
				if (jumpAddr == null) {
					throw new LabelNotFoundException(jumpto, lineNo);
				}
				strbuf.append(intToBinaryString(jumpAddr - stepNo, 11));
			}
			break;

		}
		return strbuf.toString();
	}

	/**
	 * Run the instruction.
	 * 
	 * @param pc
	 *            the current program counter
	 * @param regfile
	 *            the register file
	 * @param memory
	 *            the memory
	 * @return the next program counter
	 */
	public int run(int pc, RegisterFile regfile, Memory memory) {
		int newPc = pc;
		boolean npc = true; // auto increment pc
		switch (inst) {
		case AND:
			regfile.set(rd, regfile.get(rs) & regfile.get(rt));
			break;
		case CAND:
			regfile.set(rd, ~regfile.get(rs) & regfile.get(rt));
			break;
		case OR:
			regfile.set(rd, regfile.get(rs) | regfile.get(rt));
			break;
		case XOR:
			regfile.set(rd, regfile.get(rs) ^ regfile.get(rt));
			break;
		case ADD:
			regfile.set(rd, regfile.get(rs) + regfile.get(rt));
			break;
		case NADD:
			regfile.set(rd, - regfile.get(rs) + regfile.get(rt));
			break;
		case SLT:
			if (regfile.get(rs) < regfile.get(rt)) {
				regfile.set(rd,  1);
			}
			break;
		case SLTU:
			if (regfile.get(rs) < regfile.get(rt)) {
				regfile.set(rd,  1);
			}
			break;
		case ANDI:
			regfile.set(rt, regfile.get(rs) & imm5);
			break;
		case CANDI:
			regfile.set(rt, regfile.get(rs) & imm5);
			break;
		case ORI:
			regfile.set(rt, regfile.get(rs) | imm5);
			break;
		case XORI:
			regfile.set(rt, regfile.get(rs) ^ imm5);
			break;
		case ADDI:
			regfile.set(rt, regfile.get(rs) + imm5);
			break;
		case NADDI:
			regfile.set(rt, - regfile.get(rs) + imm5);
			break;
		case SLTI:
			if (regfile.get(rs) < imm5) {
				regfile.set(rt,  1);
			}
			break;
		case SLTUI:
			if (regfile.get(rs) < imm5) {
				regfile.set(rt,  1);
			}
			break;
	
		case SLL:
			regfile.set(rt, regfile.get(rs) << imm5);
			break;
		case SRL:
			regfile.set(rt, regfile.get(rs) >> imm5);
			break;
		case SRA:
			regfile.set(rt, regfile.get(rs) >>> imm5);
			break;
		case ROR:
			regfile.set(rt, regfile.get(rs) >>> imm5  | regfile.get(rs) << (32-imm5));
			break;
		
		case LW:
			regfile.set(rt, memory.read(regfile.get(rs) + imm5));
			break;
		case SW:
			memory.write(regfile.get(rs) + imm5, regfile.get(rt));
			break;
			
		case BEQZ:
			if (regfile.get(rs) == 0) {
				newPc += imm8;
				npc = false;
			}
			break;
		case BNEZ:
			if (regfile.get(rs) != 0) {
				newPc += imm8;
				npc = false;

			}
			break;
		case BLTZ:
			if (regfile.get(rs) < 0) {
				newPc += imm8;
				npc = false;

			}
			break;
		case BGEZ:
			if (regfile.get(rs) >= 0) {
				newPc += imm8;
				npc = false;

			}
			break;
		case BGTZ:
			if (regfile.get(rs) > 0) {
				newPc += imm8;
				npc = false;

			}
			break;
		case BLEZ:
			if (regfile.get(rs) <= 0) {
				newPc += imm8;
				npc = false;


			}
			break;
		
		case JR:
			newPc = regfile.get(rs) + imm8;
			npc = false;
			break;
		
		case JALR:
			newPc = regfile.get(rs) + imm8;
			regfile.set(7, newPc + 1);
			npc = false;
			break;
	
		case SET:
			System.out.println(imm11);
			regfile.set(0, imm11);
			npc = true;
			break;
			
		case SSET:
			int tmp = (regfile.get(0) << 11) | imm11;
			regfile.set(0, tmp); 
			npc = true;
			break;

		case JAL:
			regfile.set(7, newPc + 1);
			newPc += imm11;
			npc = false;
			break;

		case J:
			newPc += imm11;
			npc = false;
			break;
		}
		if (npc) {
			newPc += 1;
		}
		return newPc;
	}

	/**
	 * Returns a string representation of the instruction.
	 * 
	 * @return a string representation of this instruction
	 */
	@Override
	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(inst);
		strbuf.append(": {");
		strbuf.append("lineNo: " + lineNo + ", ");
		strbuf.append("stepNo: " + stepNo + ", ");
		strbuf.append("rs: " + rs + ", ");
		strbuf.append("rt: " + rt + ", ");
		strbuf.append("rd: " + rd + ", ");
		strbuf.append("imm5: " + imm5 + ", ");
		strbuf.append("imm8: " + imm8 + ", ");
		strbuf.append("address: " + imm11 + ", ");
		strbuf.append("jumpto: " + jumpto);
		strbuf.append("}");
		return strbuf.toString();
	}
}
