(ns dinsro.model.settings
  (:require
   [dinsro.model.users :as m.users]))

(defn get-settings
  []
  ;; Enable Registration if there are no users
  {:allow-registration (not (empty? (m.users/index-ids)))})
