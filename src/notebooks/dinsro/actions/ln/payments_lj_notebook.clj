^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.payments-lj-notebook
  (:require
   [clojure.core.async :as async :refer [<! <!!]]
   [dinsro.actions.ln.payments-lj :as a.ln.payments-lj]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Payment Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (a.ln.payments-lj/update-payments n.lnd/node)

  (a.ln.payments-lj/fetch-payments n.lnd/node)

  (<!! (a.ln.payments-lj/fetch-payments n.lnd/node))

  (let [chan (a.ln.payments-lj/fetch-payments n.lnd/node)]
    (async/go-loop []
      (let [a (<! chan)] (when a a (recur)))))

  (let [ch (async/chan 1)]
    (async/pipeline
     1
     ch
     (map (fn [z] z))
     (a.ln.payments-lj/fetch-payments n.lnd/node)
     true
     (fn [error] (println "ahhh: " (.getMessage error)))))

  nil)
