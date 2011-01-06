(ns clojure-crawl.things
  (:use clojure-crawl.utils)
  (:require [clojure [string :as string]]))

(defrecord Effect [strength agility health magic attack defense critical evade life mana life-regen mana-regen hide skills expires])

(defrecord AFix [name type level effect])

(defrecord Item [name type kind prefix suffix level effect])

(defn- show-valid
  ([key effect]
     (show-valid key effect nil))
  ([key effect add]
     (let [v (key effect)]
       (if (and v (not= v 0))
	 (str (if (pos? v) "+" "-") v add " to " (key->name key) "\n")
	 nil))))

(defn create-effect
  ([desc] (create-effect desc nil))
  ([desc expiration]
     (new Effect (:strength desc) (:agility desc) (:health desc) (:magic desc)
	  (:attack desc) (:defense desc) (:critical desc) (:evade desc)
	  (:life desc) (:mana desc) (:life-regen desc) (:mana-regen desc)
	  (:hide desc) (:skills desc) expiration)))

(defn show-effect [effect]
  (str (show-valid :strength effect)
       (show-valid :agility effect)
       (show-valid :health effect)
       (show-valid :magic effect)
       (show-valid :attack effect)
       (show-valid :defense effect)
       (show-valid :critical effect "%")
       (show-valid :evade effect "%")
       (show-valid :life effect)
       (show-valid :mana effect)
       (let [lr (:life-regen effect)
	     mr (:mana-regen effect)]
	 (str (if (and lr (not= lr 0))
		(str (to-num (/ (inc lr) 5)) " life regen/second\n")
		nil)
	      (if (and mr (not= mr 0))
		(str (to-num (/ (inc mr) 20)) " mana regen/second\n")
		nil)))
       (show-valid :hide effect "%")
       (let [sks (:skills effect)]
	 (if sks
	   (str "grant skills: " (string/join ", " (filter key->name sks)))
	   nil))))