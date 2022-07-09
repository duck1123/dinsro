^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.payments-notebook
  (:require
   [clojure.core.async :as async :refer [<! <!!]]
   [dinsro.actions.ln.payments :as a.ln.payments]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.payments :as q.ln.payments]
   [dinsro.queries.users :as q.users]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Payment Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (a.ln.payments/update-payments node)

  (a.ln.payments/fetch-payments node)

  (q.ln.payments/index-records)
  (q.ln.payments/index-ids)

  (map q.ln.payments/delete!
       (q.ln.payments/index-ids))

  (let [ch (async/chan 1)]
    (async/pipeline
     1
     ch
     (map (fn [z] z))
     (a.ln.payments/fetch-payments node)
     true
     (fn [error] (println "ahhh: " (.getMessage error)))))

  (<!! (a.ln.payments/fetch-payments node))

  (let [chan (a.ln.payments/fetch-payments node)]
    (async/go-loop []
      (let [a (<! chan)] (when a a (recur)))))

  nil)
