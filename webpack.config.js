const path = require("path");
const webpack = require("webpack");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
module.exports = {
    entry: {
        librarian: "./src/main/resources/ui/js/librarian.js"
    },
    mode: "development",
    module: {
        rules: [
            {
                test: /\.vue$/,
                exclude: /(node_modules|bower_components)/,
                loader: "vue-loader"
                /*options: {presets: ["@babel/env"]}*/
            },
            {
                test: /\.js$/,
                loader: "babel-loader",
                options: {presets: ["@babel/env"]}
            },
            {
                test: /\.template$/,
                loader: 'raw-loader'
            }
        ]
    },
    plugins: [
        new VueLoaderPlugin()
    ],
    resolve: {extensions: ["*", ".js", ".vue"]},
    output: {
        path: path.resolve(__dirname, "src/main/resources/static/dist/"),
        publicPath: "/static/dist/",
        filename: "[name]-bundle.js",
        libraryTarget: "umd",
    },
    optimization: {
        minimizer: [
            new UglifyJsPlugin({
                uglifyOptions: {
                    cache: true,
                    unused: false
                }
            }),
        ],
    },
    externals: {
        jquery: 'jQuery',
        jsbarcode: 'JsBarcode'
    },
    devServer: {
        contentBase: [path.join(__dirname, 'src/main/resources/static/'), path.join(__dirname, "temp/")],
        publicPath: '/static/dist/',
        compress: true,
        port: 9000
    }
};