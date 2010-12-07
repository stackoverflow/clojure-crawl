(ns clojure-crawl.game
  (:use clojure-crawl.races
	clojure-crawl.skills
	clojure-crawl.classes
	clojure-crawl.actors)
  (:import (clojure-crawl.actors Player))
  (:require [clojure [string :as string]]))

(defn- add-race-bonus [item race value]
  (+ (item (:effect race)) value))

(defn new-player [name race clazz]
  (let [r (race *races*)
	c (clazz *classes*)
	health (add-race-bonus :health r (* 5 (:health c)))
	magic (add-race-bonus :magic r (* 5 (:magic c)))
	life (add-race-bonus :life r (+ 20 (* 2 health)))
	mana (add-race-bonus :mana r (* 2 magic))]
    (new Player name r (atom c)
	 (atom (add-race-bonus :strength r (* 5 (:strength c))))
	 (atom (add-race-bonus :agility r (* 5 (:agility c))))
	 (atom health)
	 (atom magic)
	 (atom (vec (map #(% *skills*) (:skills c)))) ;skills
	 (atom 0) ;exp
	 (atom life)
	 (atom life) ;max life
	 (atom mana)
	 (atom mana) ;max mana
	 (atom 1) ; level
	 (atom []) ;equip
	 (atom []) ;bag
	 (atom [])))) ;effects

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