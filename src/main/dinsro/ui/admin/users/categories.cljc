(ns dinsro.ui.admin.users.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../model/categories.cljc]]
;; [[../../../mutations/categories.cljc]]
;; [[../../../ui/categories.cljs]]
;; [[../../../../../test/dinsro/ui/admin/users/categories_test.cljs]]

(def index-page-key :admin-users-show-categories)
(def model-key ::m.categories/id)
(def parent-model-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(def override-report false)
(def override-row false)
(def show-controls true)

(def create!-form-button
  {:type   :button
   :local? true
   :label  "Create"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [`(mu.categories/create! ~props)])))})

(form/defsc-form NewForm2
  [_this _props]
  {fo/action-buttons [::create!]
   fo/attributes     [m.categories/name]
   fo/controls       {::create! create!-form-button}
   fo/id             m.categories/id
   fo/route-prefix   "new-category"
   fo/title          "Create Category"})

(defsc NewForm
  [this {::m.categories/keys [name]
         :as                 props}]
  {:ident         (fn [] [:component/id ::NewForm])
   :initial-state {::m.categories/name ""}
   :query         [::m.categories/name]}
  (log/debug :NewForm/starting {:props props})
  (ui-segment {}
    (ui-form {}
      (ui-form-field {}
        (ui-form-input
         {:value    name
          :onChange (fn [evt _] (fm/set-string! this ::m.categories/name :event evt))
          :label    "Name"}))
      (ui-form-field {}
        (ui-button
         {:content "Submit"
          :primary true
          :fluid   true
          :size    "large"
          :onClick
          (fn [_ev]
            (comp/transact! this [`(mu.categories/create! {::m.categories/name ~name})]))})))))

(def ui-new-form (comp/factory NewForm))

(declare Report)

(defsc DeleteButton
  [this props]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id nil}
   :query         [::m.categories/id]}
  (ui-button
   {:icon     "trash"
    :negative true
    :onClick  (fn [_] (comp/transact! this [`(mu.categories/delete! ~props)]))}))

(def ui-delete-button (comp/factory DeleteButton))

(defsc BodyItem
  [this {::j.categories/keys [transaction-count]
         :as                 props}]
  {:ident         ::m.categories/id
   :query         [::m.categories/id
                   ::m.categories/name
                   ::j.categories/transaction-count]
   :initial-state {::m.categories/id                nil
                   ::m.categories/name              ""
                   ::j.categories/transaction-count 0}}
  (if override-row
    (report/render-row this Report props)
    (dom/div :.ui.item.segment
      (dom/div :.header
        (u.links/ui-category-link props))
      (dom/div :.meta
        (dom/div {}
          (str "Transactions: " transaction-count))
        (dom/div {}
          (ui-delete-button {}))))))

(def ui-body-item (comp/factory BodyItem {:keyfn model-key}))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [this props]
  {ro/BodyItem         BodyItem
   ro/columns          [m.categories/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::new        new-button
                        ::refresh    u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.categories/admin-index
   ro/title            "Categories"}
  (log/info :Report/starting {:props props})
  (let [{:ui/keys [current-rows]} props]
    (if override-report
      (report/render-layout this)
      (ui-segment {}
        (when show-controls
          ((report/control-renderer this) this))
        (dom/div {}
          (dom/div {}
            (map ui-body-item current-rows)))))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [form report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/form         (comp/get-initial-state NewForm {})
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/form (comp/get-query NewForm)}
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["categories"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (dom/div {}
        (ui-new-form form)
        (ui-report report))
      (u.debug/load-error props "admin user categories report"))
    (u.debug/load-error props "admin user categories page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Categories"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-users-show
   ::m.navlinks/router        :admin-users
   ::m.navlinks/required-role :admin})
