(ns dinsro.mutations
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

(s/def ::message string?)
(s/def ::data map?)
(s/def ::errors (s/keys :req-un [::message ::data]))
(s/def ::status #{:error :ok :initial :fail})

(defsc ErrorData
  [_ _]
  {:query         [:message :data]
   :initial-state {:message :data}})

(defn error-response
  [message]
  {::status :error
   ::errors {:message message :data {}}})

(defn exception-response
  [^Throwable ex]
  (error-response (str ex)))
