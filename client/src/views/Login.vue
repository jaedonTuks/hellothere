<template>
  <v-container class="login">
    <v-row
      style="height: 100vh;"
      justify="center"
      align="center"
    >
      <v-card
        color="accent"
        class="pa-5 loginCard"
      >
        <v-card-title class="active">
          <div>
            <h1 class="logo">Hello there!</h1>
            <div :class="{
              underline: true,
              active: scaleUnderline
            }"/>
          </div>
        </v-card-title>
        <v-card-text class="justify-center">
          <p class="subtitle">Making emails better</p>
        </v-card-text>
        <v-card-actions class="justify-center">
          <v-btn @click="redirectToGoogleOAuth">
            Login with Google
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-row>
  </v-container>
</template>

<script>

export default {
  name: 'Login',

  data() {
    return {
      scaleUnderline: false,
    };
  },

  methods: {
    redirectToGoogleOAuth() {
      window.location.href = 'http://localhost:9000/api/gmail/login';
    },
  },

  mounted() {
    this.scaleUnderline = true;
  },
};
</script>

<style scoped>
.underline {
  margin-top: 10px;
  content: '';
  width: 100%;
  height: 4px;
  border-radius: 2px;
  bottom: -10px;
  left: 0;
  background: -webkit-linear-gradient(
    1turn,
    rgba(135, 240, 252, 1),
    rgba(191, 120, 251, 1),
    rgba(250, 115, 198, 1));
  transform-origin: bottom left;
  transition: transform 1s ease-out;
  transform: scaleX(0);
}

.underline.active {
  transform: scaleX(1) !important;
}

.subtitle {

  /*border-bottom: 2px solid aliceblue;*/
  /*padding: 5px;*/
  text-align: center;
  font-size: 1.4em;
}
</style>
