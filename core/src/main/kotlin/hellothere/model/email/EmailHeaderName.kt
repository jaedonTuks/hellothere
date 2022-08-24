package hellothere.model.email

enum class EmailHeaderName(val value: String) {
    CC("CC"),
    EMAIL_TO("To"),
    FROM("from"),
    IN_REPLY_TO("In-Reply-To"),
    MESSAGE_ID("Message-Id"),
    REFERENCE("Reference"),
    SUBJECT("subject");
}
