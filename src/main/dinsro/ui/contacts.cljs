(ns dinsro.ui.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.contacts :as j.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/contacts.cljc]]
;; [[../model/contacts.cljc]]

(def index-page-key :contacts)
(def model-key ::m.contacts/id)
(def show-page-key :contacts-show)

(form/defsc-form NewContactForm
  [_this _props]
  {fo/attributes   [m.contacts/name
                    m.contacts/pubkey]
   fo/cancel-route ["contacts"]
   fo/id           m.contacts/id
   fo/route-prefix "new-contact"
   fo/title        "Edit Contact"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewContactForm))})

(report/defsc-report Report
  [this _props]
  {ro/column-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.contacts/name
                         m.contacts/pubkey]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control
                         ::new     new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.contacts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.contacts/index
   ro/title             "Contacts"}
  (dom/div :.ui.container.centered
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.contacts/keys [name]}]
  {:ident         ::m.contacts/id
   :initial-state {::m.contacts/id   nil
                   ::m.contacts/name ""}
   :pre-merge     (u.loader/page-merger model-key {})
   :query         [::m.contacts/id
                   ::m.contacts/name]}
  (dom/div :.ui.container
    (ui-segment {}
      (str name))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["contacts"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-segment {}
      (dom/h1 {} "Contacts page"))
    (ui-report report)))

(defsc ShowPage
  [_this {::m.contacts/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.contacts/id     nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.contacts/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["contact" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {} "Failed to load record")))
