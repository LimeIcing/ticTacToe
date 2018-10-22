public enum Slot {
    EMPTY ("-"),
    X ("X"),
    O ("O");

    private String value;

    Slot(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}