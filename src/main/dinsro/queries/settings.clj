(ns dinsro.queries.settings
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.queries.users :as q.users]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>def ::settings (s/keys))

(defn find-by-key
  [key]
  (let [db (c.xtdb/get-db)]
    (log/debug :record/find-by-key {:key key})
    (let [query        '{:find  [(pull ?sid [*])]
                         :in    [?key]
                         :where [[?sid ::key ?key]]}
          raw-response (xt/q db query key)
          response     (ffirst raw-response)]
      (log/finest :record/find-by-key-response
                  {:db           db
                   :key          key
                   :raw-response raw-response
                   :response     response})
      response)))

(defn get-setting
  [k]
  (log/debug :setting/get {:key k})
  (let [value (::value (find-by-key k))]
    (log/trace :setting/get-response {:key k :value value})
    value))

(defn set-setting
  [k v]
  (let [node (c.xtdb/get-node)]
    (if-let [existing-setting (find-by-key k)]
      (let [id     (:xt/id existing-setting)
            params (assoc existing-setting ::value v)]
        (log/debug :setting/updated {:key k :value v :id id})
        (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
        id)
      (let [id     (new-uuid)
            params {::key   k
                    ::value v
                    :xt/id  id
                    ::id    id}]
        (log/debug :setting/created {:key k :value v})
        (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
        id))))

(>defn get-settings
  []
  [=> ::settings]
  {;; Enable Registration if there are no users
   :allow-registration (not (seq (q.users/index-ids)))
   :first-run          (not (seq (q.users/index-ids)))})
