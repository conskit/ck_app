(ns {{name}}.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub-raw
 :name
 (fn [db]
   (reaction (get-in @db [1 :data :firstname]))))

(re-frame/reg-sub-raw
  :current-page
  (fn [db]
    (reaction (first @db))))
