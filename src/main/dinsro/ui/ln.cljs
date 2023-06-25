(ns dinsro.ui.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.ln.accounts :as u.ln.accounts]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.ln.remote-nodes :as u.ln.remote-nodes]))

(def index-page-key :ln)

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

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/router      {}}
   :query         [::m.navlinks/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div {:classes [:.nostr-page router-wrapper]}
      (ui-router router))))
