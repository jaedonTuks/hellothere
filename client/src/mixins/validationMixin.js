export default {
  methods: {
    getFileValidationRules() {
      return [(attachments) => attachments.reduce((accumulator, attachment) => accumulator + attachment.size, 0) < 4000000 || 'Attachments size should be less than 4 MB'];
    },

    getEmailRule() {
      const emailRegex = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
      return (emails) => emails.every((email) => emailRegex.test(email)) || 'Invalid Email';
    },

    getEmailBodyRules() {
      return [(v) => !!v || 'Email body is required'];
    },
  },
};
