const SitemapPlugin = require("sitemap-webpack-plugin").default;
const PrerenderSPAPlugin = require("prerender-spa-plugin");
const path = require("path");
const Renderer = PrerenderSPAPlugin.PuppeteerRenderer;



const today = new Date().toISOString().replace(/T.*/, "");

const paths = [
  {path: "/", lastMod: today, priority: "0.8", changeFreq: "hourly"},
  {path: "/#/?moneda=USD", lastMod: today, priority: "0.8", changeFreq: "hourly"},
  {path: "/#/?moneda=EUR", lastMod: today, priority: "0.8", changeFreq: "hourly"},
  {path: "/#/licenses", lastMod: today, priority: "0.1", changeFreq: "monthly"},
  {path: "/#/swagger", lastMod: today, priority: "0.1", changeFreq: "monthly"}
];

module.exports = {
  configureWebpack: {
    plugins: [
      new SitemapPlugin("https://cotizaciones.volpe.com.py", paths),
      new PrerenderSPAPlugin({
        staticDir: path.join(__dirname, "dist"),
        routes: [ "/" ],
        server: {
          port: 8001
        },
        //renderer: new Renderer({
          //renderAfterDocumentEvent: "render-event"
        //})
      })

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

