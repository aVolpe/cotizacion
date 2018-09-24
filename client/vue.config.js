const SitemapPlugin = require("sitemap-webpack-plugin").default;
const PrerenderSPAPlugin = require('prerender-spa-plugin');
const path = require('path');
const Renderer = PrerenderSPAPlugin.PuppeteerRenderer



const today = new Date().toISOString().replace(/T.*/, "");

const paths = [
  {path: "/", lastMod: today, priority: "0.8", changeFreq: "hourly"},
  {path: "/#/licenses", lastMod: today, priority: "0.1", changeFreq: "monthly"},
  {path: "/#/swagger", lastMod: today, priority: "0.1", changeFreq: "monthly"}
];

module.exports = {
  configureWebpack: {
    plugins: [
      new SitemapPlugin("https://cotizaciones.volpe.com.py", paths),
      new PrerenderSPAPlugin({
        staticDir: path.join(__dirname, 'dist'),
        routes: [ '/' ],
        server: {
          port: 8001
        },
        //renderer: new Renderer({
          //renderAfterDocumentEvent: 'render-event'
        //})
      })

    ]
  }
};

