(ns clojure-crawl.map)

(def *enemy-chance* 25)

(def *max-rooms* 40)

(def *shrine-chance-multiplier* 3)

(def current-room (atom nil))

(def current-level (atom nil))

(def dungeon (atom []))

(defrecord Level [start depth count])

(defrecord Room [right left front rear shrine? up? down? enemy treasure])

(defn- probability-result [n]
  (< n (inc (rand-int 100))))

;(defn- gen-map [start? rigth left front rear]
;  (let [depth (if start? 1 @(:count current-level))
;	shrine% (* *shrine-chance-multiplier* depth)
;	shrine? (probability-result shrine%)
;	down? (probability-result 10)
;	map (new Map )]))

;(defn- gen-level []
;  (let [depth (inc @(count dungeon))
;	nproced (atom [{[100 100] ])))))