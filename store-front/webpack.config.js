const webpack = require('webpack');
const path = require('path');
const rules = require('./webpack.rules');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const HOST = process.env.HOST || "localhost";
const PORT = process.env.PORT || "8888";

module.exports = {
  // disabling cache in watch mode due to SCSS reload issue - REMOVE it for production
  cache: false,
  mode: "production",
  entry: [
    './src/index.jsx'
  ],
  output: {
    path: path.join(__dirname, 'public'),
    publicPath: '/',
    filename: 'bundle.js'
  },
  module: {
    rules
  },
  resolve: {
    extensions: ['.js', '.jsx']
  },
  devServer: {
    historyApiFallback: true,
    contentBase: "./public",
    port: PORT,
    host: HOST
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "styles.css"
    }),
    new HtmlWebpackPlugin({
      template: './src/template.html',
      files: {
        css: ['styles.css'],
        js: ['bundle.js'],
      }
    })
  ]
};
