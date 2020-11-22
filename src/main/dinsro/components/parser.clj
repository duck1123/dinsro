(ns dinsro.components.parser
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.blob :as blob]
   [com.fulcrologic.rad.database-adapters.datomic :as datomic]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.pathom :as pathom]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [com.wsscode.pathom.core :as p]
   [dinsro.components.auto-resolvers :refer [automatic-resolvers]]
   [dinsro.components.blob-store :as bs]
   [dinsro.components.config :refer [config]]
   [dinsro.components.datomic :refer [datomic-connections]]
   [dinsro.components.delete-middleware :as delete]
   [dinsro.components.save-middleware :as save]
   [dinsro.model :refer [all-attributes]]
   [dinsro.resolvers :as resolvers]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as timbre]))

(defstate parser
  :start
  (do
    (timbre/spy :info config)
    (pathom/new-parser
     config
     [(attr/pathom-plugin all-attributes)]
     [resolvers/resolvers])))

(comment

  (form/pathom-plugin save/middleware delete/middleware)
  (datomic/pathom-plugin (fn [_env] {:production (:main datomic-connections)}))
  (blob/pathom-plugin bs/temporary-blob-store {:files         bs/file-blob-store
                                               :avatar-images bs/image-blob-store})
  {::p/wrap-parser
   (fn transform-parser-out-plugin-external [parser]
     (fn transform-parser-out-plugin-internal [env tx]
       ;; TASK: This should be taken from account-based setting
       (dt/with-timezone "America/Los_Angeles"
         (if (and (map? env) (seq tx))
           (parser env tx)
           {}))))}

  automatic-resolvers
  (parser
   {}
   [{:all-lists [{:list/people [:person/name]}]}]))
