^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.components.seed-notebook
  (:require
   [dinsro.components.seed :as c.seed]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; Seed Component

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/currencies)

^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/default-rate-sources)

;; ## users

^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/users)

;; ## seed-data

^{::clerk/viewer clerk/code}
(ds/gen-key ::c.seed/seed-data)
