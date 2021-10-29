(ns dinsro.ui.ln-channels
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln-channels :as m.ln-channels]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(form/defsc-form LNChannelForm [_this _props]
  {fo/id           m.ln-channels/id
   fo/attributes   [m.ln-channels/id
                    m.ln-channels/channel-point]
   fo/route-prefix "ln-channels"
   fo/title        "Lightning Channels"})

(report/defsc-report LNChannelsReport
  [this _props]
  {ro/columns          [m.ln-channels/id
                        m.ln-channels/channel-point]
   ro/links            {::m.ln-channels/id (fn [this props]
                                             (let [{::m.ln-channels/keys [id]} props]
                                               (form/view! this LNChannelForm id)))}
   ro/route            "ln-channels"
   ro/row-actions      []
   ro/row-pk           m.ln-channels/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln-channels/all-channelss
   ro/title            "Lightning Channels Report"}
  (dom/div {}
    (dom/h1 {} "Channels")
    (report/render-layout this)))
