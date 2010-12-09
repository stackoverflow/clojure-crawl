(ns clojure-crawl.classes
  (:use clojure-crawl.utils)
  (:require [clojure [string :as string]])
  (:import (clojure-crawl.actors Clazz)))

(def *classes*
     {:warrior (new Clazz "Warrior" "Fighter specialized in melee damage" 4 2 3 2 [:crush :attack-up])
      :rogue (new Clazz "Rogue" "Sneaky warrior specialized in critical, evade and hide"
		  3 4 2 2 [:critical-strike :sneak])
      :mage (new Clazz "Mage" "Master of magic with low damage and life" 2 3 2 4 [:fireball :magic-up])
      :cleric (new Clazz "Cleric" "Holy man with divine magic. Highly durable" 2 2 4 3 [:heal :vitality])})

(defn class-names []
  (map :name (vals *classes*)))

(defn show-class [name]
  (let [clazz (first (filter #(= (:name %) name) (vals *classes*)))
	s (:strength clazz)
	a (:agility clazz)
	h (:health clazz)
	m (:magic clazz)]
    (str "class: " (:name clazz) "\n"
	 (:description clazz) "\n"
	 "strength: " (* 5 s) ", strength increment: " s "\n"
	 "agility: " (* 5 a) ", agility increment: " a "\n"
	 "health: " (* 5 h) ", health increment: " h "\n"
	 "magic: " (* 5 m) ", magic increment: " m "\n"
	 "skills: " (string/join ", " (map key->name (:skills clazz))))))