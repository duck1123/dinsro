^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.payreqs-notebook
  (:require
   [dinsro.actions.ln.payreqs :as a.ln.payreqs]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.payreqs :as q.ln.payreqs]
   [dinsro.queries.users :as q.users]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Payment Request Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
(def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
(def node node-alice)

(def payment-request "lnbcrt230n1psuca02pp5lhjau56zdmmce9hva726lvqyxuztfsusyv7yw770mur5hu2x989qdq523jhxapqf9h8vmmfvdjscqzpgxqyz5vqsp5rxljvwf9r3q4zhecwr2ccnn3eakvs0n500t4p3hnrv626s8852ms9qyyssqpz8a9dnmhhk3q3ly8ykdnr3ts9m94v9e9un2j6rsvstqlhh0wdgrx84hawf2zagqs7ph24r4pqk0yy7jpmxay8f0kjxf6khkh2z9axspc25re5")

(comment
  node-alice
  node-bob
  node

  (q.ln.payreqs/index-ids)

  (map q.ln.payreqs/delete!   (q.ln.payreqs/index-ids))

  (a.ln.payreqs/decode {::m.ln.payreqs/payment-request payment-request
                        ::m.ln.payreqs/node            node-alice})

  nil)
