// var tsImportPluginFactory = require('ts-import-plugin')
//
// module.exports = {
//   configureWebpack: {
//     module: {
//       rules: [
//         {
//           test: /\.(jsx|tsx|js|ts)$/,
//           loader: "ts-loader",
//           options: {
//             transpileOnly: true,
//             getCustomTransformers: function () {
//               console.log('asdfasfsadfasfasdfasdfasdf');
//               return {
//                 before: [tsImportPluginFactory({
//                   libraryDirectory: "es5/components/",
//                   libraryName: "vuetify"
//                 })]
//               }
//             },
//             compilerOptions: {
//               module: "esnext"
//             }
//           },
//           exclude: /node_modules/
//         }
//       ]
//     },
//   }
// }
