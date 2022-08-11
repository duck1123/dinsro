^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.components.seed-notebook
  (:require
   [dinsro.specs :as ds]
   [dinsro.components.seed :as c.seed]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; Seed Component


^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/default-currencies)

^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/default-rate-sources)


;; ## users


^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/users)


;; ## seed-data


^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/seed-data)