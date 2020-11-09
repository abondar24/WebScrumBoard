module.exports = {
    configureWebpack: {
        resolve: {
            alias: require('./aliases.config').webpack
        },
        devServer: {
            proxy: 'http://localhost:8024/cxf/wsboard'
        },
    },

    css: {
        // Enable CSS source maps.
        sourceMap: true
    },

    outputDir: 'target/classes/dist',
    assetsDir: 'static',

    pluginOptions: {
      i18n: {
        locale: 'en',
        fallbackLocale: 'en',
        localeDir: 'locales',
        enableInSFC: true
      }
    }
};
