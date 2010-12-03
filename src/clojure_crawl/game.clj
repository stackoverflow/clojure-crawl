(ns clojure-crawl.game
  (:use clojure-crawl.races
	clojure-crawl.skills
	clojure-crawl.classes)
  (:import (clojure-crawl.actors Player)))

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
	 1 ; level
	 (atom []) ;equip
	 (atom []) ;bag
	 (atom [])))) ;effects

(defn show-actor [actor]
  (str "===== " (:name actor) " =====\n"
       "race: " (:name (:race actor)) "\n"
       "class: " (:name @(:clazz actor)) "\n\n"
       "strength: " @(:strength actor) "\n"
       "agility: " @(:agility actor) "\n"
       "health: " @(:health actor) "\n"
       "magic: " @(:magic actor) "\n"
       "life: " @(:life actor) "/" @(:max-life actor) "\n"
       "mana: " @(:mana actor) "/" @(:max-mana actor) "\n"))