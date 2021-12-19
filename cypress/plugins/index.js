/// <reference types="cypress" />
// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)

const extensionLoader = require("@poziworld/cypress-browser-extension-plugin/loader");
const makeCljsPreprocessor = require("cypress-clojurescript-preprocessor");

/**
 * @type {Cypress.PluginConfig}
 */
module.exports = (on, config) => {
  on(
    "before:browser:launch",
    extensionLoader.load({
      source: "./cypress/plugins/Fulcro-Inspect.zip",
      skipHooks: true,
    })
  );
  on("file:preprocessor", makeCljsPreprocessor(config));
  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
};
