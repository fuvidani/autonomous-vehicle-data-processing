const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = [
  {
    test: /\.jsx?$/,
    exclude: /(node_modules|bower_components|public\/)/,
    loader: "babel-loader",
		query: {
			presets: ["react", "es2015", "stage-0"]
    }
  },
  {
    // Rules for SCSS files. Loaders run in reverse order
    test: /\.(scss)$/,
    use: [
      MiniCssExtractPlugin.loader,

      {
        loader: "css-loader"
      }, {
        loader: "sass-loader",
        options: {
          sourceMap: true,
        }
        }],
  },
  {
    test: /\.css$/,
    loaders: ['style-loader', 'css-loader?importLoaders=1'],
    exclude: ['node_modules']
  },
  {
    test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
    exclude: /(node_modules|bower_components)/,
    loader: "file-loader"
  },
  {
    test: /\.(woff|woff2)$/,
    exclude: /(node_modules|bower_components)/,
    loader: "url-loader?prefix=font/&limit=5000"
  },
  {
    test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
    exclude: /(node_modules|bower_components)/,
    loader: "url-loader?limit=10000&mimetype=application/octet-stream"
  },
	{
		test: /\.(eot|woff|woff2|ttf|svg)$/,
		exclude: /(node_modules|bower_components)/,
		loader: 'url-loader?limit=30000&name=[name]-[hash].[ext]'
	},
  {
    test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
    exclude: /(node_modules|bower_components)/,
    loader: "url-loader?limit=10000&mimetype=image/svg+xml"
  },
  {
    test: /\.gif/,
    exclude: /(node_modules|bower_components)/,
    loader: "url-loader?limit=10000&mimetype=image/gif"
  },
	{
		test: /\.(png|jpg|ico)$/,
		loader: 'url-loader?limit=25000'
	}
];
