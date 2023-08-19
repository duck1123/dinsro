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
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(def index-page-key :admin-contacts)
(def model-key ::m.contacts/id)
(def override-form true)
(def show-page-key :admin-contacts-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.categories/delete!))

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.contacts/name m.contacts/user]
   fo/cancel-route  ["admin"]
   fo/field-options {::m.contacts/user u.pickers/admin-user-picker}
   fo/field-styles  {::m.contacts/user :pick-one}
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
  {ro/column-formatters {::m.contacts/name #(u.links/ui-admin-category-link %3)
                         ::m.contacts/user #(u.links/ui-admin-user-link %2)}
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
   :initial-state  {::m.contacts/id   nil
                    ::m.contacts/name ""}
   ::m.navlinks/id :show-category
   :pre-merge      (u.loader/page-merger model-key {})
   :query          [::m.contacts/id
                    ::m.contacts/name]}
  (ui-container {}
    (ui-segment {}
      (str name))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [_props]
                        {::m.navlinks/id index-page-key
                         :ui/report      {}})
   :query             (fn [_props]
                        [::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["contacts"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/info :Page/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "admin index categories page"))))

(defsc ShowPage
  [_this {::m.categories/keys [id]
          ::m.navlinks/keys [target]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.categories/id nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.categories/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["node" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin show category")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Contacts"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin page for contacts"
   ::m.navlinks/label         "Show Contact"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
