(ns dinsro.client
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as m]
   [com.fulcrologic.fulcro.networking.http-remote :as net]
   [com.fulcrologic.fulcro.networking.websocket-remote :as fws]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.application :as rad-app]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.routing :as routing]
   [com.fulcrologic.rad.routing.history :as history]
   [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
   [dinsro.formatters.date-time :as fmt.date-time]
   [dinsro.ui :as ui]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.nostr.dashboard :as u.a.n.dashboard]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.core.networks :as u.c.networks]
   [dinsro.ui.core.networks.addresses :as u.c.n.addresses]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.login :as u.login]
   [lambdaisland.glogi :as log]
   [lambdaisland.glogi.console :as glogi-console]
   [taoensso.timbre :as timbre]))

(defn target-component-requests-errors
  [query path]
  ;; path can be a single keyword -> ignore
  (some->> (when (vector? path) (butlast path))
           (get-in query)
           meta
           :component
           (comp/get-query)
           (some #{:com.wsscode.pathom.core/errors})))

(defn extract-query-from-transaction
  "Extract the component query from a `result`.
  Ex. tx.: `[({:all-organizations [:orgnr ...]} params) ::p/errors]`,
  `[{:people [:orgnr ...]} ::p/errors]`"
  [original-transaction]
  (let [query (first original-transaction)]
    (cond-> query
      ;; A parametrized query is wrapped in (..) but we need the raw data query itself
      (list? query) (first))))

(defn unhandled-errors
  "Returns Pathom errors (if any) that are not handled by the target component

  The argument is the same one as supplied to Fulcro's `remote-error?`"
  [result]
  ;; TODO Handle RAD reports - their query is `{:some/global-resolver ..}` and it lacks any metadata
  (let [load-errs     (:com.wsscode.pathom.core/errors (:body result))
        query         (extract-query-from-transaction (:original-transaction result))]
    (log/trace :unhandled-errors/query {:query query})
    (let [mutation-sym  (as-> (-> query keys first) x
                          (when (sequential? x) (first x))
                          (when (symbol? x)
                            (log/trace :unhandled-errors/symbol {:x x :result result :load-errs load-errs :query query})
                            x)) ; join query => keyword
          mutation-errs (when mutation-sym
                          (log/trace :unhandled-errors/mutation-errors {:mutation-sym mutation-sym})
                          (get-in result [:body mutation-sym :com.fulcrologic.rad.pathom/errors]))]
      (cond
        (seq load-errs)
        (reduce
         (fn [unhandled-errs [path :as entry]]
           (if (target-component-requests-errors query path)
             (do
               (log/trace :unhandled-errors/ignored {:last-path (last path)})
               unhandled-errs)
             (conj unhandled-errs entry)))
         {}
         ;; errors is a map of `path` to error details
         load-errs)

        mutation-errs
        (do
          (log/trace :unhandled-errors/mutation-errors {:mutation-errs mutation-errs})
          mutation-errs)

        :else
        nil))))

(defn contains-error? [result]
  (seq (unhandled-errors result)))

(defn component-handles-mutation-errors? [component]
  (boolean (some-> component comp/get-query set ::m/mutation-error)))

(defn global-error-action
  "Run when app's :remote-error? returns true"
  [{:keys [component state], {:keys [body status-code error-text]} :result}]
  (log/error :global-error-action/starting
             {:component   component
              :state       state
              :body        body
              :status-code status-code
              :error-text  error-text})
  (when-not (component-handles-mutation-errors? component)
    (let [msg (first
               (map
                (fn [body]
                  (let [pathom-errs (:com.fulcrologic.rad.pathom/errors body)]
                    (cond
                      (and (string? error-text) status-code (> status-code 299))
                      (cond-> error-text (and status-code (> status-code 299)) (str " - " body))

                      pathom-errs
                      (->> pathom-errs
                           (map (fn [[query {{:keys [message data]} :com.fulcrologic.rad.pathom/errors :as val}]]
                                  (str query " failed with "
                                       (or (and message (str message (when (seq data) (str ", extra data: " data)))) val))))
                           (string/join " | "))

                      :else
                      (str body))))
                (vals body)))]
      (swap! state assoc :ui/global-error msg))))

(defn restore-route-ensuring-leaf!
  "Attempt to restore the route given in the URL. If that fails, simply route to the default given (a class and map).
   WARNING: This should not be called until the HTML5 history is installed in your app.
   (Based on `hist5/restore-route!` modified to check for Partial Routes and routing to the correct leaf target.)

   NOTE: Fulcro dyn. routing requires that you always route to a leaf target, i.e. not just to a router somewhere in
   the middle of your UI tree with some unrouted, descendant routers - otherwise weird stuff may happen."
  [app]
  (let [{:keys [route params]} (hist5/url->route)
        target0                (dr/resolve-target app route)
        target                 (condp = target0
                                 nil               u.home/Page
                                 u.admin/AdminPage u.a.users/Report
                                 u.a.nostr/Page    u.a.n.dashboard/Dashboard
                                 u.c.networks/Show u.c.n.addresses/SubPage
                                 target0)]
    (log/info :restore-route-ensuring-leaf!/routing {:target0 target0 :params params})
    (routing/route-to! app target (or params {}))))

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
   {:client-did-mount    (fn [app] (restore-route-ensuring-leaf! app))
    :global-error-action global-error-action
    :props-middleware    (comp/wrap-update-extra-props (fn [cls extra-props] (merge extra-props (css/get-classnames cls))))
    :remotes             (do
                           (comment :ws-remote (fws/fulcro-websocket-remote {:uri "/api2"}))
                           {:remote (net/fulcro-http-remote {:url "/api" :request-middleware secured-request-middleware})})
    :remote-error?       (fn [result] (or (app/default-remote-error? result) (contains-error? result)))}))

(m/defmutation fix-route
  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
  [_]
  (action [{:keys [app]}]
    (let [logged-in (auth/verified-authorities app)]
      (if (empty? logged-in)
        (hist5/restore-route! app u.home/Page {})
        (hist5/restore-route! app u.home/Page {})))))

(defn setup-RAD [app]
  (let [all-controls (u.controls/all-controls)]
    (log/fine :controls/installing {:all-controls all-controls})
    (rad-app/install-ui-controls! app all-controls))
  (report/install-formatter! app :inst :default fmt.date-time/date-formatter)
  (report/install-formatter! app :boolean :affirmation (fn [_ value] (if value "yes" "no"))))

(def my-auth-machine
  (-> auth/auth-machine
      (assoc-in [::uism/states :state/gathering-credentials ::uism/events :event/cancel]
                {::uism/target-state :state/idle})
      (assoc-in [::uism/states :state/idle ::uism/events :event/cancel]
                {::uism/target-state :state/idle})))

(uism/register-state-machine! `auth/auth-machine my-auth-machine)

(defn install-logging!
  []
  (glogi-console/install!)
  (timbre/merge-config! {:level     :warn
                         :min-level :warn
                         :output-fn prefix-output-fn
                         :appenders {:console (console-appender)}})
  (log/debug :logging/initialized {}))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (install-logging!)
  (log/info :app/starting {})
  (app/set-root! app ui/Root {:initialize-state? true})
  (dr/change-route! app [""])
  (history/install-route-history! app (html5-history))
  (setup-RAD app)

  (set! eb/*render-error* (fn [this error]
                            (println this)
                            (println error)
                            (dom/div "Error")))

  (auth/start! app [u.login/LoginPage] {:after-session-check `fix-route})
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
