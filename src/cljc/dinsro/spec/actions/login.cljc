(ns dinsro.spec.actions.login
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [taoensso.timbre :as timbre]))


(s/def ::create-params-valid (s/keys))
