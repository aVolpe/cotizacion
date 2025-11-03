const SitemapPlugin = require("sitemap-webpack-plugin").default;
const path = require("path");



const today = new Date().toISOString().replace(/T.*/, "");

const paths = [
  {path: "/", lastMod: today, priority: 0.8, changeFreq: "hourly"},
  {path: "/#/?moneda=USD", lastMod: today, priority: 0.8, changeFreq: "hourly"},
  {path: "/#/?moneda=EUR", lastMod: today, priority: 0.8, changeFreq: "hourly"},
  {path: "/#/licenses", lastMod: today, priority: 0.1, changeFreq: "monthly"},
  {path: "/#/swagger", lastMod: today, priority: 0.1, changeFreq: "monthly"}
];

module.exports = {
  configureWebpack: {
    plugins: [
      new SitemapPlugin({base: "https://cotizaciones.volpe.com.py", paths})
      // Prerendering disabled - old prerender-spa-plugin incompatible with webpack 5
      // Can be re-enabled later with proper configuration if needed for SEO
    ]
  },
  pwa: {
    name: "Cotizaciones del Paraguay",
    themeColor: "#283593",
    msTileColor: "#283593",
    appleMobileWebAppCapable: "yes",
    appleMobileWebAppStatusBarStyle: "black",
  },
  chainWebpack: config => {
  config
    .plugin('html')
    .tap(args => {
      args[0].ssr = '{\n        exchanges: [(${exchanges})],\n        branches: [(${branches})],\n        currencies: [(${currencies})],\n        current: [(${current})]\n      }';
      return args;
    })
}
};

