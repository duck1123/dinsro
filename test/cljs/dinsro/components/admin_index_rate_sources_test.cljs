(ns dinsro.components.admin-index-rate-sources-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.admin-index-rate-sources :as c.admin-index-rate-sources]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.translations :refer [tr]]))

(cards/header "Admin Index Rate Source Components" [])

(defcard-rg c.admin-index-rate-sources/form
  "**Admin Index Rate Sources**"
  (fn []
    [error-boundary
     [c.admin-index-rate-sources/section]]))
