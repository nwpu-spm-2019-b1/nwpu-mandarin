const path = require("path");
const webpack = require("webpack");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = {
    entry: {
        common: "./src/main/resources/ui/js/common.js",
        librarian: "./src/main/resources/ui/js/librarian.js"
    },
    mode: "development",
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /(node_modules|bower_components)/,
                loader: "babel-loader",
                options: {presets: ["@babel/env"]}
            }
        ]
    },
    resolve: {extensions: ["*", ".js", ".jsx"]},
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
        "react": "React",
        "react-dom": "ReactDOM"
    },
    devServer: {
        contentBase: [path.join(__dirname, 'src/main/resources/static/'), path.join(__dirname, "temp/")],
        publicPath: '/static/dist/',
        compress: true,
        port: 9000
    }
};