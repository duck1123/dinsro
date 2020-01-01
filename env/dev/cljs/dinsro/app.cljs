(ns ^:figwheel-no-load dinsro.app
  (:require [dinsro.core :as core]
            [clojure.spec.test.alpha :as stest]
            [cljs.spec.alpha :as s]
            [expound.alpha :as expound]
            [devtools.core :as devtools]))

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(set! s/*explain-out* expound/printer)

(enable-console-print!)

(devtools/install!)

(core/init! true)

(stest/instrument)
