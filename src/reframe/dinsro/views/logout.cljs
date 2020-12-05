(ns dinsro.views.logout
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.store :as st]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn page
  [_store _match])

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
