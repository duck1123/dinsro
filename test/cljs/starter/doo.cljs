(ns starter.doo
  (:require
   [cljs.repl :as repl]
   [cljs.spec.alpha :as s]
   [devtools.core :as devtools]
   [dinsro.core-test]
   [dinsro.components-test]
   [dinsro.events-test]
   [dinsro.events.forms-test]
   [dinsro.spec-test]
   [dinsro.spec.actions-test]
   [dinsro.views-test]
   [doo.runner :refer-macros [doo-all-tests]]
   [expound.alpha :as expound]
   [mount.core :as mount]
   [orchestra-cljs.spec.test :as stest]
   [taoensso.timbre :as timbre]))

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

(mount/defstate instrument
  :start (stest/instrument))

(doo-all-tests #"dinsro\..*(?:-test)$")
