^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.instances-notebook
  (:require
   [dinsro.model.instances :as m.instances]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.instances :as q.instances]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Instances

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; [[../../../main/dinsro/actions/instances.clj]]
;; [[../../../main/dinsro/model/instances.cljc]]
;; [[../../../main/dinsro/mutations/instances.cljc]]
;; [[../../../main/dinsro/queries/instances.clj]]

(ds/gen-key ::m.instances/item)

(comment

  (q.instances/index-ids)

  nil)
