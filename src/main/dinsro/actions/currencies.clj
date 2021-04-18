(ns dinsro.actions.currencies
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs.actions.currencies :as s.a.currencies]
   [expound.alpha :as expound]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name ::m.currencies/name})

(>defn prepare-record
  [params]
  [::s.a.currencies/create-params => (? ::m.currencies/params)]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (if (s/valid? ::m.currencies/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::m.currencies/params params)))
        nil))))

(>defn create!
  [params]
  [::s.a.currencies/create-params => (? ::m.currencies/item)]
  (if-let [eid (q.currencies/create-record params)]
    (q.currencies/read-record eid)
    (do
      (timbre/warn "failed to create")
      nil)))

(>defn create-handler
  [{:keys [params]}]
  [::s.a.currencies/create-request => ::s.a.currencies/create-response]
  (or (when-let [item (some-> params prepare-record create!)]
        (http/ok {:item item}))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.currencies/read-request => ::s.a.currencies/read-response]
  (let [id (some-> request :path-params :id Integer/parseInt)]
    (if-let [item (q.currencies/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))))

(>defn delete-handler
  [request]
  [::s.a.currencies/delete-request => ::s.a.currencies/delete-response]
  (let [{{:keys [id]} :path-params} request]
    (or (try
          (let [id (Integer/parseInt id)]
            (q.currencies/delete-record id)
            (http/ok {:id id}))
          (catch NumberFormatException _ nil))
        (http/bad-request {:status :invalid}))))

(>defn index-handler
  [_]
  [::s.a.currencies/index-request => ::s.a.currencies/index-response]
  (let [items (q.currencies/index-records)]
    (http/ok {:items items})))

(>defn index-by-account-handler
  [_]
  [::s.a.currencies/index-request => ::s.a.currencies/index-response]
  (let [items (q.currencies/index-records)]
    (http/ok {:items items})))

(>defn index-by-user-handler
  [_]
  [::s.a.currencies/index-request => ::s.a.currencies/index-response]
  (let [items (q.currencies/index-records)]
    (http/ok {:items items})))
