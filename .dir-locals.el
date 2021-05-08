((nil .
   ((indent-tabs-mode . nil)       ; always use spaces for tabs
     (require-final-newline . t)    ; add final newline on save
     ;; (cider-shadow-default-options . "node-repl")
     (cider-default-cljs-repl . shadow)))
  (clojure-mode .
    ((eval .
       (progn
         (define-clojure-indent
           (>defn 1)
           (a 1)
           (behavior 1)
           (button 1)
           (div 1)
           (footer 1)
           (h2 1)
           (input 1)
           (nav 1)
           (select 1)
           (specification 1)
           (table 1)
           (tbody 1)
           (thead 1)
           (tr 1)
           (ui-modal-content 1))))
      (clojure-align-forms-automatically t)
      (clojure-indent-style . always-align))
    ;; (clojure-defun-indents
    ;;   '(describe describe-config it fact facts future-fact future-facts
    ;;      Given When Then context GET POST DELETE fn-traced >defn specification
    ;;      behavior ui-dropdown ui-dropdown-menu ui-modal-content div table h2))

    ))
