(ns dinsro.processors.accounts
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.mutations :as mu]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.responses.accounts :as r.accounts]
   [lambdaisland.glogc :as log]))

;; [[../mutations/accounts.cljc]]
;; [[../queries/accounts.clj]]
;; [[../responses/accounts.cljc]]

(defn create!
  [props]
  (log/info :create!/starting {:props props})
  (let [id (q.accounts/create! props)]
    {::mu/status                  :ok
     ::r.accounts/created-records (m.accounts/idents [id])}))

(>defn delete!
  [props]
  [::r.accounts/delete!-request => ::r.accounts/delete!-response]
  (let [{::m.accounts/keys [id]} props]
    (log/info :delete!/starting {})
    (let [response (q.accounts/delete! id)]
      (log/info :delete!/finished {:response response})
      {::mu/status       :ok
       ::r.accounts/deleted-records [id]})))
