(ns dinsro.ui.controls
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
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
         user-id     ::m.users/id} value]
    (dom/div {}
      (or
       (when account-id (u.links/ui-account-link {::m.accounts/id account-id}))
       (when category-id (u.links/ui-category-link {::m.categories/id category-id}))
       (when currency-id (u.links/ui-currency-link {::m.currencies/id currency-id}))
       (when user-id (u.links/ui-user-link {::m.users/id user-id}))
       (dom/div (merge env {}) "link control")))))

(def render-link-control (render-field-factory link-control))

(defn ref-control
  [{:keys [value] :as env} _attribute]
  (dom/div (merge env {}) (str "ref control" value)))

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
      (control-type :ref  :ln-tx-row u.ln-tx/render-ref-ln-tx-row)
      (control-type :uuid :default   render-uuid)
      (control-type :date :default   render-date)))
