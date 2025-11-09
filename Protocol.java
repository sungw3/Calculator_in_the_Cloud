public class Protocol {
    // Response types & codes
    public static final String RESP_PREFIX = "RESP";
    public static final String TYPE_ANS = "ANS";
    public static final String TYPE_ERR = "ERR";


    public static final int CODE_OK = 0;
    public static final int ERR_INVALID_OPCODE = 1;
    public static final int ERR_ARG_COUNT = 2;
    public static final int ERR_DIV_BY_ZERO = 3;
    public static final int ERR_INVALID_NUMBER = 4;
    public static final int ERR_INTERNAL = 5;
}