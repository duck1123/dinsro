(ns dinsro.spec.events.forms.create-category
  (:require [clojure.spec.alpha :as s]
            ;; [clojure.spec.gen.alpha :as gen]
            ;; [day8.re-frame.tracing :refer-macros [fn-traced]]
            ;; [dinsro.spec.categories :as s.categories]
            ;; [dinsro.translations :refer [tr]]
            ;; [kee-frame.core :as kf]
            ;; [orchestra.core :refer [defn-spec]]
            ;; [re-frame.core :as rf]
            ;; [reframe-utils.core :as rfu]
            ;; [taoensso.timbre :as timbre]

            ))

(s/def ::name string?)
(s/def ::user-id string?)
(s/def ::shown? boolean?)
