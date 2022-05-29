package hellothere.model.email

enum class EmailHeaderName(val value: String) {
    SUBJECT("subject"),
    FROM("from"),
    MESSAGE_ID("Message-Id"),
    REFERENCE("Reference"),
    IN_REPLY_TO("In-Reply-To");
}
