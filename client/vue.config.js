module.exports = {
  configureWebpack: {
    devtool: 'source-map',
  },

  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:9000',
        ws: true,
        changeOrigin: true,
      },
    },
  },

  transpileDependencies: [
    'vuetify',
  ],
};
