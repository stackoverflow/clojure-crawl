(ns clojure-crawl.actors
  (:use clojure-crawl.utils)
  (:require [clojure [string :as string]]))

(defrecord Race [name description effect])

(defrecord Clazz [name description strength agility health magic skills])

(defrecord Skill [name description only-in-battle? active? level target])

(defmulti describe-skill :name)
(defmulti mana-consume :name)
(defmulti skill-strength :name)
(defmulti skill-agility :name)
(defmulti skill-health :name)
(defmulti skill-magic :name)
(defmulti skill-attack :name)
(defmulti skill-defense :name)
(defmulti skill-critical :name)
(defmulti skill-evade :name)
(defmulti skill-life :name)
(defmulti skill-mana :name)
(defmulti skill-life-regen :name)
(defmulti skill-mana-regen :name)
(defmulti skill-hide :name)

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

(defprotocol Descriptable
  (describe [stuff]))

(defn- effect->str [key effect desc]
  (let [tattr (key effect)
	name (subs (str key) 1)
	attr (if (or (= name "critical")
		     (= name "evade")
		     (= name "hide"))
	       (str tattr "%")
	       (if (= name "life-regen")
		 (str tattr "/5 (per second)")
		 (if (= name "mana-regen")
		   (str tattr "/20 (per second)")
		   tattr)))]
    (when (not= attr 0)
      (add-to! desc (str "* " (if (pos? attr)
				(str "+" attr)
				attr)
			 " to " name)))))

(defn describe-effect [effect]
  (let [desc (atom [])]
    (effect->str :strength effect desc)
    (effect->str :agility effect desc)
    (effect->str :health effect desc)
    (effect->str :magic effect desc)
    (effect->str :attack effect desc)
    (effect->str :defense effect desc)
    (effect->str :critical effect desc)
    (effect->str :evade effect desc)
    (effect->str :life effect desc)
    (effect->str :mana effect desc)
    (effect->str :life-regen effect desc)
    (effect->str :mana-regen effect desc)
    (effect->str :hide effect desc)
    (doseq [sk (:skills effect)]
      (add-to! desc (str "* gives skill: " (:name sk))))
    (string/join "\n" @desc)))

(extend-type Race
  Descriptable
  (describe [race]
	    (describe-effect (:effect race))))

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
  (let [eq (reduce + (map key (map :effect @(:equip player))))
	pre (reduce + (map key (map :effect (map :prefix @(:equip player)))))
	suf (reduce + (map key (map :effect (map :suffix @(:equip player)))))]
    (+ eq pre suf)))

(defn- skill-bonus [key player]
  (let [passives (filter #(not (:active? %)) @(:skills player))
	keyname (subs (str key) 1)
	bonus (fn [sk]
		(eval-string (str "(skill-" keyname " " sk " " player ")")))]
    (reduce + (map key (map bonus passives)))))

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