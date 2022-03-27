(ns dinsro.ui.core.tx-in
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [dinsro.model.core.tx-in :as m.core-tx-in]))

(defsc RefRow
  [_this {::m.core-tx-in/keys [coinbase
                               txinwitness
                               sequence]}]
  {}
  (dom/tr {}
    (dom/td (str coinbase))
    (dom/td (str txinwitness))
    (dom/td (str sequence))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.core-tx-in/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "coinbase")
         (dom/th {} "txinwitness")
         (dom/th {} "sequence")))

     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))
