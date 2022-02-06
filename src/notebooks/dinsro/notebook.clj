(ns ^{:nextjournal.clerk/no-cache true
      :nextjournal.clerk/visibility :fold-ns}
 dinsro.notebook
  (:require
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [dinsro.helm.bitcoind :as h.bitcoind]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
   [dinsro.notebook-utils :refer [display]]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.core-tx :as q.core-tx]
   [dinsro.queries.core-tx-in :as q.core-tx-in]
   [dinsro.queries.core-tx-out :as q.core-tx-out]
   [dinsro.queries.ln-nodes :as q.ln-nodes]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]))

(when-let [user-id (q.users/find-eid-by-name "bob")]
  (count (q.accounts/find-by-user user-id)))

(def matchers {:a 'a
               :b 'b
               :c 'c})

(defn get-matcher
  [value]
  (->> matchers
       (map (fn [[kw f]] (when (get value kw) f)))
       (filter identity)
       first))

(get-matcher {:a 1})

(comment
  ::io/_
  ::q.ln-nodes/_
  ::h.bitcoind/_
  ::s/_

  (clerk/show! "src/notebooks/dinsro/notebook.clj")
  ;; (clerk/code (sort (map str (keys (s/registry)))))

  (ds/gen-key ::m.users/item)

  (def options {:rpc {:user "foo" :password "bar"}})

  (h.bitcoind/merge-defaults options)

  (defn roll! []
    (tap> "Rolling")
    (clerk/show! "dinsro/notebook.clj"))

  (roll!)

  ^::clerk/no-cache
  (clerk/with-viewer
    (fn [_v] (v/html [:div.text-center [:button "Roll"]])) 7)

  (tap> (rand-int 1000000))

  (q.ln-nodes/index-records)

  ^:nextjournal.clerk/no-cache
  (display (slurp "lnd_notebook.yaml"))

  (clerk/code (json/read-json (slurp "tilt_config.json")))

  (clerk/html
   [:button {:on-click (fn [_e] (println "clicked"))} "click"])

  (ds/gen-key :xt/id)
  (ds/gen-key ::m.accounts/item)

  (q.core-tx/index-ids)
  (q.core-tx-in/index-ids)
  (q.core-tx-out/index-ids)

  (q.accounts/index-records)
  (count (q.accounts/index-ids))

  (clerk/html "Hello")
  (v/plotly
   {:data [{:z    [[1 2 3]
                   [3 2 1]]
            :type "surface"}]})

  nil)
