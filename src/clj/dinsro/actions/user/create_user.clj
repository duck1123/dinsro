(ns dinsro.actions.user.create-user
  (:require [clojure.spec.alpha :as s]
            [dinsro.model.user :as model.user]
            dinsro.specs
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn create-user-response
  [{:keys [registration-data] :as request}]
  {:pre [(s/valid? :dinsro.specs/register-request registration-data)]}
  (if (model.user/create-user! registration-data)
    (ok "ok")))

(s/fdef create-user-response
  :args (s/cat :data :dinsro.specs/register-request))
