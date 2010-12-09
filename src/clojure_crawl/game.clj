(ns clojure-crawl.game
  (:use clojure-crawl.utils
	clojure-crawl.actors
	clojure-crawl.races
	clojure-crawl.classes
	clojure-crawl.skills)
  (:import (clojure-crawl.actors Player))
  (:require [clojure [string :as string]]
	     [clojure-crawl [map :as gamemap]]))

(defrecord Game [player dungeon current-level current-room])

(def game (new Game (atom nil) (atom nil) (atom nil) (atom nil)))

(defprotocol IGame
  (set-player [game player])
  (set-dungeon [game dungeon])
  (set-current-level [game current-level])
  (set-current-room [game current-room]))

(extend-type Game
  IGame
  (set-player [game player]
	      (reset! (:player game) player))
  (set-dungeon [game dungeon]
	      (reset! (:dungeon game) dungeon))
  (set-current-level [game current-level]
	      (reset! (:current-level game) current-level))
  (set-current-room [game current-room]
	      (reset! (:current-room game) current-room)))

(defn start-game [pname race clazz]
  (let [player (new-player pname (name->key race) (name->key clazz))
	level (gamemap/enter-dungeon)]
    (set-player game player)
    (set-dungeon game gamemap/dungeon)
    (set-current-level game level)
    (set-current-room game gamemap/current-room)))

;; create player

(defn- add-race-bonus [item race value]
  (+ (item (:effect race)) value))

(defn- life-skill-bonus [skills actor]
  (let [pass (filter #(not (:active? %)) skills)]
    (reduce + (map #(skill-life % actor) skills))))

(defn- mana-skill-bonus [skills actor]
  (let [pass (filter #(not (:active? %)) skills)]
    (reduce + (map #(skill-mana % actor) skills))))

(defn new-player [name race clazz]
  (let [r (race *races*)
	c (clazz *classes*)
	skills (vec (map #(% *skills*) (:skills c)))
	health (add-race-bonus :health r (* 5 (:health c)))
	magic (add-race-bonus :magic r (* 5 (:magic c)))
	life (add-race-bonus :life r (+ 20 (* 2 health)))
	mana (add-race-bonus :mana r (* 2 magic))
	player (new Player name r (atom c)
		    (atom (add-race-bonus :strength r (* 5 (:strength c))))
		    (atom (add-race-bonus :agility r (* 5 (:agility c))))
		    (atom health)
		    (atom magic)
		    (atom skills)
		    (atom 0) ;exp
		    (atom life)
		    (atom life) ;max life
		    (atom mana)
		    (atom mana) ;max mana
		    (atom 1) ; level
		    (atom []) ;equip
		    (atom []) ;bag
		    (atom []))] ;effects
    (swap! (:life player) + (life-skill-bonus skills player))
    (swap! (:mana player) + (mana-skill-bonus skills player))
    player))

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