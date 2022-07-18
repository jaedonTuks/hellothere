package hellothere.model.email

enum class EmailFormat(val value: String) {
    MINIMAL("minimal"),
    FULL("full"),
    RAW("raw"),
    METADATA("metadata"),
    UNSPECIFIED("unspecified");
}
