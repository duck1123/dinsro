(ns dinsro.ui.core.words
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.words :as m.c.words]))

(report/defsc-report WordReport
  [_this _props]
  {ro/columns          [m.c.words/word
                        m.c.words/position]
   ro/control-layout   {:action-buttons [::new]}
   ro/route            "words"
   ro/row-pk           m.c.words/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.words/index
   ro/title            "Word Report"})