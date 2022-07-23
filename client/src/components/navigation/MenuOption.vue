<template>
  <v-btn
      text
      plain
      :ripple="false"
      :class="{
        button: true,
        active: $route.name === routeName
      }"
      :to="{ name: routeName }"
  >
    <span v-if="isMobile" class="mr-2 underlineEffect">{{ routeName }}</span>
    <v-badge
        color="deep-purple accent-4"
        overlap
        :content="notificationCount"
        :value="notificationCount"
    >
      <v-img class="icon mr-2" :src="getImageUrl()"/>
    </v-badge>
    <span v-if="isNotMobile" class="mr-2 underlineEffect">{{ routeName }}</span>
  </v-btn>
</template>
<script>
import screenSizeMixin from '@/mixins/screenSizeMixin';

export default {
  name: 'MenuOption',
  mixins: [screenSizeMixin],

  props: {
    notificationCount: {
      type: Number,
      default: 0,
    },
    routeName: String,
    iconName: String,
  },

  methods: {
    getImageUrl() {
      // eslint-disable-next-line global-require,import/no-dynamic-require
      return require(`../../assets/icons/${this.iconName}.png`);
    },
  },
};
</script>
<style scoped>

span {
  color: var(--v-info-base);
}

.icon {
  filter: grayscale(1);
  width: 2em;
  transition: 0.5s;
}

.button.active .icon {
  filter: grayscale(0);
}

.button.active {
  opacity: 1 !important;
}

.button:hover .icon {
  filter: grayscale(0);
}

.underlineEffect {
  display: inline-block;
}

.button:hover .underlineEffect::before {
  transform: scaleX(1);
}

.active .underlineEffect::before {
  transform: scaleX(1) !important;
}

.underlineEffect::before {
  content: '';
  position: absolute;
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
  transition: transform 0.4s ease-out;
  transform: scaleX(0);
}

</style>
