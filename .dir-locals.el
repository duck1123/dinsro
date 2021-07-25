((nil .
   ((indent-tabs-mode . nil)
     (require-final-newline . t)))
  (clojure-mode .
    ((eval .
       (progn
         (define-clojure-indent
           (>defn 1)
           (a 1)
           (behavior 1)
           (button 1)
           (describe 1)
           (div 1)
           (footer 1)
           (h2 1)
           (input 1)
           (it 1)
           (nav 1)
           (select 1)
           (specification 1)
           (table 1)
           (tbody 1)
           (thead 1)
           (tr 1)
           (ui-form 1)
           (ui-form-field 1)
           (ui-modal-content 1))))
      (clojure-align-forms-automatically t)
      (clojure-indent-style . always-align))))
