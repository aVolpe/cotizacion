const SitemapPlugin = require('sitemap-webpack-plugin').default;

const today = new Date().toISOString().replace(/T.*/, '')

const paths = [
  {
    path: '/', lastMod: today, priority: '0.8', changeFreq: 'hourly',
    path: '/#/licenses', lastMod: today, priority: '0.2', changeFreq: 'monthly',
    path: '/#/swagger', lastMod: today, priority: '0.2', changeFreq: 'monthly',
  }
];

module.exports = {
  configureWebpack: {
    plugins: [
      new SitemapPlugin('https://cotizaciones.volpe.com.py', paths)
    ]
  }
}
