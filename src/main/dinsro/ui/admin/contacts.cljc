(ns dinsro.ui.admin.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.options.categories :as o.categories]
   [dinsro.options.contacts :as o.contacts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(def index-page-id :admin-contacts)
(def model-key o.contacts/id)
(def override-form true)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-contacts-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.categories/delete!))

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.contacts/name m.contacts/user]
   fo/cancel-route  ["admin"]
   fo/field-options {o.contacts/user u.pickers/admin-user-picker}
   fo/field-styles  {o.contacts/user :pick-one}
   fo/id            m.contacts/id
   fo/route-prefix  "new-contact"
   fo/title         "Contact"}
  (if override-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Contact"))))

(def new-button
  {:label  "New Contact"
   :type   :button
   :action #(form/create! % NewForm)})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.contacts/name #(u.links/ui-admin-category-link %3)
                         o.contacts/user #(u.links/ui-admin-user-link %2)}
   ro/columns           [m.contacts/name
                         m.contacts/user]
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/admin-index
   ro/title             "Categories"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.contacts/keys [name]}]
  {:ident          ::m.contacts/id
   :initial-state  (fn [props]
                     {model-key (model-key props)
                      o.contacts/name ""})
   ::m.navlinks/id :show-category
   :pre-merge      (u.loader/page-merger model-key {})
   :query          (fn []
                     [o.contacts/id
                      o.contacts/name])}
  (ui-container {}
    (ui-segment {}
      (str name))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["contacts"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "admin index categories page"))))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.categories/id
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["node" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Contacts"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin page for contacts"
   o.navlinks/label         "Show Contact"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
