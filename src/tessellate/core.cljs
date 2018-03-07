(ns tessellate.core
  (:require [reagent.core :as reagent :refer [atom]]
            [devtools.core :as core]))

(enable-console-print!)

(def board-size
  {:x 3
   :y 3})

(defn rotate
  ([s] (rotate 1 s))
  ([n s]
   (let [c (count s)]
     (take c
       (drop
         n
         (cycle s))))))

(defn new-board
  [board-size colours]
  (into {}
   (for [x (range (:x board-size))
         y (range (:y board-size))]
     [(gensym)
      {:coord {:x x
               :y y}
       :colour (rotate (rand-int (count colours)) colours)}])))

(defonce app-state (atom {:title "Let's trux it up!"
                          :board (new-board board-size ["red" "blue"])}))

(defn neighbours
  [id]
  (let [b (:board @app-state)
        t (id @app-state)]
    (filter
      (fn [[k v]]
        (= 1 (+
           (Math/abs (- (:x v) (:x t)))
           (Math/abs (- (:y v) (:y t)))))))))

(defn tile-click-handler
  [id]
  (swap! app-state update-in [:board id :colour] rotate))

(println "Hello?")

(defn render-tile
  [id t]
  (let [x (get-in t [:coord :x])
        y (get-in t [:coord :y])
        x-max (:x board-size)
        y-max (:y board-size)]
     [:rect
      {:key id
       :width (/ 0.95 x-max)
       :height (/ 0.95 y-max)
       :fill (-> t :colour first)
       :x (/ x x-max)
       :y (/ y y-max)
       :on-click (partial tile-click-handler id)}]))

(defn tesselate []
  [:center
   [:h1 (:title @app-state)]
   [:svg
    {:view-box "0 0 1 1"
     :width 256
     :height 256}
    (for [[k t] (:board @app-state)]
      (render-tile k t))]])

(reagent/render-component [tesselate]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (swap! app-state assoc-in [:board] (new-board board-size ["green" "blue"]))
  )
