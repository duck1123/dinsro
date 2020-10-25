(ns ^:figwheel-no-load dinsro.app
  (:require
   [cljs.repl :as repl]
   [cljs.spec.alpha :as s]
   [devtools.core :as devtools]
   [dinsro.core :as core]
   [expound.alpha :as expound]
   [mount.core :as mount]
   [orchestra-cljs.spec.test :as stest]
   [taoensso.timbre :as timbre]))

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" sym "\""))))

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



(timbre/merge-config!
 {:min-level [[#{"dinsro.store.mock"} :info]
              :debug]})

(mount/defstate instrument
  :start (stest/instrument))

(defn init
  []
  (timbre/info "app init")
  (core/init! true))
