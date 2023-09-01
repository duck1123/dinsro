(ns dinsro.actions.current-instance)

(defonce ^:dynamic *current-instance-id* (atom nil))

(defn get-current
  []
  @*current-instance-id*)
