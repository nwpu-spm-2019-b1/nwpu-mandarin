const path = require("path");
const webpack = require("webpack");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = {
    entry: {
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
        // require("jquery") is external and available
        //  on the global var jQuery
        "react": "React",
        "react-dom": "ReactDOM"
    }
};