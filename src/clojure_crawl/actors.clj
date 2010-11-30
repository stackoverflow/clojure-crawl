(ns clojure-crawl.actors
  (:use clojure-crawl.utils))

(defrecord Race [name description effect])

(defrecord Clazz [name description strength agility health magic skills])

(defrecord Skill [name description only-in-battle? active? level target effect])

(defprotocol Skillable
  (attack [skill actor])
  (critical [skill actor]))

(defrecord Player [name race clazz strength agility health magic skills exp life max-life mana max-mana life-regen mana-regen level equip bag effects])

(defrecord Enemy [name description clazz strength agility health magic skills life max-life mana max-mana level])

(defprotocol Actor
  (base-attack [actor])
  (attack [actor])
  (defense [actor])
  (critical [actor])
  (evade [actor])
  (life-regen [actor])
  (mana-regen [actor])
  (hide [actor]))

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
  (defense [enemy]
    (let [s (:strength enemy)
	  h (:health enemy)]
      (/ (+ s h) 10)))
  (critical [enemy]
	    (let [a (:agility enemy)]
	      (* a 0.08)))
  (evade [enemy]
	 (let [a (:agility enemy)]
	   (* a 0.06))))

;;; player helper functions

(defn- race-bonus [key player]
  (key (:effect (:race player))))

(defn- effect-bonus [key player]
  (reduce + (map key @(:effects player))))

(defn- equip-bonus [key player]
  (reduce + (map key (map :effect @(:equip player)))))

(defn- skill-bonus [key player]
  (let [passives (filter #(not (:active? %)) @(:skills player))]
    (reduce + (map key (map :effect passives)))))

(defn- all-bonus [key player]
  (+ (race-bonus key player)
     (effect-bonus key player)
     (equip-bonus key player)
     (skill-bonus key player)))

(extend-type Player
  Actor
  (base-attack [player]
	       (let [bonus (all-bonus :attack player)
		     s @(:strength player)]
		 [(+ (/ s 3) bonus) (+ (/ s 2) bonus)]))
  (attack [player]
	  (let [bonus (all-bonus :attack player)
		s @(:strength player)
		s3 (double (/ s 3))
		s2 (double (/ s 2))]
	    (rand-between (+ s3 bonus) (+ s2 bonus))))
  (defense [player]
    (let [bonus (all-bonus :defense player)
	  s @(:strength player)
	  h @(:health player)]
      (+ (/ (+ s h) 10) bonus)))
  (critical [player]
	    (let [bonus (all-bonus :critical player)
		  a @(:agility player)]
	      (+ (* a 0.08) bonus)))
  (evade [player]
	 (let [bonus (all-bonus :evade player)
	       a @(:agility player)]
	   (+ (* a 0.06) bonus)))
  (life-regen [player]
	      (let [bonus (all-bonus :life-regen player)
		    level @(:level player)]
		(+ (/ level 5) bonus)))
  (mana-regen [player]
	      (let [bonus (all-bonus :mana-regen player)
		    level @(:level player)]
		(+ (/ level 20) bonus)))
  (hide [player] (all-bonus :hide player)))