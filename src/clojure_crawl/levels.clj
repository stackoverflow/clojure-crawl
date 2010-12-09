(ns clojure-crawl.levels
  (:use clojure-crawl.actors))

(def *xp-table*
     (for [x (range 1 100)] (* x x 10)))

(defn to-next-level [player]
  (let [xp @(:exp player)
	level @(:level player)]
    (- (nth *xp-table* (dec level)) xp)))

(defn leveled? [player]
  (<= (to-next-level player) 0))

(defn level-up [player]
  (let [clazz @(:clazz player)]
    (swap! (:level player) inc)
    (swap! (:strength player) + (:strength clazz))
    (swap! (:agility player) + (:agility clazz))
    (swap! (:health player) + (:health clazz))
    (swap! (:magic player) + (:magic clazz))
    (swap! (:life player) + (* 15 (:health clazz)))
    (reset! (:max-life player) @(:life player))
    (swap! (:mana player) + (* 2 (:magic clazz)))
    (reset! (:max-mana player) @(:mana player))
    (reset! (:exp player) 0)))