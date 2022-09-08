<template>
  <iframe
      class="emailHtml pa-4"
      title=""
      :height="height"
      :ref="`iframe`"
      :srcdoc="email.body"
      @load="calculateHeight"
  />
</template>
<script>
export default {
  name: 'EmailHtml',
  props: {
    email: {},
  },

  data() {
    return {
      height: '0px',
    };
  },

  methods: {
    async calculateHeight() {
      const { iframe } = this.$refs;
      if (!iframe) {
        this.height = '200px';
        return;
      }

      await this.$nextTick(() => {
        this.height = `${iframe.contentWindow.document.body.scrollHeight + 60}px`;
      });
    },
  },
};
</script>
<style scoped>
html {
  margin: 0 !important;
}

.emailHtml {
  width: 100%;
  border: none;
}

@media only screen and (max-width: 1264px) {

  h1 {
    font-size: 1.3em;
  }

  h2 {
    font-size: 1.2em;
  }
}

</style>
