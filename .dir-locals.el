((nil .
   ((indent-tabs-mode . nil)
     (require-final-newline . t)))
  (clojure-mode .
    ((eval .
       (progn
         (define-clojure-indent
           (>def 1)
           (>defn 1)
           (a 1)
           (action 1)
           (ANY 2)
           (behavior 1)
           (button 1)
           (code 1)
           (concat-when 1)
           (debug 1)
           (defroutes 'defun)
           (describe 1)
           (div 1)
           (dd 1)
           (DELETE 2)
           (dl 1)
           (error-action 1)
           (footer 1)
           (form 1)
           (HEAD 2)
           (GET 2)
           (get-initial-state 1)
           (h1 1)
           (h2 1)
           (info 1)
           (input 1)
           (it 1)
           (li 1)
           (nav 1)
           (ok-action 1)
           (OPTIONS 2)
           (p 1)
           (page-merger 1)
           (PATCH 2)
           (POST 2)
           (PUT 2)
           (remote 1)
           (route-deferred 1)
           (select 1)
           (span 1)
           (specification 1)
           (table 1)
           (tbody 1)
           (td 1)
           (thead 1)
           (tr 1)
           (trace 1)
           (transact! 1)
           (ui-breadcrumb 1)
           (ui-breadcrumb-section 1)
           (ui-breadcrumbs 1)
           (ui-button-group 1)
           (ui-container 1)
           (ui-form 1)
           (ui-form-field 1)
           (ui-grid 1)
           (ui-grid-column 1)
           (ui-grid-row 1)
           (ui-list-item 1)
           (ui-menu 1)
           (ui-menu-menu 1)
           (ui-modal 1)
           (ui-modal-content 1)
           (ui-moment 1)
           (ui-segment 1)
           (ui-sidebar-pushable 1)
           (ui-sidebar-pusher 1)
           (ui-table 1)
           (ui-table-body 1)
           (ui-table-cell 1)
           (ui-table-header 1)
           (ui-table-header-cell 1)
           (ui-table-row 1)
           (ul 1))))
      (clojure-align-forms-automatically t)
      (clojure-indent-style . always-align))))
