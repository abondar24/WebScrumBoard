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
    }
};
