^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.transactions-lj-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.actions.ln.transactions-lj :as a.ln.transactions-lj]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Transaction Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (a.ln.transactions-lj/get-transactions n.lnd/node)

  nil)
