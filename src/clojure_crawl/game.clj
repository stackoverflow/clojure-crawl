(ns clojure-crawl.game
  (:use clojure-crawl.utils))

(defrecord Game [player dungeon current-level current-room])

;; helpers
(defn show-player [player]
  (str "===== " (:name player) " =====\n"
       "race: " (:name (:race player)) "\n"
       "class: " (:name @(:clazz player)) "\n\n"
       "strength: " (strength player) "\n"
       "agility: " (agility player) "\n"
       "health: " (health player) "\n"
       "magic: " (magic player) "\n"
       "life: " @(:life player) "/" (max-life player) "\n"
       "mana: " @(:mana player) "/" (max-mana player) "\n"
       "life regen: " (double (life-regen player)) " per second\n"
       "mana regen: " (double (mana-regen player)) " per second\n"
       "skills: " (string/join ", " (map :name @(:skills player)))))

(defn- target->str [target]
  (cond (= target :enemy)
	"enemy"
	(= target :self)
	"self"
	:default "-"))

(defn- passive-skill-desc->str [desc]
  (let [f (fn [[k v]]
	    (let [value (if (or (= k :hide)
				(= k :critical)
				(= k :evade))
			  (str (to-num v) "%")
			  (to-num v))]
	      (str "adds " value " to " (key->name k) "\n")))]
    (apply str (map f desc))))

(defn- active-skill-desc->str [desc]
  (let [f (fn [[k v]]
	    (cond (= k :attack)
		  (str "damage: " (to-num (first v)) " - " (to-num (second v)) "\n")
		  (= k :critical)
		  (str "critical chance: " (to-num v) "%\n")
		  (= k :mana)
		  (str "mana: " v "\n")))]
    (apply str (map f desc))))

(defn- skill-desc->str [desc active]
  (if active
    (active-skill-desc->str desc)
    (passive-skill-desc->str desc)))

(defn show-skill [name player]
  (let [skill (first (filter #(= name (:name %)) @(:skills player)))
	desc (describe-skill skill player)]
    (str "===== " (:name skill) " =====\n"
	 (:description skill) "\n"
	 (if (:active? skill)
	   (str "level: " @(:level skill) "\n"))
	 "active: " (if (:active? skill) "yes" "no") "\n"
	 "only in battle: " (if (:only-in-battle? skill) "yes" "no") "\n"
	 "target: " (target->str (:target skill)) "\n"
	 (skill-desc->str desc (:active? skill)))))