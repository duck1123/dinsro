(ns dinsro.views.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate-source :as u.f.create-rate-source]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [taoensso.timbre :as timbre]))

(defsc IndexRateSourcesPage
  [_this {::keys [form rate-sources toggle-button]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::form           {}
                   ::rate-sources   {}
                   ::toggle-button  {}}
   :query [{::form          (comp/get-query u.f.create-rate-source/CreateRateSourceForm)}
           {::rate-sources  (comp/get-query u.index-rate-sources/IndexRateSources)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]
   :route-segment ["rate-sources"]}
  (let [shown? false]
    (bulma/section
     (bulma/container
      (bulma/content
       (bulma/box
        (dom/h1
         (tr [:index-rates "Index Rate Sources"])
         (u.buttons/ui-show-form-button toggle-button))
        (when shown?
          (u.f.create-rate-source/ui-create-rate-source-form form))
        (dom/hr)
        (u.index-rate-sources/ui-index-rate-sources rate-sources)))))))

(def ui-page (comp/factory IndexRateSourcesPage))
