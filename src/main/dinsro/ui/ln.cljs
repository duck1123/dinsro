(ns dinsro.ui.ln
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.ln.channels :as u.ln.channels]
   [dinsro.ui.ln.invoices :as u.ln.invoices]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [dinsro.ui.ln.payments :as u.ln.payments]
   [dinsro.ui.ln.payreqs :as u.ln.payreqs]
   [dinsro.ui.ln.peers :as u.ln.peers]
   [dinsro.ui.ln.remote-nodes :as u.ln.remote-nodes]
   [dinsro.ui.ln.transactions :as u.ln.tx]))

(defrouter LnRouter
  [_this _props]
  {:router-targets [u.ln.channels/LNChannelForm
                    u.ln.channels/LNChannelsReport
                    u.ln.invoices/LNInvoiceForm
                    u.ln.invoices/LNInvoicesReport
                    u.ln.invoices/NewInvoiceForm
                    u.ln.nodes/CreateLightningNodeForm
                    u.ln.nodes/LightningNodeForm
                    u.ln.nodes/LightningNodesReport
                    u.ln.nodes/ShowNode
                    u.ln.payments/LNPaymentForm
                    u.ln.payments/LNPaymentsReport
                    u.ln.payreqs/NewPaymentForm
                    u.ln.payreqs/LNPaymentForm
                    u.ln.payreqs/LNPayreqsReport
                    u.ln.payreqs/NewPaymentForm
                    u.ln.peers/LNPeerForm
                    u.ln.peers/LNPeersReport
                    u.ln.peers/NewPeerForm
                    u.ln.remote-nodes/ShowRemoteNode
                    u.ln.remote-nodes/RemoteNodeForm
                    u.ln.remote-nodes/RemoteNodesReport
                    u.ln.tx/LNTransactionForm
                    u.ln.tx/LNTransactionsReport]}

  (dom/div {} "Ln router"))

(def ui-ln-router (comp/factory LnRouter))

(defsc LnPage
  [_this {:keys [ln-router]}]
  {:query         [{:ln-router (comp/get-query LnRouter)}]
   :initial-state {:ln-router {}}
   :ident         (fn [] [:component/id ::LnPage])
   :route-segment ["ln"]}
  (ui-ln-router ln-router))
