(ns {{name}}.services.socket
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [clojure.tools.logging :as log]
            [conskit.macros :refer [definterceptor defcontroller action]]
            [ring.middleware.defaults]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [conskit.protocols :as ckp]))

(def ring-defaults
  (-> ring.middleware.defaults/site-defaults
      (assoc :session {:cookie-name  "_rs"
                       :root         "/"
                       :cookie-attrs {:http-only true}
                       :store        (cookie-store)})))

(defcontroller
  socket-ctrl
  []
  (action
    ^{:route "/chsk"
      :no-csrf true
      :socket-end-point true}
    endpoint
    [req]
    (identity req)))

(definterceptor
  ^:socket-end-point
  socket-end-point
  ""
  [f config #{ajax-post-fn ajax-get-or-ws-handshake-fn}]
  (let [fn-post (ring.middleware.defaults/wrap-defaults ajax-post-fn ring-defaults)
        fn-get (ring.middleware.defaults/wrap-defaults ajax-get-or-ws-handshake-fn ring-defaults)]
    (if config
      (fn [req]
        (condp = (:request-method req)
          :post (fn-post req)
          :get (fn-get req))))))

(definterceptor
  ^:socket
  socket
  [f config req]
  (if config
    (f (:socket-data req))
    (f req)))

(definterceptor
  ^:ring-default
  ring-default
  ""
  [f config #{get-meta}]
  (if (:socket (get-meta))
    f
    (ring.middleware.defaults/wrap-defaults f ring-defaults)))


(defservice
  service
  [[:ActionRegistry select-meta-keys get-action register-bindings! register-interceptors! register-controllers!]
   [:ConfigService get-in-config]]
  (init [this context]
        (log/info "Initializing Socket Service")
        (register-controllers! [socket-ctrl] [[ring-default {:except :no-defaults}]])
        (register-interceptors! [socket-end-point
                                 socket])
        (let [bindings (sente/make-channel-socket! (get-sch-adapter) {:packer :edn
                                                                      :user-id-fn (fn [_]
                                                                                    (rand-int 100))})]
          (register-bindings! bindings)
          (assoc context :bindings bindings)))
  (start [this context]
         (log/info "Starting Socket Service")
         (let [handlers (into [] (map :id (filter #(not (nil? (:socket %)))
                                                  (select-meta-keys [:socket :id]))))]
           (sente/start-server-chsk-router!
             (get-in context [:bindings :ch-recv])
             (fn [{:keys [id ?data ?reply-fn ring-req]}]
               (let [action (get-action id)]
                 (if-let [reply ?reply-fn]
                   (if (some #{id} handlers)
                     (reply (ckp/invoke action (assoc ring-req :socket-data ?data)))
                     (reply {::error "Action not found"}))
                   (when (some #{id} handlers)
                     (ckp/invoke action (assoc ring-req :socket-data ?data))))))))
         context))
