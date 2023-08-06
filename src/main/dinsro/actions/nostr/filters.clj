(ns dinsro.actions.nostr.filters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.filter-items :as a.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.queries.nostr.filter-items :as q.n.filter-items]
   [dinsro.queries.nostr.filters :as q.n.filters]
   [lambdaisland.glogc :as log]))

;; [[../../processors/nostr/filters.clj]]
;; [[../../ui/admin/nostr/filters.cljc]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/filters_notebooks.clj]]

(defn add-filter!
  [request-id]
  (log/info :add-filter!/starting {:request-id request-id})
  (let [n (q.n.filters/get-greatest-index request-id)]
    (q.n.filters/create-record
     {::m.n.filters/index   (inc n)
      ::m.n.filters/request request-id})))

(defn register-filter!
  [request-id]
  (add-filter! request-id))

(>defn get-query-string
  [filter-id]
  [::m.n.filters/id => (s/keys)]
  (log/info :get-query-string-filter/starting {:filter-id filter-id})
  (let [data (->> filter-id
                  q.n.filter-items/find-by-filter
                  (map a.n.filter-items/get-query-string)
                  (reduce
                   (fn [val item]
                     (log/info :get-query-string-item/reducing {:val val :item item})
                     (let [{:keys [pubkey event kind]} item]
                       {:ids     (:ids val)
                        :authors (concat (:authors val) (if pubkey [pubkey] []))
                        :kinds   (concat (:kinds val) (if kind [kind] []))
                        :#e      (concat (:#e val) (if event [event] []))
                        :#p      (:#p val)}))
                   {:ids [] :authors [] :kinds [] :#e [] :#p []})
                  (map (fn [[k v]] (when (seq v) [k v])))
                  (filter identity)
                  (into {}))]
    (log/info :get-query-string-filter/finished {:data data})
    data))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.filters/delete! id))
