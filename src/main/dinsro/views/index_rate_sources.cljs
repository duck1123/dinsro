(ns dinsro.views.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate-source :as u.f.create-rate-source]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexRateSourcesPage
  [this {::keys [toggle-button rate-sources form]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable form-toggle-sm
                  {:actor/navbar IndexRateSourcesPage})

     (df/load! this ::m.rate-sources/all-rate-sources u.index-rate-sources/IndexRateSourceLine
               {:target [:page/id
                         ::page
                         ::rate-sources
                         :dinsro.ui.index-rate-sources/items]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::form          {}
                   ::rate-sources  {}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :route-segment ["rate-sources"]
   :query         [{::form (comp/get-query u.f.create-rate-source/CreateRateSourceForm)}
                   {::rate-sources (comp/get-query u.index-rate-sources/IndexRateSources)}
                   {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                   [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/page
     (bulma/box
      (dom/h1
       (tr [:index-rates "Index Rate Sources"])
       (u.buttons/ui-show-form-button toggle-button))
      (when shown? (u.f.create-rate-source/ui-create-rate-source-form form))
      (dom/hr)
      (u.index-rate-sources/ui-index-rate-sources rate-sources)))))

(def ui-page (comp/factory IndexRateSourcesPage))
