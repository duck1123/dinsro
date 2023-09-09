(ns dinsro.ui.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.contacts :as j.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations.contacts :as mu.contacts]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.contacts :as u.f.contacts]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/contacts.cljc]]
;; [[../model/contacts.cljc]]
;; [[../mutations/contacts.cljc]]
;; [[../processors/contacts.clj]]

(def index-page-id :contacts)
(def model-key ::m.contacts/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :contacts-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.contacts/delete!))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.contacts/NewContactForm))})

(report/defsc-report Report
  [this _props]
  {ro/column-formatters {::m.contacts/name            #(u.links/ui-contacts-link %3)
                         ::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.contacts/name
                         m.contacts/pubkey]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control
                         ::new     new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.contacts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.contacts/index
   ro/title             "Contacts"}
  (ui-container {}
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.contacts/keys [id name]
          :as               props}]
  {:ident         ::m.contacts/id
   :initial-state {::m.contacts/id   nil
                   ::m.contacts/name ""}
   :pre-merge     (u.loader/page-merger model-key {})
   :query         [::m.contacts/id
                   ::m.contacts/name]}
  (if id
    (ui-container {}
      (ui-segment {}
        (str "foo" name)
        (ui-button {:onClick (fn [evt] (log/info :Show/click {:evt evt}))}
                   "Click")
        (u.debug/log-props props)))
    (u.debug/load-error props "show contacts")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["contacts"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-segment {}
      (dom/h1 {} "Contacts page"))
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["contact" :id]
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
   o.navlinks/label         "Show Contact"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
