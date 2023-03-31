(ns dinsro.ui.admin.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/picture
                        m.n.pubkeys/name
                        j.n.pubkeys/contact-count
                        j.n.pubkeys/event-count]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.pubkeys/hex     #(u.links/ui-pubkey-link %3)
                        ::m.n.pubkeys/name    #(u.links/ui-pubkey-name-link %3)}
   ro/route            "pubkeys"
   ro/row-actions      [(u.links/row-action-button "Add to contacts" ::m.n.pubkeys/id mu.n.pubkeys/add-contact!)
                        (u.links/row-action-button "Fetch" ::m.n.pubkeys/id mu.n.pubkeys/fetch!)
                        (u.links/row-action-button "Fetch Contacts" ::m.n.pubkeys/id mu.n.pubkeys/fetch-contacts!)]
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.pubkeys/index
   ro/title            "Pubkeys"})
