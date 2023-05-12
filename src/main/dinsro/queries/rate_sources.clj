(ns dinsro.queries.rate-sources
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [xtdb.api :as xt]))

;; [../actions/rate_sources.clj]
;; [../model/rate_sources.cljc]

(def query-info
  {:ident   ::m.rate-sources/id
   :pk      '?rate-source-id
   :clauses [[::m.accounts/id       '?account-id]
             [::m.currencies/id     '?currency-id]
             [::m.rate-sources/name '?rate-source-name]]
   :rules
   (fn [[account-id currency-id rate-source-name] rules]
     (->> rules
          (concat-when account-id
            [['?rate-source-id ::m.rate-sources/account  '?account-id]])
          (concat-when currency-id
            [['?rate-source-id ::m.rate-sources/currency '?currency-id]])
          (concat-when rate-source-name
            [['?rate-source-id ::m.rate-sources/name     '?rate-source-name]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn find-by-name
  [name]
  [::m.rate-sources/name => ::m.rate-sources/id]
  (c.xtdb/query-value
   '{:find  [?id]
     :in    [[?name]]
     :where [[?id ::m.rate-sources/name ?name]]}
   [name]))

(>defn find-by-currency-and-name
  [currency-id name]
  [::m.rate-sources/currency ::m.rate-sources/name => (? ::m.rate-sources/id)]
  (c.xtdb/query-value
   '{:find  [?rate-source-id]
     :in    [[?currency-id ?name]]
     :where [[?rate-source-id ::m.rate-sources/currency ?currency-id]
             [?rate-source-id ::m.rate-sources/name ?name]]}
   [currency-id name]))

(>defn create-record
  [params]
  [::m.rate-sources/params => :xt/id]
  (let [node   (c.xtdb/get-node)
        id     (new-uuid)
        params (assoc params ::m.rate-sources/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.rate-sources/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.rate-sources/id)
      record)))

(>defn delete!
  [id]
  [:xt/id => any?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))))
