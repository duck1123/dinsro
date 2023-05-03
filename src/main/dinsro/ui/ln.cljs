(ns dinsro.ui.ln
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.ln.accounts :as u.ln.accounts]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.ln.remote-nodes :as u.ln.remote-nodes]))

(defrouter Router
  [_this _props]
  {:router-targets [u.ln.accounts/Report
                    u.ln.channels/NewForm
                    u.ln.channels/Report
                    u.ln.channels/Show
                    u.ln.invoices/NewForm
                    u.ln.invoices/Report
                    u.ln.invoices/Show
                    u.ln.nodes/NewForm
                    u.ln.nodes/Report
                    u.ln.nodes/Show
                    u.ln.remote-nodes/Show
                    u.ln.remote-nodes/Report]}
  (dom/div {} "Ln router"))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state {:ui/router {}}
   :query         [{:ui/router (comp/get-query Router)}]
   :route-segment ["ln"]}
  (let [{:keys [router-wrapper]} (css/get-classnames Page)]
    (dom/div {:classes [:.nostr-page router-wrapper]}
      ((comp/factory Router) router))))
