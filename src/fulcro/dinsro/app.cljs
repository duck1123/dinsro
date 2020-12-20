(ns dinsro.app
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.networking.http-remote :as http]
   [dinsro.router :as router]
   [dinsro.ui :as u :refer [Root]]
   [dinsro.ui.debug-menu :as u.debug-menu]
   [dinsro.ui.navbar :as u.navbar]
   [taoensso.timbre :as timbre]))

(defonce app
  (app/fulcro-app
   {:client-did-mount
    (fn [app]
      (js/console.log "Mounted")
      (comment (df/load! app :root/navbar u.navbar/Navbar))
      (df/load! app :debug-menu/list u.debug-menu/DebugLinkButton
                {:target [:root/debug-link-bar :all-debug-menus]}))
    :remotes
    {:remote
     (http/fulcro-http-remote
      {:url "/pathom"
       :request-middleware
       (comp (http/wrap-fulcro-request)
             (http/wrap-csrf-token js/csrfToken))})}
    :remote-error? (fn [result]
                     (let [{:keys [status-code]} result]
                       (when-not (= status-code 200)
                         (timbre/errorf "Error: %s" result)
                         (js/console.log result))))}))

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/set-root! app Root {:initialize-state? true})
  (dr/initialize! app)
  ;; TODO: parse from url
  (dr/change-route-relative! app router/RootRouter [""])
  (app/mount! app Root "app" {:initialize-state? false}))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))
