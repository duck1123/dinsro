(ns user
  (:require
   [clojure.string :as string]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.components.database-queries :as dq]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.users :as q.users]
   [mount.core :as mount]
   [nextjournal.clerk :as clerk]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server.runtime]
   [taoensso.timbre :as log]))

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn cljs-repl
  ([]
   (cljs-repl :main))
  ([build-id]
   ;; (server/start!)
   ;; (shadow/watch build-id)
   (loop []
     (println "Trying to connect")
     (when (nil? @@(jit shadow.cljs.devtools.server.runtime/instance-ref))
       (Thread/sleep 1000)
       (recur)))
   ((jit shadow.cljs.devtools.api/nrepl-select) build-id)))

(defn start-clerk!
  []
  (clerk/serve!
   {:watch-paths    ["notebooks" "src"]
    :show-filter-fn #(string/starts-with? % "notebooks")}))

(comment
  (mocks/mock-account)

  (:main c.xtdb/xtdb-nodes)
  (start-clerk!)

  (clerk/show! "notebooks/notebooks/test.clj")

  (def db (xt/db (:main c.xtdb/xtdb-nodes)))

  (mount/running-states)

  (q.accounts/index-ids)
  (q.accounts/index-records)

  (q.categories/index-ids)

  (q.users/index-records)

  (xt/q db '{:find [?uuid] :where [[?uuid ::m.accounts/id ?id]]})

  (xt/q
   db
   '{:find [?uuid ?name]
     :where [[?uuid ::m.navlink/id ?id]
             [?uuid ::m.navlink/name ?name]
             [?uuid ::m.navlink/href ?href]]})

  (xt/q
   db
   '{:find  [(pull ?uuid [*])]
     :where [[?uuid ::m.navlink/id ?id]
             [?uuid ::m.navlink/name ?name]
             [?uuid ::m.navlink/href ?href]]})

  (xt/q
   db
   '{:find  [(pull ?uuid [*])]
     :in [[?id ...]]
     :where [[?uuid ::m.navlink/id ?id]
             [?uuid ::m.navlink/name ?name]
             [?uuid ::m.navlink/href ?href]]}
   ["admin"
    "login"])

  (dq/get-navlinks- db ["admin" "login"])

  (let [id nil]
    (q.categories/find-eid-by-id id))

  (dq/get-all-categories- db)
  (dq/get-all-currencies- db)
  (dq/get-all-navlinks- db)
  (dq/get-navlinks- db ["admin"])

  (xt/submit-tx
   (:main c.xtdb/xtdb-nodes)
   [[::xt/put {:xt/id      :a
               ::m.navlink/id   :a
               ::m.navlink/name "A"}]]))
