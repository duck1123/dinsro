(ns dinsro.spec.events.forms.add-user-category
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::name string?)
(def name ::name)

(s/def ::user-id string?)
(def user-id ::user-id)
