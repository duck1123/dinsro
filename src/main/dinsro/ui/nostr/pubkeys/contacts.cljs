(ns dinsro.ui.nostr.pubkeys.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/nostr/pubkey_contacts.cljc][Pubkey Contacts Joins]]
;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/picture
                        m.n.pubkeys/name
                        j.n.pubkeys/contact-count
                        j.n.pubkeys/event-count]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/field-formatters {::m.n.pubkeys/hex  #(u.links/ui-pubkey-link %3)
                        ::m.n.pubkeys/name #(u.links/ui-pubkey-name-link %3)
                        ::m.n.pubkeys/picture
                        (fn [_ picture]
                          (when picture
                            (dom/img {:src    (str picture)
                                      :width  100
                                      :height 100})))}
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.pubkeys/contacts
   ro/title            "Contacts"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["contacts"]}
  ((comp/factory Report) report))
