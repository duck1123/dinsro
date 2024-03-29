(ns dinsro.components.parser
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.blob :as blob]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.pathom :as pathom]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [com.wsscode.pathom.connect :as pc]
   [com.wsscode.pathom.core :as p]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.components.auto-resolvers :refer [automatic-resolvers]]
   [dinsro.components.blob-store :as bs]
   [dinsro.components.config :as c.config]
   [dinsro.components.delete-middleware :as delete]
   [dinsro.components.save-middleware :as save]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model :refer [all-attributes all-resolvers]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]))

(def default-timezone "America/Detroit")

(pc/defresolver index-explorer [{::pc/keys [indexes]} _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (p/transduce-maps
    (remove (comp #{::pc/resolve ::pc/mutate} key))
    indexes)})

(def use-taps false)

(defn blob-store-plugin
  []
  (blob/pathom-plugin
   @bs/temporary-blob-store
   {:files         @bs/file-blob-store
    :avatar-images @bs/image-blob-store}))

(defn timezone-plugin
  []
  {::p/wrap-parser
   (fn transform-parser-out-plugin-external [wrapped-parser]
     (fn transform-parser-out-plugin-internal [env tx]
       ;; TASK: This should be taken from account-based setting
       (dt/with-timezone default-timezone
         (if (and (map? env) (seq tx))
           (wrapped-parser env tx)
           {}))))})

(defn auth-user-plugin
  "Pathom plugin. Adds :actor/id to query params"
  []
  {::p/wrap-parser
   (fn transform-parser-out-plugin-external [wrapped-parser]
     (log/info :transform-parser-out-plugin-external/starting {})
     (fn transform-parser-out-plugin-internal [env tx]
       (if (and (map? env) (seq tx))
         (let [actor-id (a.authentication/get-user-id env)
               env     (assoc-in env [:query-params :actor/id] actor-id)]
           (wrapped-parser env tx))
         {})))})

(defn tap-plugin
  []
  {::p/wrap-parser
   (fn tap-parser-out-plugin-external [wrapped-parser]
     (fn tap-parser-out-plugin-internal [env tx]
       (when use-taps (tap> tx))
       (wrapped-parser env tx)))})

(defn start-parser!
  []
  (log/info :start-parser!/starting {})
  (let [config    (c.config/get-config)
        node      (c.xtdb/get-node)
        plugins   [(attr/pathom-plugin all-attributes)
                   (form/pathom-plugin save/middleware delete/middleware)
                   (xt/pathom-plugin (fn [_env] {:production node}))
                   (blob-store-plugin)
                   (timezone-plugin)
                   (auth-user-plugin)
                   (tap-plugin)]
        resolvers [@automatic-resolvers
                   form/resolvers
                   (blob/resolvers all-attributes)
                   all-resolvers
                   index-explorer]]
    (log/trace :start-parser!/config {:plugins plugins :resolvers resolvers})
    (pathom/new-parser config plugins resolvers)))

(defn stop-parser!
  []
  (log/info :stop-parser!/stopping {})
  nil)

(defstate parser
  :start (start-parser!)
  :stop (stop-parser!))
