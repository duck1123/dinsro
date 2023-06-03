(ns dinsro.client
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.networking.http-remote :as net]
   [com.fulcrologic.fulcro.networking.websocket-remote :as fws]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.application :as rad-app]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.routing.history :as history]
   [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
   [dinsro.components.logging :as c.logging]
   [dinsro.components.routing :as c.routing]
   [dinsro.formatters.date-time :as fmt.date-time]
   [dinsro.ui :as ui]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.errors :as u.errors]
   [dinsro.ui.login :as u.login]
   [lambdaisland.glogi :as log]))

(def secured-request-middleware
  ;; The CSRF token is embedded via server_components/html.clj
  (->
   (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
   (net/wrap-fulcro-request)))

(def secured-request-ws-params
  ;; The CSRF token is embedded via server_components/html.clj
  {:csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!")})

(defonce app
  (rad-app/fulcro-rad-app
   {:client-did-mount    (fn [app] (c.routing/restore-route-ensuring-leaf! app))
    :global-error-action u.errors/global-error-action
    :props-middleware    (comp/wrap-update-extra-props (fn [cls extra-props] (merge extra-props (css/get-classnames cls))))
    :remotes             (do
                           (comment :ws-remote (fws/fulcro-websocket-remote {:uri "/api2"}))
                           {:remote (net/fulcro-http-remote {:url "/api" :request-middleware secured-request-middleware})})
    :remote-error?       (fn [result] (or (app/default-remote-error? result) (u.errors/contains-error? result)))}))

(defn setup-RAD [app]
  (let [all-controls (u.controls/all-controls)]
    (log/fine :controls/installing {:all-controls all-controls})
    (rad-app/install-ui-controls! app all-controls))
  (report/install-formatter! app :inst :default fmt.date-time/date-formatter)
  (report/install-formatter! app :boolean :affirmation (fn [_ value] (if value "yes" "no"))))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (c.logging/install-logging!)
  (log/info :app/starting {})
  (app/set-root! app ui/Root {:initialize-state? true})
  (dr/change-route! app [""])
  (history/install-route-history! app (html5-history))
  (setup-RAD app)

  (set! eb/*render-error* (fn [this error]
                            (println this)
                            (println error)
                            (dom/div "Error")))

  (auth/start! app [u.login/Page] {:after-session-check `c.routing/fix-route})
  (app/mount! app ui/Root "app" {:initialize-state? false})
  (log/info :client/loaded {}))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  (log/info :client/refreshing {})
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (setup-RAD app)
  (log/debug :client/refreshed {}))
