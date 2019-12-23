(ns dinsro.events.forms.create-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-name "Offshore")
(def default-initial-value 1.0)

(s/def ::name string?)
(s/def ::currency-id string?)
(s/def ::user-id string?)
(s/def ::shown? boolean?)
(s/def ::initial-value string?)
