(ns dinsro.model.settings
  (:require
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as timbre]))

(defn get-settings
  []
  {
   ;; Enable Registration if there are no users
   :allow-registration (not (seq (m.users/index-ids)))

   :first-run (not (seq (m.users/index-ids)))
   })
