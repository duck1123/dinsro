(ns dinsro.ui.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.ui.links :as u.links]))

(defn delete-action
  [report-instance {::m.n.events/keys [id]}]
  (form/delete! report-instance ::m.n.events/id id))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.n.events/id]
   fo/cancel-route ["events"]
   fo/id           m.n.events/id
   fo/route-prefix "new-event"
   fo/title        "Event"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Event"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.events/pubkey
                        m.n.events/content
                        m.n.events/created-at]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                        ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/route            "events"
   ro/row-pk           m.n.events/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.events/index
   ro/title            "Events Report"})

(defsc Show
  [_this _props]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id nil}
   :query         [::m.n.events/id]
   :route-segment ["relay" :id]
   :will-enter    (partial u.links/page-loader ::m.n.events/id ::Show)})
