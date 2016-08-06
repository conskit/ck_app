(ns {{name}}.handlers
    (:require [re-frame.core :as re-frame]
              [{{name}}.config :as config]
              [{{name}}.routing :as routing]))

(re-frame/register-handler
 :initialize-db
 [config/standard-middlewares]
 (fn  [_ [state]]
   state))

(re-frame/register-handler
  :change-to-last-name
  [config/standard-middlewares]
  (fn  [db _]
    (routing/navigate :{{name}}.core/hello-page {:foo :bar} false nil)
    (assoc-in db [1 :data :firstname] (get-in db [1 :data :lastname]))))
