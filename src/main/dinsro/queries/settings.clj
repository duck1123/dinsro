(ns dinsro.queries.settings
  (:require
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defn get-settings
  []
  {;; Enable Registration if there are no users
   :allow-registration (not (seq (q.users/index-ids)))

   :first-run (not (seq (q.users/index-ids)))})
