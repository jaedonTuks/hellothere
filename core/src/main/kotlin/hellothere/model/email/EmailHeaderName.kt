package hellothere.model.email

enum class EmailHeaderName(val value: String) {
    SUBJECT("subject"),
    FROM("from"),
    MESSAGE_ID("Message-Id"),
    REFERENCE("Reference"),
    EMAIL_TO("To"),
    IN_REPLY_TO("In-Reply-To");
}
