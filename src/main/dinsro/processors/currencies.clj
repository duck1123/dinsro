(ns dinsro.processors.currencies
  (:require
   [dinsro.actions.currencies :as a.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]
   [dinsro.responses.currencies :as r.currencies]
   [lambdaisland.glogc :as log]))

;; [../actions/currencies.clj]
;; [../model/currencies.cljc]
;; [../mutations/currencies.cljc]
;; [../responses/currencies.cljc]

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (try
    (if-let [currency-id (::m.currencies/id props)]
      (do
        (a.currencies/delete! currency-id)
        {::mu/status                    :ok
         ::r.currencies/deleted-records [(m.currencies/ident currency-id)]})
      (mu/error-response "No ID"))
    (catch Exception ex (mu/exception-response ex))))
