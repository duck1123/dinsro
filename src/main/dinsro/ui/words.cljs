(ns dinsro.ui.words
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.words :as m.words]))

(report/defsc-report WordReport
  [_this _props]
  {ro/columns          [m.words/word
                        m.words/position]
   ro/control-layout   {:action-buttons [::new]}
   ro/route            "words"
   ro/row-pk           m.words/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.words/index
   ro/title            "Word Report"})
