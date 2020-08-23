'use strict';
const path = require('path');
const MomentLocalesPlugin = require('moment-locales-webpack-plugin');

function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'public',
  productionSourceMap: false,

  devServer: {
    port: 2234,
    disableHostCheck: true,
    hot: true,
  },

  configureWebpack: {
    // provide the app's title in webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    // name: name,
    output: {
      filename: 'js/[name].[hash:8].js',
      chunkFilename: 'js/[name].[chunkhash:8].js',
    },
    resolve: {
      alias: {
        '@': resolve('src'),
      },
      symlinks: true,
    },
    plugins:[
      new MomentLocalesPlugin({
        localesToKeep: ['es-us', 'zh-cn','zh-tw'],
    }),
    ]
  },
  chainWebpack: function (config) {
    // config.module
    //   .rule('svg')
    //   .exclude.add(resolve('src/icons'))
    //   .end();
    // config.module
    //   .rule('icons')
    //   .test(/\.svg$/)
    //   .include.add(resolve('src/icons'))
    //   .end()
    //   .use('svg-sprite-loader')
    //   .loader('svg-sprite-loader')
    //   .options({
    //     symbolId: 'icon-[name]',
    //   })
    //   .end();
    config.module
      .rule('images')
      .test(/\.(png|jpe?g|gif|webp)(\?.*)?$/)
      .use('url-loader')
      .loader('url-loader')
      .options({
        limit: 4096,
        fallback: {
          loader: 'file-loader',
          options: {
            name: `images/[name].[hash:8].[ext]`,
          },
        },
      });

    config.plugin('html').tap(args => {
      args[0].title = 'Noah小说网';
      return args;
    });
  },

  css: {
    extract: {
      filename: 'style/[name].[contenthash:8].css',
      chunkFilename: 'style/[name].[contenthash:8].css',
    },
    loaderOptions: {
      less: {
        lessOptions: {
          // important extra layer for less-loader^6.0.0
          javascriptEnabled: true
        }
      }
    }
  }
}