(ns dinsro.spec.events.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            ;; [dinsro.components :as c]
            ;; [dinsro.components.debug :as c.debug]
            ;; [dinsro.events.accounts :as e.accounts]
            ;; [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            ;; [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            #_[re-frame.core :as rf]
            #_[reframe-utils.core :as rfu]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::name string?)
(def name ::name)

(s/def ::initial-value number?)
(def initial-value ::initial-value)

(s/def ::currency-id ds/id-string)
(def currency-id ::currency-id)

(s/def ::user-id ds/id-string)
(def user-id ::user-id)

(s/def ::create-handler-request (s/keys :req-un [::s.accounts/name]))
(s/def ::form-bindings (s/cat
                        :name ::name
                        :initial-value ::initial-value
                        :currency-id ::currency-id
                        :user-id ::user-id))
