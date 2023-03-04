^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.contacts
  (:require
   [dinsro.model.contacts :as m.contacts]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Contacts

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## Generated item

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.contacts/item)
