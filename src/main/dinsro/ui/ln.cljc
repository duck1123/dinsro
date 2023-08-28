(ns dinsro.ui.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.ln.accounts :as u.ln.accounts]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.ln.remote-nodes :as u.ln.remote-nodes]))

(def index-page-id :ln)
(def parent-router-id :root)
(def required-role :guest)

(defrouter Router
  [_this _props]
  {:router-targets [u.ln.accounts/IndexPage
                    u.ln.channels/NewForm
                    u.ln.channels/IndexPage
                    u.ln.channels/ShowPage
                    u.ln.invoices/NewForm
                    u.ln.invoices/IndexPage
                    u.ln.invoices/ShowPage
                    u.ln.nodes/NewForm
                    u.ln.nodes/IndexPage
                    u.ln.nodes/ShowPage
                    u.ln.remote-nodes/ShowPage
                    u.ln.remote-nodes/IndexPage]}
  (dom/div {} "Ln router"))

(def ui-router (comp/factory Router))

(defsc IndexPage
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/router      {}}
   :query         [::m.navlinks/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames IndexPage)]
    (dom/div {:classes [:.nostr-page router-wrapper]}
      (ui-router router))))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "LN Router"
   o.navlinks/navigate-key  u.ln.nodes/index-page-id
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
