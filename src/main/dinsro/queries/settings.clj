(ns dinsro.queries.settings
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.queries.users :as q.users]
   [xtdb.api :as xt]))

(>def ::settings (s/keys))

(defn get-setting-record
  [k]
  (let [db    (c.xtdb/main-db)
        query '{:find     [(pull ?sid [*])]
                :in       [?key]
                :where    [[?sid ::key ?key]
                           [?sid ::value ?value]]}]
    (ffirst (xt/q db query k))))

(defn get-setting
  [k]
  (::value (get-setting-record k)))

(defn set-setting
  [k v]
  (if-let [setting-id (get-setting-record k)]
    (throw (RuntimeException. (str "Setting already exists: " setting-id)))
    (let [node   (c.xtdb/main-node)
          id     (new-uuid)
          params {::key k
                  ::value v}
          params (assoc params :xt/id id)
          params (assoc params ::id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)))

(comment

  (get-setting :foo)
  (get-setting-record :foo)
  (set-setting :foo "bar")

  nil)

(>defn get-settings
  []
  [=> ::settings]
  {;; Enable Registration if there are no users
   :allow-registration (not (seq (q.users/index-ids)))

   :first-run (not (seq (q.users/index-ids)))})
