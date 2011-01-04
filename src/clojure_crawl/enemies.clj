(ns clojure-crawl.enemies
  (:use clojure-crawl.actors
	clojure-crawl.skills
	clojure-crawl.utils)
  (:import (clojure-crawl.actors Enemy))
  (:require [clojure [string :as string]]))

(def *enemies*
     {:orc {:name "Orc" :description "Brutal green-skinned warrior"
	    :strength 6 :agility 3 :health 5 :magic 3
	    :skills [:crush] :drop nil}
      :ninja {:name "Ninja" :description "Deadly warrior"
	      :strength 5 :agility 6 :health 5 :magic 3
	      :skills [:critical-strike] :drop nil}
      :fallen-priest {:name "Fallen Priest" :description "Unholy cleric"
		      :strength 4 :agility 4 :health 6 :magic 5
		      :skills [:heal] :drop nil}
      :sorcerer {:name "Sorcerer" :description "Evil mage"
		 :strength 3 :agility 5 :health 4 :magic 6
		 :skills [:fireball] :drop nil}})

;; ai actions
(defmethod ai-action "Orc" [enemy player]
  (let [plife @(:life player)
	pmaxlife @(:max-life player)
	plifeperc (/ (* 100 plife) pmaxlife)
	skill (first (:skills enemy))]
    (if (and (can-use enemy skill)
	     (> plifeperc 50))
      {:skill skill}
      {:attack true})))

(defmethod ai-action "Ninja" [enemy player]
  (let [plife @(:life player)
	pmaxlife @(:max-life player)
	plifeperc (/ (* 100 plife) pmaxlife)
	skill (first (:skills enemy))]
    (if (and (can-use enemy skill)
	     (< plifeperc 50))
      {:skill skill}
      {:attack true})))

(defmethod ai-action "Fallen Priest" [enemy player]
  (let [life @(:life enemy)
	maxlife @(:max-life enemy)
	lifeperc (/ (* 100 life) maxlife)
	skill (first (:skills enemy))]
    (if (and (can-use enemy skill)
	     (< lifeperc 25))
      {:skill skill}
      {:attack true})))

(defmethod ai-action "Sorcerer" [enemy player]
  (let [skill (first (:skills enemy))]
    (if (can-use enemy skill)
      {:skill skill}
      {:attack true})))

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
	 (vec (map #(new-skill (% *skills*) level) (:skills kind)))
	 (atom life) life (atom mana) mana level (:drop kind) (atom false))))

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