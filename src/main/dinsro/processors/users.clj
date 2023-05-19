(ns dinsro.processors.users
  (:require
   [dinsro.actions.users :as a.users]
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]
   [dinsro.responses.users :as r.users]
   [lambdaisland.glogc :as log]))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (try
    (if-let [currency-id (::m.users/id props)]
      (do
        (a.users/delete! currency-id)
        {::mu/status                    :ok
         ::r.users/deleted-records [(m.users/ident currency-id)]})
      (mu/error-response "No ID"))
    (catch Exception ex (mu/exception-response ex))))