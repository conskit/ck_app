(ns {{name}}.core
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [puppetlabs.trapperkeeper.services :refer [get-service service-context]]
            [clojure.tools.logging :as log]
            [conskit.macros :refer [defcontroller action]]
            [ck.routing.bidi]
            [ck.server.http-kit]
            [ck.migrations.flyway]
            [hugsql.core :as hug]
            [hiccup.page :as h]
            [ring.util.response :as r]
            [ck.react-server :as ckrs]))

(defn template
  [rendered-html meta state]
  (h/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:title (:title meta)]]
    [:body
     [:div#app rendered-html]
     [:script#app-state {:type "application/edn"} state]
     (h/include-js "/js/compiled/app.js")
     [:script "{{sanitized}}.core.init()"]]))

(defn map-of-db-bindings
  "Creates a binding map from hugsql db fns"
  [file conn]
  (->> (hug/map-of-db-fns file)
       (map (fn [[k v]] [k (partial (:fn v) conn)]))
       (into {})))

(defn bidify
  [routes]
  ["" (for [r routes
            :let [{:keys [id route]} r]]
        [route id])])

(defcontroller
  main-ctrlr
  [get-first-account get-routes]
  (action
    ^{:route "/"
      :react-server-page {:title "{{name}}"
                          :template-fn template}}
    hello-world
    [req]
    [:ck.react-server/ok (first (get-first-account))])
  (action
    ^{:route "/page"
      :react-server-page {:title "Page | {{name}}"
                          :template-fn template}}
    hello-page
    [req]
    [:ck.react-server/ok {:message "Hello There"}])
  (action
    ^{:route true
      :react-server-page {:title "Not Found | {{name}}"
                          :template-fn template}}
    not-found
    [req]
    [:ck.react-server/not-found {}])
  (action
    ^{:socket true}
    routes
    [data]
    (str (bidify (get-routes))))
  (action
    ^{:route #"/js.*"}
    scripts
    [req]
    (r/resource-response (:uri req) {:root "public"})))


(defservice
  service
  [[:ConfigService get-in-config]
   [:ActionRegistry register-controllers! register-bindings! register-interceptors!]
   [:CKServer register-handler!]
   [:CKMigration migrate!]
   [:CKReactServer get-render-fn]
   [:CKRouter get-routes]]
  (init [this context]
        (log/info "Initializing Application")
        (register-controllers! [main-ctrlr])
        (register-interceptors! [ckrs/react-server-page])
        (register-bindings! (merge
                              (map-of-db-bindings "db/sql/account.sql" (get-in-config [:database]))
                              {:get-render-fn get-render-fn
                               :get-routes    get-routes}))
        (register-handler! :http-kit :bidi)
        (migrate! :flyway :database)
        context)
  (start [this context]
         (log/info "Start Application")
         context)
  (stop [this context]
        (log/info "Stopping Application")
        context))