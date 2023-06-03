^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.transactions-notebook
  (:require
   [dinsro.model.transactions :as m.transactions]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]
   [tick.alpha.api :as t]))

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

^{::clerk/viewer clerk/table}
(when-let [ids (q.transactions/index-ids)] (map q.transactions/read-record ids))

(ds/->inst "2021-11-07T13:05:16")

(t/now)

;; ## create record

(comment

  (q.transactions/create-record
   {::m.transactions/description "test"
    ::m.transactions/date        (t/now)})

  nil)
