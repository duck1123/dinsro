^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.instances-notebook
  (:require
   [dinsro.actions.current-instance :as a.current-instance]
   [dinsro.actions.instances :as a.instances]
   [dinsro.model.instances :as m.instances]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.instances :as q.instances]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]
   [tick.alpha.api :as t]))

;; # Instances

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; [[../../../main/dinsro/actions/instances.clj]]
;; [[../../../main/dinsro/joins/instances.cljc]]
;; [[../../../main/dinsro/model/instances.cljc]]
;; [[../../../main/dinsro/mutations/instances.cljc]]
;; [[../../../main/dinsro/queries/instances.clj]]
;; [[../../../main/dinsro/ui/admin/instances.cljc]]

(ds/gen-key ::m.instances/item)

a.current-instance/*current-instance-id*

(map q.instances/read-record (q.instances/index-ids))

(comment

  (a.instances/register!)

  (def id (first (q.instances/index-ids)))

  (t/>
   (t/instant #inst "2023-08-05T01:15:40-04:00")
   (t/<< (ds/->inst) (t/new-duration 16 :minutes)))

  (t/minute 30)

  (a.instances/beat! id)
  (q.instances/read-record id)

  (q.instances/expired? #inst "2023-08-05T09:59:00-04:00")

  (doseq [id (q.instances/index-ids)]
    (q.instances/delete! id))

  nil)
