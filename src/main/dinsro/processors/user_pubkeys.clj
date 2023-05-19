(ns dinsro.processors.user-pubkeys
  (:require
   [dinsro.actions.user-pubkeys :as a.user-pubkeys]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.mutations :as mu]
   [dinsro.responses.user-pubkeys :as r.user-pubkeys]
   [lambdaisland.glogc :as log]))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (try
    (if-let [currency-id (::m.user-pubkeys/id props)]
      (do
        (a.user-pubkeys/delete! currency-id)
        {::mu/status                    :ok
         ::r.user-pubkeys/deleted-records [(m.user-pubkeys/ident currency-id)]})
      (mu/error-response "No ID"))
    (catch Exception ex (mu/exception-response ex))))