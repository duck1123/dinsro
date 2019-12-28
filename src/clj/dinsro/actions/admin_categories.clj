(ns dinsro.actions.admin-categories
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.model.categories :as m.categories]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.actions.categories :as s.a.categories]
            [dinsro.specs :as ds]
            [dinsro.utils :as utils]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::s.categories/name})

(defn-spec prepare-record (s/nilable ::s.categories/params)
  [params :create-category/params]
  (let [user-id (utils/get-as-int params :user-id)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::s.categories/user :db/id] user-id))]
    (if (s/valid? ::s.categories/params params)
      params
      (do
        #_(timbre/warnf "not valid: %s" (expound/expound-str ::s.categories/params params))
          nil))))

(defn-spec create-handler ::s.a.categories/create-handler-response
  [{:keys [params session]} ::s.a.categories/create-handler-request]
  (or
   ;; User id should come from session
   (let [user-id 1]
        (when-let [params (prepare-record params)]
          (let [id (m.categories/create-record params #_(assoc params :user-id user-id))]
            (http/ok {:item (m.categories/read-record id)}))))
      (http/bad-request {:status :invalid})))

(defn index-handler
  [request]
  (let [categories (m.categories/index-records)]
    (http/ok {:items categories})))

(defn-spec read-handler ::s.a.categories/read-handler-response
  [{{:keys [id]} :path-params} ::s.a.categories/read-handler-request]
  (if-let [category (m.categories/read-record (utils/try-parse-int id))]
    (http/ok category)
    (http/not-found {:status :not-found})))

(defn-spec delete-handler ::s.a.categories/delete-handler-response
  [{{:keys [id]} :path-params} ::s.a.categories/delete-handler-request]
  (if-let [id (utils/try-parse-int id)]
    (let [response (m.categories/delete-record id)]
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))

(comment
  (create-handler {})
  (prepare-record {::s.categories/name "foo"})
  (delete-handler {:path-params {:id "s"}})
  )
