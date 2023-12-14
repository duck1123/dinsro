(ns dinsro.ui.admin.core.words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.core.words :as o.c.words]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.admin.core.words :as u.r.a.c.words]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/words.cljc]]
;; [[../../../model/core/words.cljc]]

(def index-page-id :admin-core-words)
(def model-key ::m.c.words/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-words-show)

(defsc Show
  [_this {::m.c.words/keys [id word]
          :as              props}]
  {:ident         ::m.c.words/id
   :initial-state (fn [props]
                    {model-key      (model-key props)
                     o.c.words/word ""})
   :pre-merge     (u.loader/page-merger model-key {})
   :query         (fn []
                    [o.c.words/id
                     o.c.words/word])}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "word")
          (dom/dd {} (str word)))))
    (u.debug/load-error props "admin show word record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % u.r.a.c.words/Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state u.r.a.c.words/Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query u.r.a.c.words/Report)}])
   :route-segment     ["words"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (u.r.a.c.words/ui-report report)))

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
   :route-segment ["word" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Words"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Word"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
