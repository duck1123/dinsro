(ns dinsro.joins.core.blocks-test
  (:require
   [dinsro.joins.core.blocks :as j.c.blocks]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :refer [inspect]]))

(dc/defcard index-card [] [inspect j.c.blocks/index])
