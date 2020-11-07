(ns cljsjs.highlight
  (:require ["highlight.js" :as hljs]))

(js/goog.exportSymbol "highlight" hljs)
(js/goog.exportSymbol "DevcardsSyntaxHighlighter" hljs)
