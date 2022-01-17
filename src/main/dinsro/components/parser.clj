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
   [dinsro.components.config :refer [config]]
   [dinsro.components.xtdb :refer [xtdb-nodes]]
   [dinsro.components.delete-middleware :as delete]
   [dinsro.components.save-middleware :as save]
   [dinsro.model :refer [all-attributes all-resolvers]]
   [dinsro.model.users :as m.users]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [taoensso.timbre :as log]))

(def default-timezone "America/Detroit")

(pc/defresolver index-explorer [{::pc/keys [indexes]} _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (p/transduce-maps
    (remove (comp #{::pc/resolve ::pc/mutate} key))
    indexes)})

(defstate parser
  :start
  (pathom/new-parser
   config
   [(attr/pathom-plugin all-attributes)
    (form/pathom-plugin save/middleware delete/middleware)
    (xt/pathom-plugin (fn [_env] {:production (:main xtdb-nodes)}))
    (blob/pathom-plugin bs/temporary-blob-store {:files         bs/file-blob-store
                                                 :avatar-images bs/image-blob-store})
    {::p/wrap-parser
     (fn transform-parser-out-plugin-external [wrapped-parser]
       (fn transform-parser-out-plugin-internal [env tx]
         (log/info "date parser")
           ;; TASK: This should be taken from account-based setting
         (dt/with-timezone default-timezone
           (if (and (map? env) (seq tx))
             (wrapped-parser env tx)
             {}))))}
    {::p/wrap-parser
     (fn transform-parser-out-plugin-external [wrapped-parser]
       (log/info "running auth processor plugin")
       (fn transform-parser-out-plugin-internal [env tx]
         (log/info "user parser")
         (if (and (map? env) (seq tx))
           (let [user-id (a.authentication/get-user-id env)
                 env (assoc env ::m.users/id user-id)]
             (wrapped-parser env tx))
           {})))}]
   [automatic-resolvers
    form/resolvers
    (blob/resolvers all-attributes)
    all-resolvers
    index-explorer]))
