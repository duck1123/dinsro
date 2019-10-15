(ns dinsro.actions.account
  (:require [clojure.spec.alpha :as s]
            [dinsro.model.account :as model.account]
            [dinsro.specs :as ds]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn create-account
  [{:keys [params]}]
  (model.account/create-account! (assoc params :user-id 1))
  (http/ok {:status "ok"}))
