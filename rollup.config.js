import { writeFileSync } from "fs";
import autoprefixer from "autoprefixer";
import postcss from "rollup-plugin-postcss";
import { Minifier } from "css-loader-minify-class";
// import less from "rollup-plugin-less";
import { nodeResolve } from "@rollup/plugin-node-resolve";

const isProduction = !process.env.ROLLUP_WATCH;

const minifier = new Minifier({ prefix: "_" }).getLocalIdent;
const minifyClass = (localName) =>
  minifier({ resourcePath: "" }, null, localName);

const generateStylesCljs = (_, json) => {
  const lines = ["(ns dinsro.styles)\n"];
  for (let [key, value] of Object.entries(json)) {
    lines.push(`(def ${key} "${value}")`);
  }
  writeFileSync("src/main/dinsro/styles.cljs", lines.join("\n"));
  console.log("=== Created styles.cljs ===");
};

export default [
  {
    input: "src/styles/main.js",
    output: {
      file: "resources/main/public/css/semantic.min.js",
      format: "es",
    },
    plugins: [
      nodeResolve(),
      // less(),
      postcss({
        // minimize: true,
        modules: {
          generateScopedName: isProduction
            ? minifyClass
            : "[local]-[hash:base64:5]",
          getJSON: generateStylesCljs,
        },
        plugins: [autoprefixer],
        use: {
          sass: null,
          stylus: null,
          less: { javascriptEnabled: true },
        },
        extract: true,
      }),
    ],
  },
];
