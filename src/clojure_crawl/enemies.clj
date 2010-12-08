(ns clojure-crawl.enemies
  (:use clojure-crawl.actors
	clojure-crawl.skills
	clojure-crawl.utils)
  (:import (clojure-crawl.actors Enemy))
  (:require [clojure [string :as string]]))

(defn new-enemy [type level]
  (let [kind (type *enemies*)
	basehe (:health kind)
	basema (:magic kind)
	st (* (+ level 4) (:strength kind))
	ag (* (+ level 4) (:agility kind))
	he (* (+ level 4) basehe)
	ma (* (+ level 4) basema)
	life (+ (* (dec level) 15 basehe)
		(* 10 basehe))
	mana (+ (* (dec level) 2 basema)
		(* 10 basema))]
    (new Enemy (:name kind) (:description kind) st ag he ma
	 (vec (map #(% *skills*) (:skills kind)))
	 (atom life) life (atom mana) mana level (:drop kind))))

(def *enemies*
     {:orc {:name "Orc" :description "Brutal green-skinned warrior"
	    :strength 7 :agility 3 :health 5 :magic 3
	    :skills [:crush] :drop nil}
      :ninja {:name "Ninja" :description "Deadly warrior"
	      :strength 5 :agility 7 :health 5 :magic 3
	      :skills [:critical-strike] :drop nil}
      :fallen-priest {:name "Fallen Priest" :description "Unholy cleric"
		      :strength 4 :agility 4 :health 6 :magic 5
		      :skills [:heal] :drop nil}
      :sorcerer {:name "Sorcerer" :description "Evil mage"
		 :strength 3 :agility 5 :health 4 :magic 7
		 :skills [:fireball] :drop nil}})

(defn random-enemy [level]
  (let [k (rand-nth (keys *enemies*))]
    (new-enemy k level)))

;; helper
(defn show-enemy [enemy]
  (str "===== " (:name enemy) " =====\n"
       "description: " (:description enemy) "\n"
       "level: " (:level enemy) "\n\n"
       "strength: " (:strength enemy) "\n"
       "agility: " (:agility enemy) "\n"
       "health: " (:health enemy) "\n"
       "magic: " (:magic enemy) "\n"
       "life: " @(:life enemy) "/" (:max-life enemy) "\n"
       "mana: " @(:mana enemy) "/" (:max-mana enemy) "\n"
       "skills: " (string/join ", " (map :name (:skills enemy))) "\n"
       "attack: " (vec->damage (base-attack enemy)) "\n"
       "defense: " (to-num (defense enemy))))