module.exports = function (config) {
  config.set({
    browsers: ["Chrome_without_security"],
    // The directory where the output file lives
    basePath: "target",
    // The file itself
    files: ["ci.js"],
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
