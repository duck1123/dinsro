(ns ^:figwheel-no-load dinsro.app
  (:require
   [dinsro.core :as core]
   [clojure.spec.test.alpha :as stest]
   [cljs.repl :as repl]
   [cljs.spec.alpha :as s]
   [expound.alpha :as expound]
   [devtools.core :as devtools]))

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(extend-type ExceptionInfo
  IPrintWithWriter
  (-pr-writer [o writer opts]
    (-write writer (repl/error->str o))))

(set! s/*explain-out* expound/printer)

(defn error-handler [_message _url _line _column e]
  (js/console.error e)
  (print (repl/error->str e))
  true)

(set! (.-onerror js/window) error-handler)

(enable-console-print!)

(devtools/install!)

(core/init! true)

(stest/instrument)
