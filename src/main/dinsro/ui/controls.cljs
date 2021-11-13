(ns dinsro.ui.controls
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.ln-transactions :as u.ln-tx]
   [taoensso.timbre :as log]))

(defn link-control
  [{:keys [value] :as env} _attribute]
  (let [{account-id  ::m.accounts/id
         category-id ::m.categories/id
         currency-id ::m.currencies/id
         node-id     ::m.ln-nodes/id
         source-id   ::m.rate-sources/id
         transaction-id   ::m.transactions/id
         user-id     ::m.users/id} (log/spy :info value)]
    (or
     (when account-id
       (log/info "Account:" account-id)
       (u.links/ui-account-link value))
     (when category-id (u.links/ui-category-link value))
     (when currency-id (u.links/ui-currency-link value))
     (when node-id (u.links/ui-node-link value))
     (when source-id (u.links/ui-rate-source-link value))
     (when transaction-id (u.links/ui-transaction-link value))
     (when user-id (u.links/ui-user-link value))
     (dom/div (merge env {}) (str "link control: " value)))))

(def render-link-control (render-field-factory link-control))

(defn link-list-control
  [{:keys [value]} attribute]
  (dom/div {}
    (dom/div {} "link list control2: ")
    (for [item value]
      (dom/div {}
        (dom/div {} (pr-str item))
        (link-control {:value item} attribute)))))

(def render-link-list-control (render-field-factory link-list-control))

(defn link-subform-control
  [{:keys [value]} attribute]
  (dom/div {}
    (dom/ul {}
      (for [item value]
        (dom/li {}
          (link-control {:value item} attribute))))))

(def render-link-subform-control (render-field-factory link-subform-control))

(defn ref-control
  [{:keys [value]} _attribute]
  (dom/div :.ui.container
    (dom/div {} "default ref control: ")
    (dom/pre {}
             (dom/code {} (pr-str value)))))

(def render-ref (render-field-factory ref-control))

(defn date-control
  [{:keys [value] :as _env} _attribute]
  (dom/div {} (str value)))

(def render-date (render-field-factory date-control))

(defn uuid-control
  [{:keys [value]} _attribute]
  (dom/div {} (str "uuid control" value)))

(def render-uuid (render-field-factory uuid-control))

(defn control-type
  [controls type style control]
  (assoc-in controls [::form/type->style->control type style] control))

(defn all-controls
  []
  (-> sui/all-controls
      (control-type :ref  :default   render-ref)
      (control-type :ref  :link      render-link-control)
      (control-type :ref  :link-list render-link-list-control)
      (control-type :ref  :link-subform render-link-subform-control)
      (control-type :ref  :ln-tx-row u.ln-tx/render-ref-ln-tx-row)
      (control-type :uuid :default   render-uuid)
      (control-type :date :default   render-date)))
