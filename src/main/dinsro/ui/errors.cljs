(ns dinsro.ui.errors
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.mutations :as m]
   [lambdaisland.glogi :as log]))

(defn target-component-requests-errors
  [query path]
  ;; path can be a single keyword -> ignore
  (some->> (when (vector? path) (butlast path))
           (get-in query)
           meta
           :component
           (comp/get-query)
           (some #{:com.wsscode.pathom.core/errors})))

(defn extract-query-from-transaction
  "Extract the component query from a `result`.
  Ex. tx.: `[({:all-organizations [:orgnr ...]} params) ::p/errors]`,
  `[{:people [:orgnr ...]} ::p/errors]`"
  [original-transaction]
  (let [query (first original-transaction)]
    (cond-> query
      ;; A parametrized query is wrapped in (..) but we need the raw data query itself
      (list? query) (first))))

(defn unhandled-errors
  "Returns Pathom errors (if any) that are not handled by the target component

  The argument is the same one as supplied to Fulcro's `remote-error?`"
  [result]
  ;; TODO Handle RAD reports - their query is `{:some/global-resolver ..}` and it lacks any metadata
  (let [load-errs     (:com.wsscode.pathom.core/errors (:body result))
        query         (extract-query-from-transaction (:original-transaction result))]
    (log/trace :unhandled-errors/query {:query query})
    (let [mutation-sym  (as-> (-> query keys first) x
                          (when (sequential? x) (first x))
                          (when (symbol? x)
                            (log/trace :unhandled-errors/symbol {:x x :result result :load-errs load-errs :query query})
                            x)) ; join query => keyword
          mutation-errs (when mutation-sym
                          (log/trace :unhandled-errors/mutation-errors {:mutation-sym mutation-sym})
                          (get-in result [:body mutation-sym :com.fulcrologic.rad.pathom/errors]))]
      (cond
        (seq load-errs)
        (reduce
         (fn [unhandled-errs [path :as entry]]
           (if (target-component-requests-errors query path)
             (do
               (log/trace :unhandled-errors/ignored {:last-path (last path)})
               unhandled-errs)
             (conj unhandled-errs entry)))
         {}
         ;; errors is a map of `path` to error details
         load-errs)

        mutation-errs
        (do
          (log/trace :unhandled-errors/mutation-errors {:mutation-errs mutation-errs})
          mutation-errs)

        :else
        nil))))

(defn contains-error? [result]
  (seq (unhandled-errors result)))

(defn component-handles-mutation-errors? [component]
  (boolean (some-> component comp/get-query set ::m/mutation-error)))

(defn global-error-action
  "Run when app's :remote-error? returns true"
  [{:keys [component state], {:keys [body status-code error-text]} :result}]
  (log/error :global-error-action/starting
             {:component   component
              :state       state
              :body        body
              :status-code status-code
              :error-text  error-text})
  (when-not (component-handles-mutation-errors? component)
    (let [msg (first
               (map
                (fn [body]
                  (let [pathom-errs (:com.fulcrologic.rad.pathom/errors body)]
                    (cond
                      (and (string? error-text) status-code (> status-code 299))
                      (cond-> error-text (and status-code (> status-code 299)) (str " - " body))

                      pathom-errs
                      (->> pathom-errs
                           (map (fn [[query {{:keys [message data]} :com.fulcrologic.rad.pathom/errors :as val}]]
                                  (str query " failed with "
                                       (or (and message (str message (when (seq data) (str ", extra data: " data)))) val))))
                           (string/join " | "))

                      :else
                      (str body))))
                (vals body)))]
      (swap! state assoc :ui/global-error msg))))
