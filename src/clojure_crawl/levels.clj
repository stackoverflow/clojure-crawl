(ns clojure-crawl.levels
  (:use clojure-crawl.actors))

(def *xp-table*
     (for [x (range 1 100)] (* x x 10)))

(defn to-next-level [player]
  (let [xp @(:exp player)
	level @(:level player)]
    (- (nth *xp-table* (dec level)) xp)))

(defn next-level-xp [player]
  (let [level @(:level player)]
    (nth *xp-table* (dec level))))

(defn leveled? [player]
  (and (< @(:level player) 99)
       (<= (to-next-level player) 0)))

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
    (reset! (:max-mana player) @(:mana player))))

(defn xp-for-level [level] level)

;; skills

(def *skill-xp-table*
     (for [x (range 1 10)] (+ 20 (* 20 x))))

(defn skill-xp-for-level [] 1)

(defn skill-to-next-level [skill]
  (let [xp @(:exp skill)
	level @(:level skill)]
    (- (nth *skill-xp-table* (dec level)) xp)))

(defn skill-next-level-xp [skill]
  (let [level @(:level skill)]
    (nth *skill-xp-table* (dec level))))

(defn skill-leveled? [skill]
  (and (< @(:level skill) 10)
       (<= (skill-to-next-level skill) 0)))

(defn skill-level-up [skill]
  (reset! (:exp skill) 0)
  (swap! (:level skill) inc))