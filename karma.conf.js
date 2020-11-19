/*jshint esversion: 6*/

function mergeFilesWithArgv(staticFiles) {
  const source = staticFiles;
  const { argv } = process;

  argv.forEach((arg) => {
    const index = arg.indexOf("--check=");
    if (index !== -1) {
      source.push(arg.substring(8));
    }
  });

  console.log("source", source);
  return source;
}

module.exports = function (config) {
  config.set({
    browsers: ["Chrome_without_security"],
    // The directory where the output file lives
    basePath: "target",
    // The file itself
    files: mergeFilesWithArgv([]),
    frameworks: ["cljs-test"],
    plugins: ["karma-cljs-test", "karma-chrome-launcher"],
    colors: true,
    logLevel: config.LOG_INFO,
    client: {
      args: ["shadow.test.karma.init"],
      singleRun: true,
    },
    // you can define custom flags
    customLaunchers: {
      Chrome_without_security: {
        base: "ChromeHeadless",
        flags: ["--disable-web-security", "--no-sandbox"],
      },
    },
  });
};
