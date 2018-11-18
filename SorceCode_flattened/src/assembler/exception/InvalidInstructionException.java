package assembler.exception;

/**
 * Thrown when the instruction was in invalid format.
 * 
 */
@SuppressWarnings("serial")
public class InvalidInstructionException extends Exception {
	private final String inststr;
	private final int lineNo;

	public InvalidInstructionException(String inststr, int lineNo) {
		this.inststr = inststr;
		this.lineNo = lineNo;
	}

	@Override
	public String getMessage() {
		return "Invalid instruction \"" + inststr + "\" on line " + lineNo + ".";
	}

}
