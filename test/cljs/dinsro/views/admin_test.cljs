(ns dinsro.views.admin-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.views.admin :as v.admin]))

(defcard title
  "**Admin**")

(defcard-rg load-buttons
  [v.admin/load-buttons])

(defcard-rg users-section
  [v.admin/users-section])

(defcard-rg page
  [v.admin/page])
