(ns clojure-crawl.actors
  (:use [clojure-crawl.utils]))

(defrecord Race [name description effect])

(defrecord Clazz [name description strength agility health magic skills])

(defrecord Player [name race clazz strength agility health magic skills exp life max-life mana max-mana life-regen mana-regen level equip bag])

(defrecord Enemy [name description clazz strength agility health magic skills life max-life mana max-mana level])

(defprotocol Actor
  (base-attack [player])
  (attack [player])
  (defend [player])
  (critical [player])
  (evade [player])
  (life-regen [player])
  (mana-regen [player])
  (hide [player]))

(extend-type Enemy
  Actor
  (base-atack [enemy]
	      (let [s (:strength enemy)]
		[(/ s 3) (/ s 2)]))
  (attack [enemy]
	  (let [s (:strength enemy)
		s3 (double (/ s 3))
		s2 (double (/ s 2))]
	    (rand-between s3 s2)))
  (defend [enemy]
    (let [s (:strength enemy)
	  h (:health enemy)]
      (/ (+ s h) 10)))
  (critical [enemy]
	    (let [a (:agility enemy)]
	      (* a 0.08)))
  (evade [enemy]
	 (let [a (:agility enemy)]
	   (* a 0.06))))