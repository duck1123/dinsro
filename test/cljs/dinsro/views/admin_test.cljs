(ns dinsro.views.admin-test
  (:require [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.views.admin :as v.admin]))

(defcard "**Admin**")

(declare load-buttons)
(defcard-rg load-buttons
  [v.admin/load-buttons])

(declare users-section)
(defcard-rg users-section
  [v.admin/users-section])

(declare page)
(defcard-rg page
  [v.admin/page])
