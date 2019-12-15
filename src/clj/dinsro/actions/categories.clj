(ns dinsro.actions.categories
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.model.categories :as m.categories]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.actions.categories :as s.a.categories]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::s.categories/name
   :initial-value ::s.categories/initial-value})

(defn try-parse
  [v]
  (try (Integer/parseInt v) (catch NumberFormatException e nil)))

(defn get-as-int
  [params key]
  (try
    (some-> params key str Integer/parseInt)
    (catch NumberFormatException e nil)))

(defn-spec prepare-record (s/nilable ::s.categories/params)
  [params :create-category/params]
  (let [currency-id (get-as-int params :currency-id)
        user-id (get-as-int params :user-id)
        initial-value (some-> params :initial-value double)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::s.categories/initial-value] initial-value)
                   (assoc-in [::s.categories/currency :db/id] currency-id)
                   (assoc-in [::s.categories/user :db/id] user-id))]
    (if (s/valid? ::s.categories/params params)
      params
      (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.categories/params params))
          nil))))

(defn-spec create-handler ::s.a.categories/create-handler-response
  [{:keys [params session]} ::s.a.categories/create-handler-request]
  (or (let [user-id 1]
        (when-let [params (prepare-record params)]
          (let [id (m.categories/create-record params #_(assoc params :user-id user-id))]
            (http/ok {:item (m.categories/read-record id)}))))
      (http/bad-request {:status :invalid})))

(defn index-handler
  [request]
  (let [categories (m.categories/index-records)]
    (http/ok {:items categories})))

(defn read-handler
  [{{:keys [categorieId]} :path-params}]
  (if-let [categorie (m.categories/read-record {:id categorieId})]
    (http/ok categorie)
    (http/not-found {})))

(defn-spec delete-handler ::s.a.categories/delete-handler-response
  [{{:keys [id]} :path-params} ::s.a.categories/delete-handler-request]
  (if-let [id (try-parse id)]
    (let [response (m.categories/delete-record id)]
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))

(comment
  (create-handler {})
  (prepare-record {::s.categories/name "foo"})
  (prepare-record (gen/generate (s/gen ::s.a.categories/create-handler-request-valid)))
  (delete-handler {:path-params {:id "s"}})
  )
