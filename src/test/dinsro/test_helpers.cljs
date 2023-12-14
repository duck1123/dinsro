(ns dinsro.test-helpers
  (:require
   [dinsro.client :as client]
   [lambdaisland.glogc :as log]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3])
  (:require-macros [dinsro.test-helpers]))

(defn fulcro-card
  ([root state]
   (fulcro-card root state {}))
  ([root state opts]
   (merge
    (ct.fulcro3/fulcro-card
     {::ct.fulcro3/root root
      ::ct.fulcro3/app
      {:client-will-mount client/setup-RAD
       :submit-transaction!
       (fn [app tx]
         (log/info :submit-transaction!/creating {:app app :tx tx}))}
      ::ct.fulcro3/initial-state state})
    opts)))
