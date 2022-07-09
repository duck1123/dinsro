^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.payments-notebook
  (:require
   [clojure.core.async :as async :refer [<! <!!]]
   [dinsro.actions.ln.payments :as a.ln.payments]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.queries.ln.payments :as q.ln.payments]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Payment Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (a.ln.payments/update-payments n.lnd/node)

  (a.ln.payments/fetch-payments n.lnd/node)

  (q.ln.payments/index-records)
  (q.ln.payments/index-ids)

  (map q.ln.payments/delete!
       (q.ln.payments/index-ids))

  (let [ch (async/chan 1)]
    (async/pipeline
     1
     ch
     (map (fn [z] z))
     (a.ln.payments/fetch-payments n.lnd/node)
     true
     (fn [error] (println "ahhh: " (.getMessage error)))))

  (<!! (a.ln.payments/fetch-payments n.lnd/node))

  (let [chan (a.ln.payments/fetch-payments n.lnd/node)]
    (async/go-loop []
      (let [a (<! chan)] (when a a (recur)))))

  nil)
