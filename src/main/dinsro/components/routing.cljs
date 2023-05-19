(ns dinsro.components.routing
  (:require
   [com.fulcrologic.fulcro.mutations :as m]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.routing :as routing]
   [com.fulcrologic.rad.routing.html5-history :as hist5]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.nostr.dashboard :as u.a.n.dashboard]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.core.networks :as u.c.networks]
   [dinsro.ui.core.networks.addresses :as u.c.n.addresses]
   [dinsro.ui.home :as u.home]
   [lambdaisland.glogi :as log]))

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

(m/defmutation fix-route
  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
  [_]
  (action [{:keys [app]}]
    (let [logged-in (auth/verified-authorities app)]
      (if (empty? logged-in)
        (hist5/restore-route! app u.home/Page {})
        (hist5/restore-route! app u.home/Page {})))))
