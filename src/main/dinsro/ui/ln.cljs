(ns dinsro.ui.ln
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.ln.accounts :as u.ln.accounts]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.ln.payments :as u.ln.payments]
   [dinsro.ui.ln.payreqs :as u.ln.payreqs]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [dinsro.ui.ln.remote-nodes :as u.ln.remote-nodes]))

(defrouter LnRouter
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
                    u.ln.payments/Report
                    u.ln.payments/Show
                    u.ln.payreqs/NewForm
                    u.ln.payreqs/Report
                    u.ln.payreqs/NewForm
                    u.ln.peers/Report
                    u.ln.peers/NewForm
                    u.ln.remote-nodes/Show
                    u.ln.remote-nodes/Report]}

  (dom/div {} "Ln router"))

(def ui-ln-router (comp/factory LnRouter))

(defsc LnPage
  [_this {:keys [ln-router]}]
  {:query         [{:ln-router (comp/get-query LnRouter)}]
   :initial-state {:ln-router {}}
   :ident         (fn [] [:component/id ::LnPage])
   :route-segment ["ln"]}
  (ui-ln-router ln-router))
