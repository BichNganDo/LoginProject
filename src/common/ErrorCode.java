package common;

public enum ErrorCode {
    SUCCESS(0), FAIL(-1), CONNECTION_FAIL(-2), NOT_EXIST(-3), EXIST_ACCOUNT(-4);

    private int value;

    private ErrorCode(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
}
