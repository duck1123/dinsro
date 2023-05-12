(ns dinsro.actions.categories
  (:require
   [dinsro.queries.categories :as q.categories]
   [lambdaisland.glogc :as log]))

;; [../queries/categories.clj]

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.categories/delete! id))

(comment

  (q.categories/count-ids)
  (q.categories/index-ids)

  nil)
