(ns dinsro.processors.accounts
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.mutations :as mu]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.responses.accounts :as r.accounts]
   [lambdaisland.glogc :as log]))

;; [[../mutations/accounts.cljc]]
;; [[../queries/accounts.clj]]
;; [[../responses/accounts.cljc]]
;; [[../ui/accounts.cljc]]

(def model-key o.accounts/id)

(defn create!
  [env props]
  (log/info :create!/starting {:props props})
  (let [actor-id       (get-in env [:query-params :actor/id])
        currency-id    (get-in props [o.accounts/currency o.currencies/id])
        selected-props (select-keys props #{o.accounts/name
                                            o.accounts/initial-value})
        computed-props {o.accounts/user     actor-id
                        o.accounts/currency currency-id}
        account-props  (merge selected-props computed-props)]
    (if-let [id (q.accounts/create! account-props)]
      {mu/status                    :ok
       ::r.accounts/created-records (m.accounts/idents [id])}
      (mu/error-response "Failed to create"))))

(defn delete!
  [props]
  (log/info :delete!/starting {:props props})
  (let [id       (model-key props)
        response (q.accounts/delete! id)]
    (log/info :delete!/finished {:response response})
    {mu/status                    :ok
     ::r.accounts/deleted-records (m.accounts/idents [id])}))
