(ns dinsro.queries.settings
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(>def ::settings (s/keys))

(>defn get-settings
  []
  [=> ::settings]
  {;; Enable Registration if there are no users
   :allow-registration (not (seq (q.users/index-ids)))

   :first-run (not (seq (q.users/index-ids)))})
